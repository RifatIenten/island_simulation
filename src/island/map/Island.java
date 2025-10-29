package island.map;

import java.util.function.Consumer;

public class Island {
    private final int width;           // ширина сетки
    private final int height;          // высота сетки
    private final Location[][] grid;   // сами клетки

    public Island(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Location[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Location(x, y); // инициализирую все клетки
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Location getLocation(int x, int y) { return grid[x][y]; } // основной геттер клетки
    public Location getCell(int x, int y) { return grid[x][y]; }     // алиас на случай старого кода

    public void forEachCell(Consumer<Location> action) {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                action.accept(grid[x][y]); // пройтись по всем клеткам
    }
}