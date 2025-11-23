package core;

import tileengine.TETile;
import utils.RandomUtils;

import java.util.Random;

public class Room {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final int centerX;
    private final int centerY;

    public Room(Random random, int worldWidth, int worldHeight, int minWidth, int maxWidth, int minHeight, int maxHeight) {
        width = RandomUtils.uniform(random, minWidth, maxWidth + 1);
        height = RandomUtils.uniform(random, minHeight, maxHeight + 1);
        int maxX = worldWidth - width - 1;
        int maxY = worldHeight - height - 1;
        x = RandomUtils.uniform(random, 1, maxX + 1);
        y = RandomUtils.uniform(random, 1, maxY + 1);
        centerX = x + width / 2;
        centerY = y + height / 2;
    }

    public boolean isOverLapping(Room other) {
        int minGap = 1;
        int myRight = x + width - 1;
        int myTop = y + height - 1;
        int otherRight = other.x + other.width - 1;
        int otherTop = other.y + other.height - 1;

        boolean noOverlap =
                myRight + minGap < other.x
                        || otherRight + minGap < this.x
                        || myTop + minGap < other.y
                        || otherTop + minGap < this.y;
        return !noOverlap;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCenterX() { return centerX; }
    public int getCenterY() { return centerY; }
}