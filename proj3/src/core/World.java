package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Integer.*;

public class World {
    private static final int minRoomCount = 10;
    private static final int maxRoomCount = 14;
    private static final int minRoomWidth = 4;
    private static final int maxRoomWidth = 8;
    private static final int minRoomHeight = 3;
    private static final int maxRoomHeight = 6;

    private final List<Room> roomList;
    private final TETile[][] world;
    private final Random random;
    private final int width;
    private final int height;
    private final Hallway hallway;

    public World(int width, int height, long SEED) {
        world = new TETile[width][height];
        random = new Random(SEED);
        this.width = width;
        this.height = height;
        hallway = new Hallway(random, world, width, height);
        roomList = new ArrayList<>();
        initializeWorld();
    }

    private void initializeWorld() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    public void generateWorld() {
        generateRoom();
        connectHallway();
    }

    private void connectHallway() {
        if (roomList.size() < 2) return;
        roomList.sort( (r1, r2) -> {
            return compare(r1.getX(), r2.getX());
        });
        for (int i = 1; i < roomList.size(); i++) {
            Room prev = roomList.get(i - 1);
            Room current = roomList.get(i);
            hallway.connectTwoRooms(prev, current);
        }
    }

    private void generateRoom() {
        int roomCount = random.nextInt(minRoomCount, maxRoomCount + 1);
        int maxRetries = 500;
        while (roomList.size() < roomCount && maxRetries > 0) {
            Room newRoom = new Room(random, width, height, minRoomWidth, maxRoomWidth, minRoomHeight, maxRoomHeight);
            boolean isOver = false;
            for (Room hasRoom : roomList) {
                if (newRoom.isOverLapping(hasRoom)){
                    isOver = true;
                    break;
                }
            }
            if (!isOver) {
                roomList.add(newRoom);
                drawRoomGrid(newRoom);
                maxRetries = 500;
            }
            maxRetries--;
        }
        if (roomList.size() < minRoomCount) {
            System.out.println("警告：房间生成重试耗尽，实际生成 " + roomList.size() + " 个（目标 " + roomCount + " 个）");
        }
    }

    private void drawRoomGrid(Room newRoom) {
        int x = newRoom.getX();
        int y = newRoom.getY();
        int w = newRoom.getWidth();
        int h = newRoom.getHeight();

        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }

        for (int j = y - 1; j <= y + h; j++) {
            if (isValidCoordinate(x - 1, j)) world[x - 1][j] = Tileset.WALL;
            if (isValidCoordinate(x + w, j)) world[x + w][j] = Tileset.WALL;
        }
        for (int i = x - 1; i <= x + w; i++) {
            if (isValidCoordinate(i, y - 1)) world[i][y - 1] = Tileset.WALL;
            if (isValidCoordinate(i, y + h)) world[i][y + h] = Tileset.WALL;
        }
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public TETile[][] getWorldGrid() {
        return world;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
