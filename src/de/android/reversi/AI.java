package de.android.reversi;

import java.util.List;

import de.android.reversi.logic.ReversiLogic;


public class AI {
    private final Player maxPlayer;

    /**
     * Disk-square table.
     */
    private final int[][] values = new int[][] {
            { 50, -1, 5, 2, 2, 5, -1, 50 },
            { -1, -10, 1, 1, 1, 1, -10, -1 },
            { 5, 1, 1, 1, 1, 1, 1, 5 },
            { 2, 1, 1, 0, 0, 1, 1, 2 },
            { 2, 1, 1, 0, 0, 1, 1, 2 },
            { 5, 1, 1, 1, 1, 1, 1, 5 },
            { -1, -10, 1, 1, 1, 1, -10, -1 },
            { 50, -1, 5, 2, 2, 5, -1, 50 } };


    private static final int depth = 3;


    public AI(final Player currentPlayer) {
        maxPlayer = currentPlayer;
    }

    /**
     * Alpha-beta pruning
     * @param node
     * @param depth
     * @param alpha
     * @param beta
     * @param player
     */
    private int minimaxAB (final Board board, final Position node, final int depth, int alpha, int beta,
            final Player player) {

        final List<Position> allowedPositions =  board.allowedPositions(player);
        if (depth == 0 || allowedPositions.isEmpty()) {
            return this.heuristic(board, player); //the heuristic value of node
        }
        if (player == maxPlayer) {
            for (final Position child : allowedPositions) {
                //TODO: This clone sucks. I must re design this program. :( I should just need the array :(
                //What I need is an undo movement function. In that way I do not need to clone/copy the whole board :(
                //see: http://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
                final Board newBoard = board.clone();
                newBoard.makeMove(player, child.getColumn(), child.getRow());
                newBoard.flipOpponentDiscs(child, player);
                final int val = minimaxAB (newBoard, child, depth -1, alpha, beta, ReversiLogic.opponent(player));
                if (val > alpha) {
                    alpha = val;
                }
                if (beta <= alpha) {
                    break;
                }
            }

            return alpha;
        }
        else {
            for (final Position child : allowedPositions) {
                //TODO: This clone sucks. I must re design this program. :( I should just need the array :(
                //What I need is an undo movement function. In that way I do not need to clone/copy the whole board :(
                //see: http://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
                final Board newBoard = board.clone();
                newBoard.makeMove(player, child.getColumn(), child.getRow());
                newBoard.flipOpponentDiscs(child, player);
                final int val = minimaxAB (newBoard, child, depth -1, alpha, beta, ReversiLogic.opponent(player));
                if (val < beta) {
                    beta = val;
                }
                if (beta <= alpha) {
                    break;
                }
            }

            return beta;
        }
    }

    public Position getBestMove(final Board board) {
        final List<Position> allowedPositions = board.allowedPositions(this.maxPlayer);
        int alpha = 0 ;
        Position bestMove = null;

        for (final Position child : allowedPositions) {
            //TODO: This clone sucks. I must re design this program. :( I should just need the array :(
            //What I need is an undo movement function. In that way I do not need to clone/copy the whole board :(
            //see: http://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
            final Board newBoard = board.clone();
            newBoard.makeMove(this.maxPlayer, child.getColumn(), child.getRow());
            newBoard.flipOpponentDiscs(child, this.maxPlayer);
            final int val = this.minimaxAB(newBoard, child, depth -1, alpha, Integer.MAX_VALUE, ReversiLogic.opponent(this.maxPlayer));
            if (val > alpha) {
                alpha = val;
                bestMove = child;
            }
        }

        return bestMove;
    }


    /**
     * http://www.site-constructor.com/othello/Present/MobilityEval.html
     * 1. Minimize your opponents move options.
     * 2. Less importantly, maximize your own move options.
     *
     * @param board
     * @param player
     * @return
     */
    private int getMobilityForPlayer(final Board board, final Player player) {
        final int mobility = board.allowedPositions(player).size();
        if (mobility == 0) {
            return Integer.MAX_VALUE - 1;
        } else {
            return 64 / mobility;
        }
    }

    private int heuristic (final Board board, final Player player) {
        return this.getMobilityForPlayer(board, player);
    }
}
