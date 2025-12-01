package core;

import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private long seed;
    private int playerX;
    private int playerY;

    public GameState(long seed, int playerX, int playerY) {
        this.seed = seed;
        this.playerX = playerX;
        this.playerY = playerY;
    }

    // Getters
    public long getSeed() { return seed; }
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
}