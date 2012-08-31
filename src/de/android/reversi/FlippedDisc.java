package de.android.reversi;

public class FlippedDisc {
    private final short row;
    private final short column;

    public FlippedDisc(final short row, final short column) {
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
