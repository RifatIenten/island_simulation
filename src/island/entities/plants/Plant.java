package island.entities.plants;

// Простая модель растения с «уровнем роста».
public class Plant {
    private int growth = 1;          /// начинаем не с нуля, чтобы травоядные могли поесть в День 1
    private static final int MAX_GROWTH = 3; // максимум «запаса» роста на клетке

    public void grow() { if (growth < MAX_GROWTH) growth++; } // каждый «день» подрастаем на 1
    public void reset() { growth = 0; }                       // съели — обнулили рост
    public int getGrowth() { return growth; }                 // полезно для статистики/отладки
    public boolean isEdible() { return growth > 0; }          // «можно есть», если рост > 0
}