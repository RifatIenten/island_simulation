package island.util;

import island.entities.Animal;
import island.entities.plants.Plant;
import island.map.Island;
import island.map.Location;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Слой совместимости, чтобы сервисы могли работать с нашей моделью,
 * не лезя в детали реализации Island/Location/Animal.
 */
public final class Compat {

    private Compat() {}

    /* ====================== Доступ к клеткам острова ====================== */

    /** Без рефлексии: Island уже имеет getLocation(x,y). */
    public static Location getLocation(Island island, int x, int y) {
        Objects.requireNonNull(island, "island");
        return island.getLocation(x, y);
    }

    /** Обход всех клеток острова. */
    public static void forEachLocation(Island island, Consumer<Location> action) {
        Objects.requireNonNull(island, "island");
        Objects.requireNonNull(action, "action");
        for (int x = 0; x < island.getWidth(); x++) {
            for (int y = 0; y < island.getHeight(); y++) {
                action.accept(island.getLocation(x, y));
            }
        }
    }

    /* ====================== Растения в клетке ====================== */

    public static Plant getPlant(Location loc) {
        return loc.getPlant();
    }

    public static void setPlant(Location loc, Plant plant) {
        loc.setPlant(plant);
    }

    /* ====================== Животные в клетке ====================== */

    public static List<Animal> getAnimalsSnapshot(Location loc) {
        return loc.getAnimalsSnapshot();
    }

    /** Добавить животное в клетку + проставить ему локацию. */
    public static void addAnimal(Location loc, Animal a) {
        if (loc == null || a == null) return;
        loc.addAnimal(a);
        a.setLocation(loc);
    }

    /** Удалить животное из клетки. */
    public static void removeAnimal(Location loc, Animal a) {
        if (loc == null || a == null) return;
        loc.removeAnimal(a);
    }

    /* ====================== Операции над Animal ====================== */

    public static boolean isAlive(Animal a) {
        return a != null && a.isAlive();
    }

    public static void setLocation(Animal a, Location loc) {
        if (a != null) a.setLocation(loc);
    }

    public static void starveTick(Animal a) {
        if (a != null) a.starveTick();
    }

    /** Совместимый вызов размножения. НИЧЕГО не возвращает. */
    public static void reproduce(Animal a, Island island) {
        if (a != null) a.reproduce(island);
    }

    /* ====================== Утилиты ====================== */

    public static Location getRandomLocation(Island island) {
        int x = ThreadLocalRandom.current().nextInt(island.getWidth());
        int y = ThreadLocalRandom.current().nextInt(island.getHeight());
        return island.getLocation(x, y);
    }
}