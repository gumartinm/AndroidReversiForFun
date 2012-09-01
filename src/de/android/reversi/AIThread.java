package de.android.reversi;

import android.util.Log;

public class AIThread extends Thread {
    private static final String TAG = "AIThread";
    private final Board board;
    private final Player player;


    public AIThread(final Board board, final Player player) {
        super("AI-Thread");
        this.setUncaughtExceptionHandler(new UnExpectedException());
        this.board = board;
        this.player = player;
    }

    private class UnExpectedException implements UncaughtExceptionHandler {

        @Override
        public void uncaughtException(final Thread thread, final Throwable ex) {
            Log.e(TAG, "Unexpected exception. Thread: " + thread.getName(), ex);
        }
    }
}
