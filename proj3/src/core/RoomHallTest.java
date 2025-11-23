package core;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomHallTest {
    private static final int WORLD_WIDTH = 80;
    private static final int WORLD_HEIGHT = 50;
    private static final long SEED = 123456L;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);

        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        Random random = new Random(SEED);
        List<Room> roomList = generateRooms(random, world);

        //用Hallway连接所有房间（链式连接：0→1→2→3→4）
        Hallway hallway = new Hallway(random, world, WORLD_WIDTH, WORLD_HEIGHT);
        for (int i = 1; i < roomList.size(); i++) {
            Room prevRoom = roomList.get(i - 1);
            Room currRoom = roomList.get(i);
            hallway.connectTwoRooms(prevRoom, currRoom);
            System.out.printf("连接房间%d（%d,%d）和房间%d（%d,%d）\n",
                    i-1, prevRoom.getCenterX(), prevRoom.getCenterY(),
                    i, currRoom.getCenterX(), currRoom.getCenterY());
        }

        ter.renderFrame(world);
        System.out.println("生成房间数量：" + roomList.size());
        System.out.println("走廊连接完成，无越界、无重叠");
}

    private static List<Room> generateRooms(Random random, TETile[][] world) {
        List<Room> roomList = new ArrayList<>();
        int maxTries = 1000;
        int targetRoom = 5;
        while (targetRoom > roomList.size() && maxTries >= 0) {
            Room newRoom = new Room(random, WORLD_WIDTH, WORLD_HEIGHT, 4, 8, 3, 6);
            boolean isOver = false;
            for (Room readyRoom : roomList) {
                if (newRoom.isOverLapping(readyRoom)) {
                    isOver = true;
                    break;
                }
            }
            if (!isOver) {
                roomList.add(newRoom);
                drawRoomToWorld(newRoom, world);
            }
            maxTries--;
        }
        return roomList;
    }

    private static void drawRoomToWorld(Room newRoom, TETile[][] world) {
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
            if (x - 1 >= 0 && j >= 0 && j < world[0].length) world[x - 1][j] = Tileset.WALL;
            if (x + w < world.length && j >= 0 && j < world[0].length) world[x + w][j] = Tileset.WALL;
        }
        for (int i = x - 1; i <= x + w; i++) {
            if (i >= 0 && i < world.length && y - 1 >= 0) world[i][y - 1] = Tileset.WALL;
            if (i >= 0 && i < world.length && y + h < world[0].length) world[i][y + h] = Tileset.WALL;
        }
    }

}
