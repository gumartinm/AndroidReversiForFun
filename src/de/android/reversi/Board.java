package de.android.reversi;

import java.util.List;

public class Board {
    public static final short NUMBER_OF_COLUMNS = 8;
    public static final short NUMBER_OF_ROWS = 8;
    public static final short TOP_MARGIN = 0;
    public static final short LEFT_MARGIN = 0;

    private final Square gameBoard[][] = new Square[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];

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

    public void updateBoard(final Player player, final short column, final short row) {
        this.updateBoard(player, column, row, false);
    }

    public void updateBoard(final Player player, final short column, final short row,
            final boolean suggestion) {
        gameBoard[column][row].setPlayer(player);
        gameBoard[column][row].setSuggestion(suggestion);
    }

    public void removeSuggestionsFromBoard(final List<Movement> list) {
        for (final Movement movement : list) {
            this.updateBoard(Player.NOPLAYER, movement.getColumn(), movement.getRow());
        }
    }


    public void flipOpponentDiscs(final Movement movement, final Player currentPlayer) {
        for (final FlippedDisc flippedDisc : movement.getFlippedDiscs()) {
            this.updateBoard(currentPlayer, flippedDisc.getColumn(), flippedDisc.getRow());
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
}
