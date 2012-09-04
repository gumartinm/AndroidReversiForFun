package de.android.reversi;

import android.graphics.Color;

//TODO: better a linked list
public enum Player {
    NOPLAYER {
        @Override
        int color() {
            return 0;
        }

    },

    PLAYER1 {
        @Override
        public int color() {
            return Color.BLACK;

        }
    },

    PLAYER2 {
        @Override
        int color() {
            return Color.WHITE;
        }
    };

    private final int color;

    private Player () {
        this.color = color();
    }

    public int getColor() {
        return this.color;
    }

    abstract int color();
}
