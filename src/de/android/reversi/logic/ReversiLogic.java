package de.android.reversi.logic;

import java.util.ArrayList;
import java.util.List;

import de.android.reversi.Board;
import de.android.reversi.CheckMovement;
import de.android.reversi.Movement;
import de.android.reversi.Player;
import de.android.reversi.Square;

public final class ReversiLogic {

    public static List<Movement> allowedMovements(final Player player, final Square gameBoard[][]) {
        final List<Movement> list = new ArrayList<Movement>();

        for (short column = 0; column < Board.NUMBER_OF_COLUMNS; column++) {
            for (short row = 0; row < Board.NUMBER_OF_ROWS; row++) {
                final Movement movement = new Movement(row, column);
                if (CheckMovement.empty(gameBoard, column, row)) {
                    final boolean diagonal = CheckMovement.diagonal(gameBoard, movement, player);
                    final boolean horizontal = CheckMovement.horizontal(gameBoard, movement, player);
                    final boolean vertical = CheckMovement.vertical(gameBoard, movement, player);

                    if(diagonal || horizontal || vertical) {
                        list.add(movement);
                    }
                }
            }
        }

        return list;
    }

    public static Movement retrieveAllowedMovement(final short row, final short column,
            final List<Movement> listAllowedMovements) {
        for (final Movement movement : listAllowedMovements) {
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
