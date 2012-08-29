package de.android.reversi;

public class CheckMovement {
    public static boolean horizontal(Square gameBoard[][], short column, short row, Player player) {
        return right(gameBoard, column, row, player) ||
                left(gameBoard, column, row, player);
    }

    public static boolean vertical(Square gameBoard[][], short column, short row, Player player) {
        return up(gameBoard, column, row, player) ||
                down(gameBoard, column, row, player);
    }

    public static boolean diagonal(Square gameBoard[][], short column, short row, Player player) {
        return diagonalLeftUp(gameBoard, column, row, player) ||
                diagonalRightDown(gameBoard, column, row, player) ||
                diagonalRightUp(gameBoard, column, row, player) ||
                diagonalLeftDown(gameBoard, column, row, player);
    }

    private static boolean up (Square gameBoard[][], short column, short row, Player player) {

        //Precondition 1.
        if  (row <= 1) {
            return false;
        }

        //Precondition 2.
        if (gameBoard[column][row-1].getPlayer() == player) {
            return false;
        }

        return outflank((short)0, (short)-1, gameBoard, player, (short)(row-2), column);
    }

    private static boolean down (Square gameBoard[][], short column, short row, Player player) {

        //Precondition 1:
        if  (row >= ReversiView.NUMBER_OF_ROWS -2) {
            return false;
        }

        //Precondition 2.
        if (gameBoard[column][row+1].getPlayer() == player) {
            return false;
        }

        return outflank((short)0, (short)1, gameBoard, player, (short)(row+2), column);
    }

    private static boolean right (Square gameBoard[][], short column, short row, Player player) {

        //Precondition 1:
        if  (column >= ReversiView.NUMBER_OF_COLUMNS -2) {
            return false;
        }

        //Precondition 2.
        if (gameBoard[column+1][row].getPlayer() == player) {
            return false;
        }

        return outflank((short)1, (short)0, gameBoard, player, row, (short)(column+2));
    }

    private static boolean left (Square gameBoard[][], short column, short row, Player player) {

        //Precondition 1:
        if  (column <= 1) {
            return false;
        }

        //Precondition 2.
        if (gameBoard[column-1][row].getPlayer() == player) {
            return false;
        }

        return outflank((short)-1, (short)0, gameBoard, player, row, (short)(column-2));
    }

    private static boolean diagonalLeftUp (Square gameBoard[][], short column, short row,
            Player player) {

        //Precondition 1:
        if  (column <= 1 && row <= 1) {
            return false;
        }

        //Precondition 2.
        if (gameBoard[column-1][row-1].getPlayer() == player) {
            return false;
        }

        return outflank((short)-1, (short)-1, gameBoard, player, (short)(row-2),
                (short)(column-2));
    }

    private static boolean diagonalRightDown (Square gameBoard[][], short column, short row,
            Player player) {

        //Precondition 1:
        if  (column >= ReversiView.NUMBER_OF_COLUMNS -2 && row >= ReversiView.NUMBER_OF_ROWS -2) {
            return false;
        }

        //Precondition 2.
        if (gameBoard[column+1][row+1].getPlayer() == player) {
            return false;
        }

        return outflank((short)1, (short)1, gameBoard, player, (short)(row+2),
                (short)(column+2));
    }

    private static boolean diagonalLeftDown (Square gameBoard[][], short column, short row,
            Player player) {

        //Precondition 1:
        if  (column <= 1 && row >= ReversiView.NUMBER_OF_ROWS -2) {
            return false;
        }

        //Precondition 2.
        if (gameBoard[column-1][row+1].getPlayer() == player) {
            return false;
        }

        return outflank((short)1, (short)1, gameBoard, player, (short)(row+2),
                (short)(column-2));
    }

    private static boolean diagonalRightUp (Square gameBoard[][], short column, short row,
            Player player) {

        //Precondition 1:
        if  (row <= 1 && column >= ReversiView.NUMBER_OF_COLUMNS -2) {
            return false;
        }

        //Precondition 2.
        if (gameBoard[column+1][row-1].getPlayer() == player) {
            return false;
        }

        return outflank((short)1, (short)1, gameBoard, player, (short)(row-2),
                (short)(column+2));
    }

    public static boolean empty(Square gameBoard[][], short column, short row) {
        if (gameBoard[column][row].getPlayer() == Player.NOPLAYER) {
            return true;
        }
        return false;
    }

    private static boolean outflank(short moveX, short moveY, Square gameBoard[][], Player player,
            short row, short column) {

        do {
            if (gameBoard[column][row].getPlayer() == player) {
                return true;
            }
            row = (short)(row + moveX);
            column = (short)(column + moveY);
        }while (row > 0 && column >0 &&
                row <= ReversiView.NUMBER_OF_ROWS && column <= ReversiView.NUMBER_OF_COLUMNS);

        return false;
    }
}
