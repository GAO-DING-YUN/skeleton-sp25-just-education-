package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;

public class Main {
    // 窗口/游戏尺寸常量
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int Game_Width = 80;
    private static final int Game_Height = 50;
    // 字体常量
    private static final Font MAIN_FONT = new Font("Monaco", Font.BOLD, 30);
    private static final Font SMALL_FONT = new Font("Monaco", Font.PLAIN, 18);
    private static final Font BUTTON_FONT = new Font("Monaco", Font.BOLD, 22);



    // 游戏状态枚举
    private enum State {
        MAIN_MENU, ENTERING_SEED, PLAYING_GAME, QUIT
    }

    // 核心状态变量
    private GameState pendingLoadState = null;
    private State currentState;
    private String seedString;
    private long gameSeed;
    private String errorMessage;

    // 按钮定义（适配旧版StdDraw，修复整数除法）
    // 主菜单按钮
    private final Button btnNewGame = new Button((double)WIDTH/2, (double)HEIGHT/2, 240, 50, "新建游戏 (N)", Color.GRAY, Color.WHITE);
    private final Button btnLoadGame = new Button((double)WIDTH/2, (double)HEIGHT/2 - 70, 240, 50, "加载游戏 (L)", Color.GRAY, Color.WHITE);
    private final Button btnQuitGame = new Button((double)WIDTH/2, (double)HEIGHT/2 - 140, 240, 50, "退出游戏 (Q)", Color.GRAY, Color.WHITE);
    // Seed输入按钮
    private final Button btnConfirmSeed = new Button((double)WIDTH/2 + 100, (double)HEIGHT/2 - 100, 120, 40, "确认", Color.GRAY, Color.WHITE);
    private final Button btnBackMenu = new Button((double)WIDTH/2 - 100, (double)HEIGHT/2 - 100, 120, 40, "返回", Color.GRAY, Color.WHITE);
    private final Button btnDeleteChar = new Button((double)WIDTH/2, (double)HEIGHT/2 - 100, 120, 40, "删除", Color.GRAY, Color.WHITE);
    // 游戏控制按钮
    private final Button btnGameBack = new Button(5,  5, 2, 2, "返回", Color.DARK_GRAY, Color.WHITE);
    private final Button btnGameQuit = new Button(10,  5, 2, 2, "退出", Color.DARK_GRAY, Color.WHITE);

    public Main() {
        initWindow();
        resetState();
    }

    // 初始化StdDraw窗口（移除enableMouseEvents）
    private void initWindow() {
        StdDraw.setCanvasSize(WIDTH, HEIGHT);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.setFont(MAIN_FONT);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
    }

    // 重置游戏状态
    private void resetState() {
        this.currentState = State.MAIN_MENU;
        this.seedString = "";
        this.gameSeed = 0L;
        this.errorMessage = "";
    }

    // 游戏主循环
    public void start() {
        while (currentState != State.QUIT) {
            switch (currentState) {
                case MAIN_MENU:
                    handleMainMenu();
                    break;
                case ENTERING_SEED:
                    handleSeedInput();
                    break;
                case PLAYING_GAME:
                    handleGamePlay();
                    break;
            }
        }
        System.out.println("游戏已退出");
        System.exit(0);
    }

    // 处理主菜单逻辑
    private void handleMainMenu() {
        while (currentState == State.MAIN_MENU) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(MAIN_FONT);

            // 绘制标题（视觉居中）
            drawCenteredText("CS61B Support", HEIGHT - 100);

            // 绘制主菜单按钮
            btnNewGame.draw();
            btnLoadGame.draw();
            btnQuitGame.draw();

            // 检测按钮点击
            if (StdDraw.isMousePressed()) {
                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();
                if (btnNewGame.isClicked(mouseX, mouseY)) {
                    currentState = State.ENTERING_SEED;
                    this.seedString = "";
                    this.errorMessage = "";
                    StdDraw.pause(200);
                } else if (btnLoadGame.isClicked(mouseX, mouseY)) {
                    GameState saved = SaveManager.load();
                    if (saved != null) {
                        this.gameSeed = saved.getSeed();
                        this.pendingLoadState = saved;
                        // 进入游戏后会用 seed 重建世界，并设置玩家位置
                        currentState = State.PLAYING_GAME;
                    } else {
                        showTempMessage("无有效存档！");
                    }
                    StdDraw.pause(200);
                } else if (btnQuitGame.isClicked(mouseX, mouseY)) {
                    currentState = State.QUIT;
                    StdDraw.pause(200);
                }
            }

            // 原有键盘操作
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                switch (key) {
                    case 'N':
                        currentState = State.ENTERING_SEED;
                        this.seedString = "";
                        this.errorMessage = "";
                        break;
                    case 'L':
                        showTempMessage("加载游戏功能暂未实现");
                        break;
                    case 'Q':
                        currentState = State.QUIT;
                        break;
                    default:
                        showTempMessage("请按 N/L/Q 或点击按钮");
                        break;
                }
            }

