package de.android.reversi;

public class Square implements Cloneable {
    private Player player;
    private boolean suggestion;
    private int squareMediumX;
    private int squareMediumY;
    private int radius;

    //Default constructor.
    public Square () {
        this.player = Player.NOPLAYER;
    }

    public boolean isSuggestion() {
        return suggestion;
    }

    public void setSuggestion(final boolean suggestion) {
        this.suggestion = suggestion;
    }

    public int getSquareMediumX() {
        return squareMediumX;
    }

    public void setSquareMediumX(final int squareMediumX) {
        this.squareMediumX = squareMediumX;
    }

    public int getSquareMediumY() {
        return squareMediumY;
    }

    public void setSquareMediumY(final int squareMediumY) {
        this.squareMediumY = squareMediumY;
    }

    public void setPlayer (final Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setRadius (final int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }
    @Override
    public Square clone() {
        try {
            final Square result = (Square) super.clone();
            return result;
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
