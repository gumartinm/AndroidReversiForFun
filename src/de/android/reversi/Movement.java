package de.android.reversi;

import java.util.ArrayList;
import java.util.List;


public class Movement {
    private final short row;
    private final short column;
    private final List<FlippedDisc> flippedDiscs = new ArrayList<FlippedDisc>();

    public List<FlippedDisc> getFlippedDiscs() {
        return flippedDiscs;
    }

    public Movement(final short row, final short column) {
        this.row = row;
        this.column = column;
    }

    public short getRow() {
        return row;
    }

    public short getColumn() {
        return column;
    }
}
