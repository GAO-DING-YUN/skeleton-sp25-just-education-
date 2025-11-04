package Lab9;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

/**
 * Draws a world initially full of trees.
 */
public class Task1 {
    /**
     * Fills the entire 2D world with the Tileset.TREE tile.
     */
    private static void fillWithTrees(TETile[][] world) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 15; j++) {
                world[i][j] = Tileset.TREE;
            }
        }
        return;
    }

    private static final int WIDTH = 30;
    private static final int HEIGHT = 20;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[30][15];
        fillWithTrees(world);
        ter.renderFrame(world);
    }
}