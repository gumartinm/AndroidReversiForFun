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

    private int squareWidth;
    private int squareHeight;
    private int canvasHeight;
    private int canvasWidth;

    private final Square gameBoard[][] = new Square[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
    //¿Funciona bien volatile con enum? Ver mi codigo de Singletons y enums.
    private volatile Player currentPlayer = Player.PLAYER1;
    private volatile boolean isEnableUserTouch;


    public ReversiView(Context context) {
        super(context);
        this.preInitBoard();
        this.initialize();
    }

    public ReversiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.preInitBoard();
        this.initialize();
    }

    public ReversiView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.preInitBoard();
        this.initialize();
    }

    /**
     * Occurs when drawing the board
     * 
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        this.setBackgroundColor(Color.RED);
    }

    private void drawGrid(Canvas canvas) {
        //Canvas background
        Paint paint = new Paint();
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
            int x = col * squareWidth + LEFT_MARGIN;
            canvas.drawLine(x, TOP_MARGIN, x, canvasHeight - TOP_MARGIN * 1, paint);
        }
        for (int row = 0; row < NUMBER_OF_ROWS + 1; row++) {
            int y = row * squareHeight + TOP_MARGIN;
            // horizontal lines
            canvas.drawLine(LEFT_MARGIN, y, canvasWidth - (LEFT_MARGIN * 1), y, paint);
        }

    }

    private void calculateGraphicParameters(Canvas canvas, int width, int height) {
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
            public void surfaceCreated(SurfaceHolder holder) {
                //White
                updateBoard(Player.PLAYER1, (short)3, (short)3);
                updateBoard(Player.PLAYER1, (short)4, (short)4);
                //Black
                updateBoard(Player.PLAYER2, (short)4, (short)3);
                updateBoard(Player.PLAYER2, (short)3, (short)4);

                //AllowedMovements for Player
                List<Movement> list = allowedMovements(currentPlayer);

                //UpdateBoard with suggestions
                for (Movement movement : list) {
                    updateBoard(currentPlayer, movement.getColumn(), movement.getRow(), true);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                    int width, int height) {
                Canvas canvas = holder.lockCanvas();
                calculateGraphicParameters(canvas, width, height);
                updateSquareParameters();
                drawGrid(canvas);
                drawPositions(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isEnableUserTouch) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Hidden pointer
            short column = transformCoordinateXInColumn(event.getX());
            short row = transformCoordinateYInRow(event.getY());
            if (row != -1 && column != -1 ) {
                updateBoard(this.currentPlayer, column, row);
                Canvas canvas = getHolder().lockCanvas();
                drawGrid(canvas);
                drawPositions(canvas);
                getHolder().unlockCanvasAndPost(canvas);
            }
            return true;
        }
        else {
            return false;
        }
    }

    private short transformCoordinateYInRow(float y) {

        short row = (short) ((y - TOP_MARGIN) / this.squareWidth);

        // if tapped outside the board
        if (row < 0 || row >= NUMBER_OF_ROWS) {
            row = -1;
        }

        return row;
    }

    private short transformCoordinateXInColumn(float x) {

        short column = (short) ((x - LEFT_MARGIN) / this.squareWidth);

        // if tapped outside the board
        if (column < 0 || column >= NUMBER_OF_COLUMNS) {
            column = -1;
        }

        return column;
    }

    private void drawDisk(Canvas canvas, Square square, short column, short row) {
        this.drawCircle(canvas, square.getPlayer(), square.getSquareMediumX(),
                square.getSquareMediumY(), square.getRadius(), square.isSuggestion());
    }

    private void drawCircle(Canvas canvas, Player player, int cx, int cy, int radius,
            boolean alphaChannel) {

        Paint paint = new Paint();

        paint.setAntiAlias(true);

        //Set alpha channel when drawing suggestion discs.
        if (alphaChannel) {
            paint.setAlpha(77);
        }

        switch (player){
            case PLAYER1:
                paint.setColor(player.getColor());
                //paint.setAntiAlias(true);
                if (alphaChannel) {
                    paint.setColor(Color.BLUE);
                }
                canvas.drawCircle(cx, cy, radius, paint);
                break;
            case PLAYER2:
                //border color
                paint.setColor(Color.BLACK);
                canvas.drawCircle(cx, cy, radius, paint);
                //inside color
                paint.setColor(player.getColor());
                canvas.drawCircle(cx, cy, radius-2, paint);
                break;
            default:
                break;
        }
    }

    private void updateBoard(Player player, short column, short row) {
        this.updateBoard(player, column, row, false);
    }

    private void updateBoard(Player player, short column, short row, boolean suggestion) {
        this.gameBoard[column][row].setPlayer(player);
        this.gameBoard[column][row].setSuggestion(suggestion);
    }

    private void drawPositions(Canvas canvas) {
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
                int cellMediumX = (column * this.squareWidth + (column + 1) * this.squareWidth) / 2;
                int cellMediumY = (row * this.squareHeight + (row + 1) * this.squareHeight) / 2;

                // applying the margins
                int cx = cellMediumX + LEFT_MARGIN;
                int cy = cellMediumY + TOP_MARGIN;

                // the radius
                int radius = (this.squareWidth - 2) / 2 - 2;

                //update squares
                this.gameBoard[column][row].setRadius(radius);
                this.gameBoard[column][row].setSquareMediumX(cx);
                this.gameBoard[column][row].setSquareMediumY(cy);
            }
        }
    }

    private void first() {
        if (this.currentPlayer == Player.PLAYER1) {
            //AllowedMovements for Player
            List<Movement> list = allowedMovements(this.currentPlayer);

            //UpdateBoard with suggestions
            for (Movement movement : list) {
                updateBoard(this.currentPlayer, movement.getColumn(), movement.getRow(), true);
            }


            //Draw board
            Canvas canvas = getHolder().lockCanvas();
            drawGrid(canvas);
            drawPositions(canvas);
            getHolder().unlockCanvasAndPost(canvas);

            this.isEnableUserTouch = true;
        }
        else {
            //The IA is always PLAYER2 ?
            //Launch IA Thread.
        }
    }

    private List<Movement> allowedMovements(Player player) {
        List<Movement> list = new ArrayList<Movement>();

        for (short column = 0; column < NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < NUMBER_OF_ROWS; row++) {
                if (CheckMovement.empty(gameBoard, column, row) &&
                        (CheckMovement.diagonal(gameBoard, column, row, player) ||
                                CheckMovement.horizontal(gameBoard, column, row, player) ||
                                CheckMovement.vertical(gameBoard, column, row, player))) {
                    list.add(new Movement(row, column));
                }
            }
        }

        return list;
    }
}
