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
        //Initial values always for alpha = Integer.MIN_VALUE
        //Initial values always for beta = Integer.MAX_VALUE

        final List<Position> allowedPositions = board.allowedPositions(this.maxPlayer);
        //Just you must make sure the heuristic values are not going to be smallest than this.
        int alpha = Integer.MIN_VALUE ;
        //Just you must make sure the heuristic values are not going to be bigger than this.
        final int beta = Integer.MAX_VALUE;
        Position bestMove = null;

        for (final Position child : allowedPositions) {
            //TODO: This clone sucks. I must re design this program. :( I should just need the array :(
            //What I need is an undo movement function. In that way I do not need to clone/copy the whole board :(
            //see: http://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
            final Board newBoard = board.clone();
            newBoard.makeMove(this.maxPlayer, child.getColumn(), child.getRow());
            newBoard.flipOpponentDiscs(child, this.maxPlayer);
            final int val = this.minimaxAB(newBoard, child, depth -1, alpha, beta, ReversiLogic.opponent(this.maxPlayer));
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
    //es la mobilidad para el oponente.
    //cuanto más baja sea la movilidad para el oponente este metodo devuelve un valor mas alto.
    private int getMobilityForOpponent(final Board board, final Player player) {
        final int mobility = board.allowedPositions(player).size();

        if (mobility == 0) {
            return 64;
        } else {
            return 64 / mobility;
        }
    }

    private int heuristic (final Board board, final Player player) {
        final int diskSquareTable = this.diskSquareTable(board, player);
        final int mobility = this.getMobilityForOpponent(board, player);
        final int frontierDiscs = this.frontierDiscs(board, player);

        return ((diskSquareTable * 10) + (mobility * 5) + (frontierDiscs * 5));
    }

    //es la puntuacion para el oponente.
    //cuanto más alta sea la puntuación para el oponente este metodo devuelve un valor mas bajo.
    private int diskSquareTable(final Board board, final Player player) {
        int total = 0;

        for (short column = 0; column < Board.NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < Board.NUMBER_OF_ROWS; row++) {
                if (board.getGameBoard()[column][row].getPlayer() == player) {
                    //TODO change the disk square table depending on the state of the game
                    //see: http://www.site-constructor.com/othello/Present/BoardLocationValue.html
                    total += this.values[column][row];
                }
            }
        }
        return (300 - total);
    }

    //discos frontera para el oponente
    //cuantos mas discos frontera tenga el oponente mejor.
    private int frontierDiscs(final Board board, final Player player) {
        int result = 0;

        for (short column = 0; column < Board.NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < Board.NUMBER_OF_ROWS; row++) {
                if (board.getGameBoard()[column][row].getPlayer() == player) {
                    if (board.idFrontierDisc(column, row)) {
                        result++;
                    }
                }
            }
        }

        return result;
    }
}
