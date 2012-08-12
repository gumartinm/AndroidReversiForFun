package de.android.reversi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class ReversiView extends SurfaceView {
    private static final String TAG = "GameBoard";

    /**
     * The number of columns of this board
     */
    private static int NUMBER_OF_COLUMNS = 8;

    /**
     * The number of rows of this board
     */
    private static int NUMBER_OF_ROWS = 8;

    /**
     * The top margin
     */
    private static int TOP_MARGIN = 0;

    /**
     * Vertical margin
     */
    private static int LEFT_MARGIN = 0;

    private int squareWidth;
    private int squareHeight;
    private int canvasHeight;
    private int canvasWidth;
    private final int gameBoard[][] = new int[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];


    public ReversiView(Context context) {
        super(context);
        this.initialize();
    }

    public ReversiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public ReversiView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
                updateGrid(Player.PLAYER1.getPlayerNumber(), 3, 3);
                updateGrid(Player.PLAYER1.getPlayerNumber(), 4, 4);
                //Black
                updateGrid(Player.PLAYER2.getPlayerNumber(), 4, 3);
                updateGrid(Player.PLAYER2.getPlayerNumber(), 3, 4);

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                    int width, int height) {
                Canvas canvas = holder.lockCanvas();
                calculateGraphicParameters(canvas, width, height);
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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Hidden pointer
            int column = transformCoordinateXInColumn(event.getX());
            int row = transformCoordinateYInRow(event.getY());
            if (row != -1 && column != -1 ) {
                updateGrid(Player.PLAYER2.getPlayerNumber(), column, row);
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

    private int transformCoordinateYInRow(float y) {

        int row = (int) ((y - TOP_MARGIN) / this.squareWidth);

        // if tapped outside the board
        if (row < 0 || row >= NUMBER_OF_ROWS) {
            row = -1;
        }

        return row;
    }

    private int transformCoordinateXInColumn(float x) {

        int column = (int) ((x - LEFT_MARGIN) / this.squareWidth);

        // if tapped outside the board
        if (column < 0 || column >= NUMBER_OF_COLUMNS) {
            column = -1;
        }

        return column;
    }

    private void drawChip(Canvas canvas, int player, int column, int row) {
        if (player != 0) {
            // calculating the center of the cell
            int cellMediumX = (column * this.squareWidth + (column + 1) * this.squareWidth) / 2;
            int cellMediumY = (row * this.squareHeight + (row + 1) * this.squareHeight) / 2;

            // applying the margins
            int cx = cellMediumX + LEFT_MARGIN;
            int cy = cellMediumY + TOP_MARGIN;
            // now the radius
            int radius = (this.squareWidth - 2) / 2 - 2;

            this.drawCircle(canvas, player, cx, cy, radius);
        }
    }

    private void drawCircle(Canvas canvas, int player, int cx, int cy, int radius) {

        Paint paint = new Paint();

        paint.setColor(Player.getPlayer(player).getColor());
        //paint.setAntiAlias(true);

        canvas.drawCircle(cx, cy, radius, paint);
    }

    private void updateGrid(int player, int column, int row) {
        this.gameBoard[column][row] = player;
    }

    private void drawPositions(Canvas canvas) {
        for (int column = 0; column < NUMBER_OF_COLUMNS; column++) {
            for (int row = 0; row < NUMBER_OF_ROWS; row++) {
                drawChip(canvas, this.gameBoard[column][row], column, row);
            }
        }
    }
}
