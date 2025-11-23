package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class Hallway {
    private final Random random;
    private final TETile[][] worldGrid;
    private final int worldWidth;
    private final int worldHeight;

    public Hallway(Random random, TETile[][] worldGrid, int worldWidth, int worldHeight) {
        this.random = random;
        this.worldGrid = worldGrid;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void connectTwoRooms(Room r1, Room r2) {
        int startX = r1.getCenterX();
        int startY = r1.getCenterY();
        int endX = r2.getCenterX();
        int endY = r2.getCenterY();

        boolean horizontalFirst = random.nextBoolean();
        if (horizontalFirst) {
            drawHorizontalCorridor(startX, endX, startY);
            drawVerticalCorridor(startY, endY, endX);
        } else {
            drawVerticalCorridor(startY, endY, startX);
            drawHorizontalCorridor(startX, endX, endY);
        }
    }

    private void setFloor(int x, int y) {
        if (x >=worldWidth || x < 0 || y < 0 || y >= worldHeight) {
            return;
        }
        if (worldGrid[x][y] == Tileset.WALL || worldGrid[x][y] == Tileset.NOTHING) {
            worldGrid[x][y] = Tileset.FLOOR;
        }

    }

    private void setWall(int x, int y) {
        if (x >=worldWidth || x < 0 || y < 0 || y >= worldHeight) {
            return;
        }
        if (worldGrid[x][y] == Tileset.NOTHING) {
            worldGrid[x][y] = Tileset.WALL;
        }
    }

    private void drawHorizontalCorridor(int startX, int endX, int startY) {
        int min = Math.min(startX, endX);
        int max = Math.max(startX, endX);

        for (int i = min; i <= max; i++) {
            setFloor(i, startY);
            setWall(i, startY - 1);
            setWall(i, startY + 1);
        }
    }

    private void drawVerticalCorridor(int startY, int endY, int endX) {
        int min = Math.min(startY, endY);
        int max = Math.max(startY, endY);

        for (int i = min; i <= max; i++) {
            setFloor(endX, i);
            setWall(endX- 1, i);
            setWall(endX + 1, i);
        }
    }

}