            StdDraw.show();
            StdDraw.pause(20);
        }
    }

    // 处理Seed输入逻辑
    private void handleSeedInput() {
        while (currentState == State.ENTERING_SEED) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(MAIN_FONT);

            // 绘制Seed输入提示（视觉居中）
            drawCenteredText("请输入 Seed", HEIGHT - 100);
            drawCenteredText(seedString, (double)HEIGHT / 2);

            // 绘制Seed操作按钮
            btnConfirmSeed.draw();
            btnBackMenu.draw();
            btnDeleteChar.draw();

            // 绘制错误信息
            if (!errorMessage.isEmpty()) {
                StdDraw.setPenColor(StdDraw.RED);
                drawCenteredText(errorMessage, (double)HEIGHT / 2 - 50);
                StdDraw.setPenColor(StdDraw.WHITE);
            }

            // 绘制辅助提示
            StdDraw.setFont(SMALL_FONT);
            drawCenteredText("按回车确认 | 按退格删除 | 按 ESC 返回主菜单", 50);
            StdDraw.setFont(MAIN_FONT);

            // 检测按钮点击
            if (StdDraw.isMousePressed()) {
                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();
                if (btnConfirmSeed.isClicked(mouseX, mouseY)) {
                    confirmSeed();
                    StdDraw.pause(200);
                } else if (btnBackMenu.isClicked(mouseX, mouseY)) {
                    currentState = State.MAIN_MENU;
                    StdDraw.pause(200);
                } else if (btnDeleteChar.isClicked(mouseX, mouseY)) {
                    if (!seedString.isEmpty()) {
                        this.seedString = seedString.substring(0, seedString.length() - 1);
                    }
                    StdDraw.pause(200);
                }
            }

            // 原有键盘操作
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                this.errorMessage = "";

                if (Character.isDigit(key)) {
                    this.seedString += key;
                } else if (key == '\n' || key == '\r') {
                    confirmSeed();
                } else if (key == '\b' || key == 127) {
                    if (!seedString.isEmpty()) {
                        this.seedString = seedString.substring(0, seedString.length() - 1);
                    }
                } else if (key == 27) {
                    currentState = State.MAIN_MENU;
                } else {
                    this.errorMessage = "仅允许输入数字、回车、退格！";
                }
            }

            StdDraw.show();
            StdDraw.pause(20);
        }
    }

    // 处理游戏游玩逻辑
    private void handleGamePlay() {
        World gameWorld = new World(Game_Width, Game_Height, gameSeed);
        TERenderer ter = new TERenderer();
        gameWorld.generateWorld();

        boolean colonPressed = false;
        Player player;
        if (pendingLoadState != null) {
            player = new Player(gameWorld, gameSeed);
            player.setPosition(pendingLoadState.getPlayerX(), pendingLoadState.getPlayerY());
            pendingLoadState = null; // 清除
        } else {
            player = new Player(gameWorld, gameSeed);
        }
        ter.initialize(Game_Width, Game_Height);
        int clickTileX = -1;
        int clickTileY = -1;
        TETile clickedTile = null;

        while (currentState == State.PLAYING_GAME) {
            // 渲染游戏世界
            ter.resetFont();
            ter.renderFrame(gameWorld.getWorldGrid());
            StdDraw.setPenColor(StdDraw.WHITE);
            if (StdDraw.isMousePressed()) {
                // 获取鼠标点击的浮点坐标（网格坐标系）
                double mouseGridX = StdDraw.mouseX();
                double mouseGridY = StdDraw.mouseY();
                // 转换为整数网格坐标（取整，匹配 Tile 数组索引）
                clickTileX = (int) Math.floor(mouseGridX);
                clickTileY = (int) Math.floor(mouseGridY);

                // 边界校验：确保点击在 80×50 游戏世界内
                if (clickTileX >= 0 && clickTileX < Game_Width
                        && clickTileY >= 0 && clickTileY < Game_Height) {
                    clickedTile = gameWorld.getWorldGrid()[clickTileX][clickTileY]; // 获取点击的 Tile
                } else {
                    clickedTile = null; // 点击世界外，清空 Tile 信息
                }
                StdDraw.pause(100); // 防重复点击
            }
            if (clickedTile == Tileset.FLOOR) {
                StdDraw.textLeft(1, 46, "FLOOR");
            } else if (clickedTile == Tileset.WALL) {
                StdDraw.textLeft(1, 46, "WALL");
            }

            // 绘制玩家
            StdDraw.setPenColor(StdDraw.YELLOW);
            StdDraw.text(player.getX() + 0.5, player.getY() + 0.5, "C");

            // 绘制游戏控制按钮
            btnGameBack.draw();
            btnGameQuit.draw();

            // 检测按钮点击
            if (StdDraw.isMousePressed()) {
                double mouseX = StdDraw.mouseX();
                double mouseY = StdDraw.mouseY();
                 if (btnGameBack.isClicked(mouseX, mouseY)) {
                    currentState = State.MAIN_MENU;
                    StdDraw.pause(200);
                } else if (btnGameQuit.isClicked(mouseX, mouseY)) {
                    currentState = State.QUIT;
                    StdDraw.pause(200);
                }
            }

            // 原有键盘操作
            if (StdDraw.hasNextKeyTyped()) {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (key == ':') {
                    colonPressed = true;
                } else if (colonPressed && (key == 'Q' || key == 'q')) {
                    GameState state = new GameState(gameSeed, player.getX(), player.getY());
                    SaveManager.save(state);
                    showTempMessage("游戏已保存！");
                    colonPressed = false;
                } else if (colonPressed) {
                    colonPressed = false;
                }else if (key == 'Q') {
                    currentState = State.QUIT;
                } else if (key == 27) {
                    currentState = State.MAIN_MENU;
                } else if (key == 'W' || key == 'A' || key == 'S' || key == 'D') {
                    player.move(key);
                }
            }

            StdDraw.show();
            StdDraw.pause(10);
        }
        initWindow();
    }

    private void confirmSeed() {
        if (seedString.isEmpty()) {
            this.errorMessage = "Seed 不能为空！";
        } else {
            try {
                this.gameSeed = Long.parseLong(seedString);
                System.out.println("Seed 转换成功：" + gameSeed);
                currentState = State.PLAYING_GAME;
            } catch (NumberFormatException e) {
                this.errorMessage = "无效 Seed！超出 long 范围或包含非数字";
            }
        }
    }

    private void drawCenteredText(String text, double y) {
        // 直接用窗口中心作为x坐标，视觉近似居中
        StdDraw.text((double)WIDTH / 2, y, text);
    }

    // 辅助：显示临时提示
    private void showTempMessage(String message) {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        drawCenteredText(message, (double)HEIGHT / 2);
        StdDraw.show();
        StdDraw.pause(1500);
    }

    private static class Button {
        private final double x;      // 按钮中心X
        private final double y;      // 按钮中心Y
        private final double width;  // 按钮宽度
        private final double height; // 按钮高度
        private final String text;   // 按钮文字
        private final Color bgColor; // 背景色
        private final Color textColor; // 文字色

        public Button(double x, double y, double width, double height, String text, Color bgColor, Color textColor) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.text = text;
            this.bgColor = bgColor;
            this.textColor = textColor;
        }

        public void draw() {
            // 绘制按钮背景
            StdDraw.setPenColor(bgColor);
            StdDraw.filledRectangle(x, y, width / 2, height / 2);
            // 绘制按钮边框
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.rectangle(x, y, width / 2, height / 2);
            // 绘制按钮文字（直接用按钮中心坐标）
            StdDraw.setPenColor(textColor);
            StdDraw.setFont(BUTTON_FONT);
            StdDraw.text(x, y, text);
            // 恢复默认字体
            StdDraw.setFont(MAIN_FONT);
        }

        // 检测是否点击按钮
        public boolean isClicked(double mouseX, double mouseY) {
            return mouseX >= x - width/2 && mouseX <= x + width/2
                    && mouseY >= y - height/2 && mouseY <= y + height/2;
        }
    }

    // 程序入口
    public static void main(String[] args) {
        Main game = new Main();
        game.start();
    }
}