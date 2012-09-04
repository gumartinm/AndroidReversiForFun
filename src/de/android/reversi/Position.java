package de.android.reversi;



public class Position {
    private final short row;
    private final short column;

    public Position(final short row, final short column) {
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
