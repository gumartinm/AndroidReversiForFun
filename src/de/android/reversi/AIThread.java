package de.android.reversi;

import java.util.List;

public class AIThread extends Thread {
    private List<Movement> listAllowedMovements;
    private final Board board;
    private final Player player;


    public AIThread(final Board board, final Player player, final String nameThread) {
        super(nameThread);
        this.board = board;
        this.player = player;
    }

    public void setListAllowedMovements(final List<Movement> listAllowedMovements) {
        this.listAllowedMovements = listAllowedMovements;
    }
}
