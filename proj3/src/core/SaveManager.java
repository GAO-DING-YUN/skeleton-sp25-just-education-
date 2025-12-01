package core;

import java.io.*;

public class SaveManager {
    private static final String SAVE_FILE = "game_save.ser";

    public static void save(GameState state) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(state);
            System.out.println("游戏已保存到 " + SAVE_FILE);
        } catch (IOException e) {
            System.err.println("保存失败: " + e.getMessage());
        }
    }

    public static GameState load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (GameState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载失败或无存档: " + e.getMessage());
            return null;
        }
    }
}
