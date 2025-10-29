package island.stats;

import island.config.Species;

import java.util.EnumMap;
import java.util.Map;

/**
 * Таблица шансов "кто кого ест" в процентах.
 * Без промежуточных констант — ссылемся на Species.* напрямую,
 * чтобы не ловить illegal forward reference.
 */
public final class DietMatrix {
    private static final Map<Species, Map<Species, Integer>> table = new EnumMap<>(Species.class);

    static {
        // Волк
        put(Species.WOLF, Species.RABBIT, 70);
        put(Species.WOLF, Species.MOUSE, 80);
        put(Species.WOLF, Species.GOAT, 55);
        put(Species.WOLF, Species.SHEEP, 65);
        put(Species.WOLF, Species.DEER, 30);
        put(Species.WOLF, Species.BOAR, 15);
        put(Species.WOLF, Species.DUCK, 20);

        // Лиса
        put(Species.FOX, Species.MOUSE, 90);
        put(Species.FOX, Species.RABBIT, 70);
        put(Species.FOX, Species.DUCK, 25);
        put(Species.FOX, Species.CATERPILLAR, 60);

        // Медведь (всеядный)
        put(Species.BEAR, Species.RABBIT, 70);
        put(Species.BEAR, Species.DEER, 25);
        put(Species.BEAR, Species.GOAT, 25);
        put(Species.BEAR, Species.SHEEP, 25);
        put(Species.BEAR, Species.BOAR, 20);
        put(Species.BEAR, Species.DUCK, 30);
        put(Species.BEAR, Species.MOUSE, 50);
        put(Species.BEAR, Species.CATERPILLAR, 70);

        // Питон
        put(Species.PYTHON, Species.RABBIT, 40);
        put(Species.PYTHON, Species.MOUSE, 60);
        put(Species.PYTHON, Species.DUCK, 30);
        put(Species.PYTHON, Species.GOAT, 8);
        put(Species.PYTHON, Species.SHEEP, 8);

        // Орёл
        put(Species.EAGLE, Species.RABBIT, 35);
        put(Species.EAGLE, Species.MOUSE, 85);
        put(Species.EAGLE, Species.DUCK, 40);
        put(Species.EAGLE, Species.CATERPILLAR, 20);

        // Кабан / мышь / утка — насекомые
        put(Species.BOAR, Species.CATERPILLAR, 60);
        put(Species.BOAR, Species.MOUSE, 20);
        put(Species.MOUSE, Species.CATERPILLAR, 50);
        put(Species.DUCK, Species.CATERPILLAR, 90);
    }

    private static void put(Species predator, Species prey, int chance) {
        table.computeIfAbsent(predator, k -> new EnumMap<>(Species.class)).put(prey, chance);
    }

    public static int chance(Species predator, Species prey) {
        Map<Species, Integer> row = table.get(predator);
        if (row == null) return 0;
        return row.getOrDefault(prey, 0);
    }

    private DietMatrix() {}
}