package de.android.reversi;

import java.util.List;



public class AI {
    private Player max;
    private Player min;

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


    private static final int depth = 2;


    public AI(final Player currentPlayer) {

        //TODO: This sucks. Re implementation without Enum and with MVC pattern :(
        if (currentPlayer == Player.PLAYER1) {
            max = Player.PLAYER1;
            min = Player.PLAYER2;
        }
        else {
            max = Player.PLAYER2;
            min = Player.PLAYER1;
        }
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
            final boolean max) {
        Player player;
        //TODO: This sucks. I must re design this program. :(
        if (max) {
            player = this.max;
        } else {
            player = this.min;
        }

        List<Position> allowedPositions;
        if (depth == 0 || (allowedPositions = board.allowedPositions(player)).isEmpty()) {
            return 10; //the heuristic value of node
        }
        if (max) {
            for (final Position child : allowedPositions) {
                //TODO: This clone sucks. I must re design this program. :( I should just need the array :(
                final Board newBoard = board.clone();
                newBoard.makeMove(player, child.getColumn(), child.getRow());
                newBoard.flipOpponentDiscs(child, player);
                final int val = minimaxAB (newBoard, child, depth -1, alpha, beta, false);
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
                final Board newBoard = board.clone();
                newBoard.makeMove(player, child.getColumn(), child.getRow());
                newBoard.flipOpponentDiscs(child, player);
                final int val = minimaxAB (newBoard, child, depth -1, alpha, beta, false);
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
        final List<Position> allowedPositions = board.allowedPositions(this.max);
        int alpha = -10;
        Position bestMove = null;

        for (final Position child : allowedPositions) {
            //TODO: This clone sucks. I must re design this program. :( I should just need the array :(
            final Board newBoard = board.clone();
            newBoard.makeMove(this.max, child.getColumn(), child.getRow());
            newBoard.flipOpponentDiscs(child, this.max);
            final int val = this.minimaxAB(newBoard, child, depth, -10, 10, true);
            if (val > alpha) {
                alpha = val;
                bestMove = child;
            }
        }

        return bestMove;
    }
}
