package de.android.reversi;

public class Movement {
    private final short row;
    private final short column;

    public Movement(short row, short column) {
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
