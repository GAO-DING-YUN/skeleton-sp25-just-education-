package core;

import tileengine.TERenderer;

public class Main {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 50;
    private static final long SEED = 1234567;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        World gameWorld = new World(WIDTH, HEIGHT, SEED);
        ter.initialize(WIDTH, HEIGHT);
        gameWorld.generateWorld();
        ter.renderFrame(gameWorld.getWorldGrid());
    }
}
