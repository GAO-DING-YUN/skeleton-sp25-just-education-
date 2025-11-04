package Lab9;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class Task2 {
    private static final int WIDTH = 30;
    private static final int HEIGHT = 20;
    private static final int SEED = 543;
    private static final Random RANDOM = new Random(SEED);
    private static final int num = 5;

    private static void fillWithTrees(TETile[][] world) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 15; j++) {
                world[i][j] = Tileset.TREE;
            }
        }
    }

    private static void drawSquare(TETile[][] world, int startX, int startY, int size, TETile tile) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = startX + i;
                int y = startY - j;
                if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) {
                    continue;
                }
                world[x][y] = tile;
            }
        }
    }

    private static void addRandomSquare(TETile[][] world, Random rand) {
        int x = rand.nextInt(30);
        int y = rand.nextInt(15);
        int size = rand.nextInt(3, 8);
        TETile tile = ranDowTile();
        drawSquare(world, x, y, size, tile);
    }

    private static TETile ranDowTile() {
        int tileNum = RANDOM.nextInt(3);
        return switch (tileNum) {
            case 0 -> Tileset.WALL;
            case 1 -> Tileset.FLOWER;
            default -> Tileset.WATER;
        };
    }

    private static void createSquares(TETile[][] world, Random rand) {
        for (int i = 0; i < num; i++) {
            addRandomSquare(world, rand);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[30][15];
        fillWithTrees(world);
        createSquares(world, RANDOM);
        ter.renderFrame(world);
    }
}
