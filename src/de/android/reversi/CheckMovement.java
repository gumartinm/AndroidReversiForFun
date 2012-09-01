package de.android.reversi;

import java.util.ArrayList;
import java.util.List;

public final class CheckMovement {
    public static boolean horizontal(final Square gameBoard[][], final Movement movement,
            final Player player) {
        final boolean outflankRight = outflankRight(gameBoard, movement, player);
        final boolean outflankLeft = outflankLeft(gameBoard, movement, player);

        return outflankRight || outflankLeft;
    }

    public static boolean vertical(final Square gameBoard[][], final Movement movement,
            final Player player) {
        final boolean outflankUp = outflankUp(gameBoard, movement, player);
        final boolean outflankDown = outflankDown(gameBoard, movement, player);

        return outflankUp || outflankDown;
    }

    public static boolean diagonal(final Square gameBoard[][], final Movement movement,
            final Player player) {
        final boolean outflankDiagonalLeftUp = outflankDiagonalLeftUp(gameBoard, movement, player);
        final boolean outflankDiagonalRightDown = outflankDiagonalRightDown(gameBoard, movement, player);
        final boolean outflankDiagonalRightUp = outflankDiagonalRightUp(gameBoard, movement, player);
        final boolean outflankDiagonalLeftDown = outflankDiagonalLeftDown(gameBoard, movement, player);

        return outflankDiagonalLeftUp || outflankDiagonalRightDown || outflankDiagonalRightUp ||
                outflankDiagonalLeftDown;
    }

    public static boolean empty(final Square gameBoard[][], final short column, final short row) {
        if (gameBoard[column][row].getPlayer() == Player.NOPLAYER) {
            return true;
        }
        return false;
    }

    private static boolean outflankUp (final Square gameBoard[][],
            final Movement movement, final Player player) {
        final short row = movement.getRow();
        final short column = movement.getColumn();

        //Precondition:
        if  (row <= 1) {
            return false;
        }

        final List<FlippedDisc> flippedDiscs = outflank((short)0, (short)-1, gameBoard,
                player, (short)(row-1), column);
        if (flippedDiscs.isEmpty()) {
            return false;
        }

        movement.getFlippedDiscs().addAll(flippedDiscs);
        return true;
    }

    private static boolean outflankDown (final Square gameBoard[][],
            final Movement movement, final Player player) {
        final short row = movement.getRow();
        final short column = movement.getColumn();

        //Precondition:
        if  (row >= Board.NUMBER_OF_ROWS -2) {
            return false;
        }

        final List<FlippedDisc> flippedDiscs = outflank((short)0, (short)1, gameBoard,
                player, (short)(row+1), column);
        if (flippedDiscs.isEmpty()) {
            return false;
        }

        movement.getFlippedDiscs().addAll(flippedDiscs);
        return true;
    }

    private static boolean outflankRight (final Square gameBoard[][],
            final Movement movement, final Player player) {
        final short row = movement.getRow();
        final short column = movement.getColumn();

        //Precondition:
        if  (column >= Board.NUMBER_OF_COLUMNS -2) {
            return false;
        }

        final List<FlippedDisc> flippedDiscs = outflank((short)1, (short)0, gameBoard,
                player, row, (short)(column+1));
        if (flippedDiscs.isEmpty()) {
            return false;
        }

        movement.getFlippedDiscs().addAll(flippedDiscs);
        return true;
    }

    private static boolean outflankLeft (final Square gameBoard[][],
            final Movement movement, final Player player) {
        final short row = movement.getRow();
        final short column = movement.getColumn();

        //Precondition:
        if  (column <= 1) {
            return false;
        }

        final List<FlippedDisc> flippedDiscs = outflank((short)-1, (short)0, gameBoard,
                player, row, (short)(column-1));
        if (flippedDiscs.isEmpty()) {
            return false;
        }

        movement.getFlippedDiscs().addAll(flippedDiscs);
        return true;
    }

    private static boolean outflankDiagonalLeftUp (final Square gameBoard[][],
            final Movement movement, final Player player) {
        final short row = movement.getRow();
        final short column = movement.getColumn();

        //Precondition:
        if  (column <= 1 || row <= 1) {
            return false;
        }

        final List<FlippedDisc> flippedDiscs = outflank((short)-1, (short)-1, gameBoard,
                player, (short)(row-1), (short)(column-1));
        if (flippedDiscs.isEmpty()) {
            return false;
        }

        movement.getFlippedDiscs().addAll(flippedDiscs);
        return true;
    }

    private static boolean outflankDiagonalRightDown (final Square gameBoard[][],
            final Movement movement, final Player player) {
        final short row = movement.getRow();
        final short column = movement.getColumn();

        //Precondition:
        if  (column >= (Board.NUMBER_OF_COLUMNS -2) || row >= (Board.NUMBER_OF_ROWS -2)) {
            return false;
        }

        final List<FlippedDisc> flippedDiscs = outflank((short)1, (short)1, gameBoard,
                player, (short)(row+1), (short)(column+1));
        if (flippedDiscs.isEmpty()) {
            return false;
        }

        movement.getFlippedDiscs().addAll(flippedDiscs);
        return true;
    }

    private static boolean outflankDiagonalLeftDown (final Square gameBoard[][],
            final Movement movement, final Player player) {
        final short row = movement.getRow();
        final short column = movement.getColumn();

        //Precondition:
        if  (column <= 1 || row >= (Board.NUMBER_OF_ROWS -2)) {
            return false;
        }

        final List<FlippedDisc> flippedDiscs = outflank((short)-1, (short)1, gameBoard, player,
                (short)(row+1), (short)(column-1));
        if (flippedDiscs.isEmpty()) {
            return false;
        }

        movement.getFlippedDiscs().addAll(flippedDiscs);
        return true;
    }

    private static boolean outflankDiagonalRightUp (final Square gameBoard[][],
            final Movement movement, final Player player) {
        final short row = movement.getRow();
        final short column = movement.getColumn();

        //Precondition:
        if  (row <= 1 || column >= (Board.NUMBER_OF_COLUMNS -2)) {
            return false;
        }

        final List<FlippedDisc> flippedDiscs = outflank((short)1, (short)-1, gameBoard, player,
                (short)(row-1), (short)(column+1));
        if (flippedDiscs.isEmpty()) {
            return false;
        }

        movement.getFlippedDiscs().addAll(flippedDiscs);
        return true;
    }

    private static List<FlippedDisc> outflank(final short moveX, final short moveY,
            final Square gameBoard[][], final Player player, short row, short column) {
        final List<FlippedDisc> flippedDiscs = new ArrayList<FlippedDisc>();
        boolean match = false;

        while (row > 0 && column > 0 &&
                row < Board.NUMBER_OF_ROWS && column < Board.NUMBER_OF_COLUMNS &&
                !empty(gameBoard, column, row)) {

            if (gameBoard[column][row].getPlayer() == player) {
                match = true;
                break;
            }

            flippedDiscs.add(new FlippedDisc(row, column));
            column = (short)(column + moveX);
            row = (short)(row + moveY);
        }

        if (!match) {
            flippedDiscs.clear();
        }

        return flippedDiscs;
    }
}
