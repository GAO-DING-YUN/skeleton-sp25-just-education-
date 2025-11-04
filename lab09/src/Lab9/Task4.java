package Lab9;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import java.util.Stack;
import java.io.File;
import java.util.Random;

public class Task4 {

    private static final int WIDTH = 30;
    private static final int HEIGHT = 20;
    private static final int screenHEIGHT = 15;
    private static final int SEED = 543;
    private static final int num = 1;
    private static final Stack<TETile[][]> history = new Stack<>();


    private static void fillWithTrees(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < screenHEIGHT; j++) {
                world[i][j] = Tileset.TREE;
            }
        }
    }

    private static TETile[][] copyWorld(TETile[][] world) {
        TETile[][] copy = new TETile[WIDTH][screenHEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            System.arraycopy(world[i], 0, copy[i], 0, screenHEIGHT);
        }
        return copy;
    }

    private static void drawSquare(TETile[][] world, int startX, int startY, int size, TETile tile) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = startX + i;
                int y = startY - j;
                if (x >= 0 && x < WIDTH && y >= 0 && y < screenHEIGHT) {
                    world[x][y] = tile;
                }
            }
        }
    }

    private static void addRandomSquare(TETile[][] world, Random rand) {
        int x = rand.nextInt(WIDTH);
        int y = rand.nextInt(screenHEIGHT);
        int size = rand.nextInt(3, 8);
        TETile tile = ranDowTile(rand);
        drawSquare(world, x, y, size, tile);
    }

    private static TETile ranDowTile(Random random) {
        int tileNum = random.nextInt(3);
        return switch (tileNum) {
            case 0 -> Tileset.WALL;
            case 1 -> Tileset.FLOWER;
            default -> Tileset.WATER;
        };
    }

    private static void createSquares(TETile[][] world, Random random) {
        for (int i = 0; i < num; i++) {
            addRandomSquare(world, random);
        }
    }

    private static void saveGame(String fileName, int cnt) {
        if (cnt == 0) {
            System.out.println("警告：cnt为0，文件将为空！");
        }
        Out out = new Out(fileName);
        for (int i = 0; i < cnt; i++) {
            out.println("n");
        }
        out.close();
    }

    private static void loadGame(TETile[][] world, String fileName, int[] cnt, Random[] randomRef) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("文件不存在，无法加载！");
            return;
        }

        history.clear();
        In in = new In(file);
        Random newRandom = new Random(SEED);
        cnt[0] = 0;
        fillWithTrees(world);

        String getLine;
        while (in.hasNextLine()) {
            getLine = in.readLine();
            if (getLine.equals("n")) {
                cnt[0]++;
                createSquares(world, newRandom);
            }
        }
        randomRef[0] = newRandom;
        in.close();
    }

    private static void deleteStep(TETile[][] world, int[] cnt) {
        if (!history.isEmpty()) {
            TETile[][] prevWorld = history.pop();
            for (int i = 0; i < WIDTH; i++) {
                System.arraycopy(prevWorld[i], 0, world[i], 0, screenHEIGHT);
            }
            cnt[0]--;
        }
    }

    public static void main(String[] args) {
        String fileName = "game.txt";
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][screenHEIGHT];
        fillWithTrees(world);

        Random random = new Random(SEED);
        Random[] randomRef = {random};
        int[] cnt = {0};

        char c;
        while (true) {
            while (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);

                switch (c) {
                    case 'n':
                        history.push(copyWorld(world));
                        cnt[0]++;
                        createSquares(world, random);
                        break;
                    case 'q':
                        System.exit(0);
                        break;
                    case 's':
                        saveGame(fileName, cnt[0]); // 传递当前cnt
                        break;
                    case 'l':
                        loadGame(world, fileName, cnt, randomRef);
                        random = randomRef[0];
                        break;
                    case 'd':
                        deleteStep(world, cnt);
                        break;
                    default:
                        break;
                }
            }
            ter.renderFrame(world);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.textLeft(1, 17, "Number of squares: " + cnt[0]);
            StdDraw.show();
            StdDraw.pause(50);
        }
    }
}