package de.android.reversi;

public class Square {
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

    public void setSuggestion(boolean suggestion) {
        this.suggestion = suggestion;
    }

    public int getSquareMediumX() {
        return squareMediumX;
    }

    public void setSquareMediumX(int squareMediumX) {
        this.squareMediumX = squareMediumX;
    }

    public int getSquareMediumY() {
        return squareMediumY;
    }

    public void setSquareMediumY(int squareMediumY) {
        this.squareMediumY = squareMediumY;
    }

    public void setPlayer (Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setRadius (int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }
}
