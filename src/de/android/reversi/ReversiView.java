package de.android.reversi;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import de.android.reversi.logic.ReversiLogic;


public class ReversiView extends SurfaceView {
    private final Board board = new Board();
    private final Player AI = Player.PLAYER1;
    int player1Score;
    int player2Score;

    private final Context context;

    //¿Funciona bien volatile con enum? Ver mi codigo de Singletons y enums.
    private volatile Player currentPlayer = AI;
    private volatile boolean isEnableUserTouch;

    private List<Position> listAllowedPositions;


    public ReversiView(final Context context) {
        super(context);
        this.context = context;
        board.initBoard();
        this.init();
    }

    public ReversiView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        board.initBoard();
        this.init();
    }

    public ReversiView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        board.initBoard();
        this.init();
    }

    private void drawGrid(final Canvas canvas) {
        //Canvas background
        final Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);


        //Board (green)
        paint.setColor(Color.rgb(0, 158, 11));
        canvas.drawRect(Board.LEFT_MARGIN, Board.TOP_MARGIN,
                Board.NUMBER_OF_COLUMNS * board.getSquareWidth() + Board.LEFT_MARGIN,
                Board.NUMBER_OF_ROWS * board.getSquareWidth() + Board.TOP_MARGIN, paint);


        //Lines creating squares on the board
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        for (int col = 0; col <= Board.NUMBER_OF_COLUMNS; col++) {
            // vertical lines
            final int x = col * board.getSquareWidth() + Board.LEFT_MARGIN;
            canvas.drawLine(x, Board.TOP_MARGIN, x, board.getCanvasWidth() - Board.TOP_MARGIN * 1, paint);
        }
        for (int row = 0; row < Board.NUMBER_OF_ROWS + 1; row++) {
            final int y = row * board.getSquareWidth() + Board.TOP_MARGIN;
            // horizontal lines
            canvas.drawLine(Board.LEFT_MARGIN, y, board.getCanvasWidth() - (Board.LEFT_MARGIN * 1), y, paint);
        }

    }

    private void init() {

        getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                //Black
                board.makeMove(Player.PLAYER1, (short)3, (short)4);
                board.makeMove(Player.PLAYER1, (short)4, (short)3);
                //White
                board.makeMove(Player.PLAYER2, (short)4, (short)4);
                board.makeMove(Player.PLAYER2, (short)3, (short)3);

                //AllowedPositions for Player
                if (currentPlayer != AI) {
                    listAllowedPositions = board.allowedPositions(currentPlayer);

                    //UpdateBoard with suggestions
                    for (final Position movement : listAllowedPositions) {
                        board.makeMove(currentPlayer, movement.getColumn(), movement.getRow(), true);
                    }
                }
            }

            @Override
            public void surfaceChanged(final SurfaceHolder holder, final int format,
                    final int width, final int height) {
                final Canvas canvas = holder.lockCanvas();
                board.calculateGraphicParameters(width, height);
                board.updateSquareParameters(board.getSquareWidth());
                drawGrid(canvas);
                drawPositions(canvas);
                holder.unlockCanvasAndPost(canvas);
                final Thread AIThread = new Thread(new Runnable(){

                    @Override
                    public void run() {
                        final AI ai = new AI(currentPlayer);

                        final Position move = ai.getBestMove(board);

                        if (move != null) {
                            mainLoop(move);
                        }
                        else {
                            int idString;
                            if (player1Score > player2Score) {
                                idString = R.string.player1WON;
                            } else {
                                idString = R.string.player2WON;
                            }
                            final DialogFragment newFragment = ErrorDialogFragment.newInstance(idString);
                            newFragment.show(((Activity)context).getFragmentManager(), "errorDialog");
                        }

                    }

                }, "AI-Thread");

                AIThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

                    @Override
                    public void uncaughtException(final Thread thread, final Throwable ex) {
                        Log.e("AIThread", "Unexpected exception. Thread: " + thread.getName(), ex);
                    }

                });


                AIThread.start();
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
            final short column = board.transformCoordinateXInColumn(event.getX());
            final short row = board.transformCoordinateYInRow(event.getY());

            if (row != -1 && column != -1 ) {
                Position movement;
                if((movement = ReversiLogic.retrieveAllowedPosition(row, column,
                        listAllowedPositions)) != null) {
                    board.removeSuggestionsFromBoard(listAllowedPositions);
                    this.mainLoop(movement);
                }
            }
        }

        return true;
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

    private void drawPositions(final Canvas canvas) {
        player1Score = 0;
        player2Score = 0;

        for (short column = 0; column < Board.NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < Board.NUMBER_OF_ROWS; row++) {
                if (board.getGameBoard()[column][row].getPlayer() != Player.NOPLAYER) {
                    if (board.getGameBoard()[column][row].getPlayer() == Player.PLAYER1 &&
                            !board.getGameBoard()[column][row].isSuggestion() ) {
                        player1Score++;
                    }
                    if (board.getGameBoard()[column][row].getPlayer() == Player.PLAYER2 &&
                            !board.getGameBoard()[column][row].isSuggestion() ) {
                        player2Score++;
                    }
                    drawDisk(canvas, board.getGameBoard()[column][row], column, row);
                }
            }
        }

        //TODO Really? I must redesign all of this :(
        final Handler updateUI = this.getHandler();
        updateUI.post(new Runnable() {

            @Override
            public void run() {
                ((TextView)((Activity)context).findViewById(R.id.txtPlayer1Score)).setText(String.format(" %d %s", player1Score, "discs"));
                ((TextView)((Activity)context).findViewById(R.id.txtPlayer2Score)).setText(String.format(" %d %s", player2Score, "discs"));
            }

        });

    }

    private void mainLoop(final Position position) {

        board.makeMove(this.currentPlayer, position.getColumn(), position.getRow());
        board.flipOpponentDiscs(position, currentPlayer);

        Canvas canvas = getHolder().lockCanvas();
        drawGrid(canvas);
        drawPositions(canvas);
        getHolder().unlockCanvasAndPost(canvas);

        //Switch player.
        this.currentPlayer = ReversiLogic.opponent(this.currentPlayer);


        if (this.currentPlayer != this.AI) {
            //AllowedPositions for player.
            listAllowedPositions = board.allowedPositions(currentPlayer);

            if (listAllowedPositions.isEmpty()) {
                int idString;
                if (player1Score > player2Score) {
                    idString = R.string.player1WON;
                } else {
                    idString = R.string.player2WON;
                }
                final DialogFragment newFragment = ErrorDialogFragment.newInstance(idString);
                newFragment.show(((Activity)context).getFragmentManager(), "errorDialog");
            }

            //UpdateBoard with suggestions
            for (final Position suggestedPosition : listAllowedPositions) {
                board.makeMove(currentPlayer, suggestedPosition.getColumn(), suggestedPosition.getRow(), true);
            }


            canvas = getHolder().lockCanvas();
            drawGrid(canvas);
            drawPositions(canvas);
            getHolder().unlockCanvasAndPost(canvas);

            this.isEnableUserTouch = true;



            //Going to wait for touch event from human player.
        }
        else {

            final Thread AIThread = new Thread(new Runnable(){

                @Override
                public void run() {
                    final AI ai = new AI(currentPlayer);
                    final Position move = ai.getBestMove(board);

                    if (move != null) {
                        mainLoop(move);
                    }
                    else {
                        int idString;
                        if (player1Score > player2Score) {
                            idString = R.string.player1WON;
                        } else {
                            idString = R.string.player2WON;
                        }
                        final DialogFragment newFragment = ErrorDialogFragment.newInstance(idString);
                        newFragment.show(((Activity)context).getFragmentManager(), "errorDialog");
                    }
                }

            }, "AI-Thread");

            AIThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(final Thread thread, final Throwable ex) {
                    Log.e("AIThread", "Unexpected exception. Thread: " + thread.getName(), ex);
                }

            });
            this.isEnableUserTouch = false;



            AIThread.start();
        }
    }

    public static class ErrorDialogFragment extends DialogFragment {

        public static ErrorDialogFragment newInstance(final int title) {
            final ErrorDialogFragment frag = new ErrorDialogFragment();
            final Bundle args = new Bundle();

            args.putInt("title", title);
            frag.setArguments(args);

            return frag;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final int title = getArguments().getInt("title");

            return new AlertDialog.Builder(getActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(title)
            .setPositiveButton("", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int whichButton) {

                }
            }).create();
        }
    }
}
