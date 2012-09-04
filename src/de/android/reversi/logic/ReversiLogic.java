package de.android.reversi.logic;

import java.util.List;

import de.android.reversi.Player;
import de.android.reversi.Position;

public final class ReversiLogic {

    public static Position retrieveAllowedPosition(final short row, final short column,
            final List<Position> listAllowedPositions) {
        for (final Position movement : listAllowedPositions) {
            if ((movement.getRow() == row) && (movement.getColumn() == column)) {
                return movement;
            }
        }

        return null;
    }

    public static Player opponent(final Player currentPlayer) {
        switch (currentPlayer){
            case PLAYER1:
                return Player.PLAYER2;
            case PLAYER2:
                return Player.PLAYER1;
            default:
                return Player.NOPLAYER;
        }
    }
}
