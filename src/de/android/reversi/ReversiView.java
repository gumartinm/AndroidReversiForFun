package de.android.reversi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class ReversiView extends SurfaceView {
    public static final short NUMBER_OF_COLUMNS = 8;
    public static final short NUMBER_OF_ROWS = 8;

    private static final short TOP_MARGIN = 0;
    private static final short LEFT_MARGIN = 0;

    private final Square gameBoard[][] = new Square[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
    private final Player IA = Player.NOPLAYER;

    //¿Funciona bien volatile con enum? Ver mi codigo de Singletons y enums.
    private volatile Player currentPlayer = Player.PLAYER1;
    private volatile boolean isEnableUserTouch;

    private int squareWidth;
    private int squareHeight;
    private int canvasHeight;
    private int canvasWidth;
    private List<Movement> listAllowedMovements;


    public ReversiView(final Context context) {
        super(context);
        this.preInitBoard();
        this.initialize();
    }

    public ReversiView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.preInitBoard();
        this.initialize();
    }

    public ReversiView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        this.preInitBoard();
        this.initialize();
    }

    private void drawGrid(final Canvas canvas) {
        //Canvas background
        final Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);


        //Square (green)
        paint.setColor(Color.rgb(0, 158, 11));
        canvas.drawRect(LEFT_MARGIN, TOP_MARGIN,
                NUMBER_OF_COLUMNS * squareWidth + LEFT_MARGIN,
                NUMBER_OF_ROWS * squareHeight + TOP_MARGIN, paint);


        //Lines between squares
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        for (int col = 0; col <= NUMBER_OF_COLUMNS; col++) {
            // vertical lines
            final int x = col * squareWidth + LEFT_MARGIN;
            canvas.drawLine(x, TOP_MARGIN, x, canvasHeight - TOP_MARGIN * 1, paint);
        }
        for (int row = 0; row < NUMBER_OF_ROWS + 1; row++) {
            final int y = row * squareHeight + TOP_MARGIN;
            // horizontal lines
            canvas.drawLine(LEFT_MARGIN, y, canvasWidth - (LEFT_MARGIN * 1), y, paint);
        }

    }

    private void calculateGraphicParameters(final Canvas canvas, final int width,
            final int height) {
        canvasWidth = width;
        canvasHeight = height;

        // getting the minor (the board is a square)
        if (canvasHeight > canvasWidth) {
            canvasHeight = canvasWidth;
        } else {
            canvasWidth = canvasHeight;
        }

        // converting the dimensions to get them divisible by 8
        while (canvasWidth % 8 != 0) {
            canvasWidth--;
            canvasHeight--;
        }

        squareWidth = (canvasWidth - LEFT_MARGIN * 2) / NUMBER_OF_COLUMNS;
        squareHeight = (canvasHeight - TOP_MARGIN * 2) / NUMBER_OF_ROWS;
    }

    private void initialize() {

        getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                //White
                updateBoard(Player.PLAYER1, (short)3, (short)4);
                updateBoard(Player.PLAYER1, (short)4, (short)3);
                //Black
                updateBoard(Player.PLAYER2, (short)4, (short)4);
                updateBoard(Player.PLAYER2, (short)3, (short)3);

                //AllowedMovements for Player
                listAllowedMovements = allowedMovements(currentPlayer, gameBoard);

                //UpdateBoard with suggestions
                for (final Movement movement : listAllowedMovements) {
                    updateBoard(currentPlayer, movement.getColumn(), movement.getRow(), true);
                }
            }

            @Override
            public void surfaceChanged(final SurfaceHolder holder, final int format,
                    final int width, final int height) {
                final Canvas canvas = holder.lockCanvas();
                calculateGraphicParameters(canvas, width, height);
                updateSquareParameters();
                drawGrid(canvas);
                drawPositions(canvas);
                holder.unlockCanvasAndPost(canvas);
                isEnableUserTouch = true;
            }

            @Override
            public void surfaceDestroyed(final SurfaceHolder holder) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {

        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return false;
        }

        if (this.isEnableUserTouch) {
            final short column = transformCoordinateXInColumn(event.getX());
            final short row = transformCoordinateYInRow(event.getY());

            if (row != -1 && column != -1 ) {
                Movement movement;
                if((movement = retrieveAllowedMovement(row, column)) != null) {
                    removeSuggestionsFromBoard(gameBoard, listAllowedMovements);
                    updateBoard(this.currentPlayer, column, row);
                    flipOpponentDiscs(gameBoard, movement, currentPlayer);
                    this.mainLoop();
                }
            }
        }

        return true;
    }

    private short transformCoordinateYInRow(final float y) {

        short row = (short) ((y - TOP_MARGIN) / this.squareWidth);

        // if tapped outside the board
        if (row < 0 || row >= NUMBER_OF_ROWS) {
            row = -1;
        }

        return row;
    }

    private short transformCoordinateXInColumn(final float x) {

        short column = (short) ((x - LEFT_MARGIN) / this.squareWidth);

        // if tapped outside the board
        if (column < 0 || column >= NUMBER_OF_COLUMNS) {
            column = -1;
        }

        return column;
    }

    private void drawDisk(final Canvas canvas, final Square square, final short column,
            final short row) {
        this.drawCircle(canvas, square.getPlayer(), square.getSquareMediumX(),
                square.getSquareMediumY(), square.getRadius(), square.isSuggestion());
    }

    private void drawCircle(final Canvas canvas, final Player player, final int cx,
            final int cy, final int radius, final boolean isSuggestion) {

        final Paint paint = new Paint();

        paint.setAntiAlias(true);


        switch (player){
            case PLAYER1:
                //border color
                paint.setColor(Color.BLACK);
                canvas.drawCircle(cx, cy, radius, paint);
                //inside color
                if (isSuggestion) {
                    paint.setColor(Color.GRAY);
                }else {
                    paint.setColor(player.getColor());
                }
                canvas.drawCircle(cx, cy, radius-2, paint);
                break;
            case PLAYER2:
                //border color
                paint.setColor(Color.BLACK);
                canvas.drawCircle(cx, cy, radius, paint);
                //inside color
                if (isSuggestion) {
                    paint.setColor(Color.BLUE);
                } else {
                    paint.setColor(player.getColor());
                }
                canvas.drawCircle(cx, cy, radius-2, paint);
                break;
            default:
                break;
        }
    }

    private void updateBoard(final Player player, final short column, final short row) {
        this.updateBoard(player, column, row, false);
    }

    private void updateBoard(final Player player, final short column, final short row,
            final boolean suggestion) {
        this.gameBoard[column][row].setPlayer(player);
        this.gameBoard[column][row].setSuggestion(suggestion);
    }

    private void drawPositions(final Canvas canvas) {
        for (short column = 0; column < NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < NUMBER_OF_ROWS; row++) {
                if (this.gameBoard[column][row].getPlayer() != Player.NOPLAYER) {
                    drawDisk(canvas, this.gameBoard[column][row], column, row);
                }
            }
        }
    }

    private void preInitBoard() {
        for (short column = 0; column < NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < NUMBER_OF_ROWS; row++) {
                this.gameBoard[column][row] = new Square();;
            }
        }
    }

    private void updateSquareParameters() {
        for (short column = 0; column < NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < NUMBER_OF_ROWS; row++) {

                // calculating the square's center
                final int cellMediumX = (column * this.squareWidth + (column + 1) *
                        this.squareWidth) / 2;
                final int cellMediumY = (row * this.squareHeight + (row + 1) *
                        this.squareHeight) / 2;

                // applying the margins
                final int cx = cellMediumX + LEFT_MARGIN;
                final int cy = cellMediumY + TOP_MARGIN;

                // the radius
                final int radius = (this.squareWidth - 2) / 2 - 2;

                //update squares
                this.gameBoard[column][row].setRadius(radius);
                this.gameBoard[column][row].setSquareMediumX(cx);
                this.gameBoard[column][row].setSquareMediumY(cy);
            }
        }
    }

    private void mainLoop() {
        this.currentPlayer = opponent(this.currentPlayer);

        if (this.currentPlayer != this.IA) {
            //AllowedMovements for Player
            listAllowedMovements = allowedMovements(currentPlayer, gameBoard);

            //UpdateBoard with suggestions
            for (final Movement movement : listAllowedMovements) {
                updateBoard(currentPlayer, movement.getColumn(), movement.getRow(), true);
            }


            final Canvas canvas = getHolder().lockCanvas();
            drawGrid(canvas);
            drawPositions(canvas);
            getHolder().unlockCanvasAndPost(canvas);

            this.isEnableUserTouch = true;
        }
        else {
            this.isEnableUserTouch = false;
            //Launch IA thread.
        }
    }

    private List<Movement> allowedMovements(final Player player, final Square gameBoard[][]) {
        final List<Movement> list = new ArrayList<Movement>();

        for (short column = 0; column < NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < NUMBER_OF_ROWS; row++) {
                final Movement movement = new Movement(row, column);
                if (CheckMovement.empty(gameBoard, column, row) &&
                        (CheckMovement.diagonal(gameBoard, movement, player) ||
                                CheckMovement.horizontal(gameBoard, movement, player) ||
                                CheckMovement.vertical(gameBoard, movement, player))) {
                    list.add(movement);
                }
            }
        }

        return list;
    }

    private Movement retrieveAllowedMovement(final short row, final short column) {
        for (final Movement movement : listAllowedMovements) {
            if ((movement.getRow() == row) && (movement.getColumn() == column)) {
                return movement;
            }
        }

        return null;
    }

    private void removeSuggestionsFromBoard(final Square gameBoard[][],
            final List<Movement> listAllowedMovements) {

        for (final Movement iterator : listAllowedMovements) {
            updateBoard(Player.NOPLAYER, iterator.getColumn(), iterator.getRow());
        }
    }

    private Player opponent(final Player currentPlayer) {
        switch (currentPlayer){
            case PLAYER1:
                return Player.PLAYER2;
            case PLAYER2:
                return Player.PLAYER1;
            default:
                return Player.NOPLAYER;
        }
    }

    private void flipOpponentDiscs(final Square gameBoard[][], final Movement movement, final Player currentPlayer) {
        for (final FlippedDisc flippedDisc : movement.getFlippedDiscs()) {
            updateBoard(currentPlayer, flippedDisc.getColumn(), flippedDisc.getRow());
        }
    }
}
