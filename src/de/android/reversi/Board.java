package de.android.reversi;

import java.util.ArrayList;
import java.util.List;

public class Board implements Cloneable {
    private final static short [][] directions = { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 },
        { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 } };

    public static final short NUMBER_OF_COLUMNS = 8;
    public static final short NUMBER_OF_ROWS = 8;
    public static final short TOP_MARGIN = 0;
    public static final short LEFT_MARGIN = 0;

    //TODO: I am using clone but it could be interesting to use a copy factory.
    private Square gameBoard[][] = new Square[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];

    //Just one dimension because it is a square.
    private int squareWidth;
    //Just one dimension because it is a square.
    private int canvasWidth;


    public void initBoard() {
        for (short column = 0; column < NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < NUMBER_OF_ROWS; row++) {
                this.gameBoard[column][row] = new Square();;
            }
        }
    }

    public void makeMove(final Player player, final short column, final short row) {
        this.makeMove(player, column, row, false);
    }

    public void makeMove(final Player player, final short column, final short row,
            final boolean suggestion) {
        gameBoard[column][row].setPlayer(player);
        gameBoard[column][row].setSuggestion(suggestion);
    }

    public void removeSuggestionsFromBoard(final List<Position> list) {
        for (final Position movement : list) {
            this.makeMove(Player.NOPLAYER, movement.getColumn(), movement.getRow());
        }
    }


    public void flipOpponentDiscs(final Position position, final Player currentPlayer) {
        final List<Position> flippedDiscs = flippedDiscs(currentPlayer, position.getRow(),
                position.getColumn());
        for (final Position flippedDisc : flippedDiscs) {
            this.makeMove(currentPlayer, flippedDisc.getColumn(), flippedDisc.getRow());
        }
    }


    public void updateSquareParameters(final int squareWidth) {
        for (short column = 0; column < Board.NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < Board.NUMBER_OF_ROWS; row++) {

                // calculating the square's center
                final int cellMediumX = (column * squareWidth + (column + 1) *
                        squareWidth) / 2;
                final int cellMediumY = (row * squareWidth + (row + 1) *
                        squareWidth) / 2;

                // applying the margins
                final int cx = cellMediumX + Board.LEFT_MARGIN;
                final int cy = cellMediumY +Board. TOP_MARGIN;

                // the radius
                final int radius = (squareWidth - 2) / 2 - 2;

                //update squares
                gameBoard[column][row].setRadius(radius);
                gameBoard[column][row].setSquareMediumX(cx);
                gameBoard[column][row].setSquareMediumY(cy);
            }
        }
    }

    public short transformCoordinateYInRow(final float y) {

        short row = (short) ((y - Board.TOP_MARGIN) / squareWidth);

        if (row < 0 || row >= Board.NUMBER_OF_ROWS) {
            row = -1;
        }

        return row;
    }

    public short transformCoordinateXInColumn(final float x) {

        short column = (short) ((x - Board.LEFT_MARGIN) / squareWidth);

        if (column < 0 || column >= Board.NUMBER_OF_COLUMNS) {
            column = -1;
        }

        return column;
    }


    public void calculateGraphicParameters(int width, final int height) {

        // getting the minor (the board is a square)
        if (height < width) {
            width = height;
        }

        // converting the dimensions to get them divisible by 8
        while (width % 8 != 0) {
            width--;
        }

        canvasWidth = width;
        squareWidth = (width - Board.LEFT_MARGIN * 2) / Board.NUMBER_OF_COLUMNS;
    }


    public final List<Position> allowedPositions(final Player player) {
        final List<Position> list = new ArrayList<Position>();

        for (short column = 0; column < Board.NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < Board.NUMBER_OF_ROWS; row++) {
                if (empty(column, row)) {
                    if (isValidMove(player, row, column)) {
                        list.add(new Position(row, column));
                    }
                }
            }
        }

        return list;
    }

    public final boolean idFrontierDisc(final short column, final short row) {
        for (int i = 0; i < directions.length; i++) {
            final short x = (short)(directions[i][0] + column);
            final short y = (short)(directions[i][1] + row);

            if(y >= 0 && x >= 0 && y < Board.NUMBER_OF_ROWS &&
                    x < Board.NUMBER_OF_COLUMNS && empty(x, y)) {
                return true;
            }
        }

        return false;
    }
    private final boolean empty(final short column, final short row) {
        if (gameBoard[column][row].getPlayer() == Player.NOPLAYER) {
            return true;
        }
        return false;
    }

    private final boolean isValidMove (final Player player, final short row, final short column) {
        for (int i = 0; i < directions.length; i++) {
            if (isValidMove (directions[i][0], directions[i][1], player,
                    (short)(row + directions[i][1]), (short)(column + directions[i][0])) > 0) {
                return true;
            }
        }

        return false;
    }

    private final List<Position> flippedDiscs (final Player player, final short row, final short column) {
        final List<Position> flippedDiscs = new ArrayList<Position>();
        List<Position> aux;

        for (int i = 0; i < directions.length; i++) {
            aux = flippedDiscs (directions[i][0], directions[i][1], player,
                    (short)(row + directions[i][1]), (short)(column + directions[i][0]));
            if (!aux.isEmpty()) {
                flippedDiscs.addAll(aux);
            }
        }

        return flippedDiscs;
    }

    private final int isValidMove (final short moveX, final short moveY, final Player player,
            short row, short column) {
        int flippedDiscs = 0;
        boolean match = false;

        while (row >= 0 && column >= 0 &&
                row < Board.NUMBER_OF_ROWS && column < Board.NUMBER_OF_COLUMNS &&
                !empty(column, row)) {

            if (gameBoard[column][row].getPlayer() == player) {
                match = true;
                break;
            }

            flippedDiscs++;
            column = (short)(column + moveX);
            row = (short)(row + moveY);
        }

        if (!match) {
            flippedDiscs = 0;
        }

        return flippedDiscs;
    }

    private final List<Position> flippedDiscs(final short moveX, final short moveY,
            final Player player, short row, short column) {
        final List<Position> flippedDiscs = new ArrayList<Position>();
        boolean match = false;

        while (row >= 0 && column >= 0 &&
                row < Board.NUMBER_OF_ROWS && column < Board.NUMBER_OF_COLUMNS &&
                !empty(column, row)) {

            if (gameBoard[column][row].getPlayer() == player) {
                match = true;
                break;
            }

            flippedDiscs.add(new Position(row, column));
            column = (short)(column + moveX);
            row = (short)(row + moveY);
        }

        if (!match) {
            flippedDiscs.clear();
        }

        return flippedDiscs;
    }


    /*** Getters ***/

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getSquareWidth() {
        return squareWidth;
    }

    public Square[][] getGameBoard() {
        return gameBoard;
    }

    @Override
    public Board clone() {
        try {
            final Board result = (Board) super.clone();
            //If this was not a matrix (a simple array) I could use gameBoard.clone();
            result.gameBoard = new Square[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
            for (short column = 0; column < NUMBER_OF_COLUMNS; column++) {
                for (short row = 0; row < NUMBER_OF_ROWS; row++) {
                    result.gameBoard[column][row] = gameBoard[column][row].clone();
                }
            }
            return result;
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
