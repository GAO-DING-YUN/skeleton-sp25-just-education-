package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class Player {
    private  final long SEED;
    private  final World gameWorld;
    private int x;
    private int y;
    private final TETile[][] worldGrid;

    public Player(World World, long seed) {
        SEED = seed;
        gameWorld = World;
        worldGrid = World.getWorldGrid();
        initializePosition();
    }

    private void initializePosition() {
        Random random = new Random(SEED);
        int width = gameWorld.getWidth();
        int height = gameWorld.getHeight();

        while (true) {
            int randX = random.nextInt(width);
            int randY = random.nextInt(height);
            if (worldGrid[randX][randY] == Tileset.FLOOR) {
                this.x = randX;
                this.y = randY;
                break;
            }
        }
    }

    public void move(char direction) {
        int newX = x;
        int newY = y;
        int width = gameWorld.getWidth();
        int height = gameWorld.getHeight();

        // 按 W/A/S/D 对应上下左右（世界坐标系y向上）
        switch (Character.toUpperCase(direction)) {
            case 'W':
                newY++;
                break; // 上：y+1
            case 'S':
                newY--;
                break; // 下：y-1
            case 'A':
                newX--;
                break; // 左：x-1
            case 'D':
                newX++;
                break; // 右：x+1
            default:
                return; // 忽略无效按键
        }

        // 检查新位置是否合法（在世界范围内 + 是地板）
        if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
            if (worldGrid[newX][newY] == Tileset.FLOOR) {
                this.x = newX;
                this.y = newY;
            }
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setPosition(int playerX, int playerY) {
        x = playerX;
        y = playerY;
    }
}
