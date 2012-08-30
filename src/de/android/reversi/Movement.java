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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + column;
        result = prime * result + row;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Movement)) {
            return false;
        }
        Movement other = (Movement) obj;
        if (column != other.column) {
            return false;
        }
        if (row != other.row) {
            return false;
        }
        return true;
    }
}
