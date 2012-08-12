package de.android.reversi;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;

public enum Player {
    PLAYER1(1) {
        @Override
        public int color() {
            return Color.BLACK;

        }
    },
    PLAYER2(2) {
        @Override
        int color() {
            return Color.WHITE;
        }
    };

    private final int playerNumber;
    private final int color;
    private static final Map<Integer, Player> playerMap = new HashMap<Integer, Player>();


    static {
        for (Player player : Player.values())
        {
            playerMap.put(player.playerNumber, player);
        }
    }

    private Player (final int playerNumber) {
        this.playerNumber = playerNumber;
        this.color = color();
    }

    public int getPlayerNumber() {
        return this.playerNumber;
    }

    public int getColor() {
        return this.color;
    }

    public static final Player getPlayer (final int playerNumber)
    {
        return playerMap.get(playerNumber);
    }

    abstract int color();
}
