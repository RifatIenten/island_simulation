package island.util;

import island.entities.Animal;
import island.entities.plants.Plant;
import island.map.Island;
import island.map.Location;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 /// Универсальные безопасные вызовы для Island/Location через reflection.
 /// Никаких прямых зависимостей от конкретного API.
 */
public final class IslandAccess {
    private IslandAccess(){}

    /// --- Размеры острова ---
    public static int getWidth(Island island) {
        Integer v = invokeIntGetter(island, "getWidth");
        if (v != null) return v;
        return getIntField(island, "width", 10);
    }

    public static int getHeight(Island island) {
        Integer v = invokeIntGetter(island, "getHeight");
        if (v != null) return v;
        return getIntField(island, "height", 10);
    }

    /// --- Получение Location по координатам (если есть такой метод) ---
    public static Location getLocation(Island island, int x, int y) {
        // getLocation(int,int)
        try {
            Method m = island.getClass().getMethod("getLocation", int.class, int.class);
            return (Location) m.invoke(island, x, y);
        } catch (Exception ignored) {}
        // getCell(int,int)
        try {
            Method m = island.getClass().getMethod("getCell", int.class, int.class);
            return (Location) m.invoke(island, x, y);
        } catch (Exception ignored) {}
        return null;
    }

    /// --- Случайная клетка ---
    public static Location randomLocation(Island island) {
        // getRandomLocation()
        try {
            Method m = island.getClass().getMethod("getRandomLocation");
            return (Location) m.invoke(island);
        } catch (Exception ignored) {}

        // Если нет — пытаемся выбрать случайно из сетки
        int w = getWidth(island);
        int h = getHeight(island);
        Location loc = getLocation(island, ThreadLocalRandom.current().nextInt(w),
                ThreadLocalRandom.current().nextInt(h));
        if (loc != null) return loc;

        // grid: Location[][] в поле
        Location rnd = randomFromGridField(island);
        if (rnd != null) return rnd;

        // getLocations()/getAllLocations(): List<Location>
        List<Location> all = locationsList(island);
        if (!all.isEmpty())
            return all.get(ThreadLocalRandom.current().nextInt(all.size()));

        return null;
    }

    /// --- Обход всех клеток острова ---
    public static void forEachLocation(Island island, Consumer<Location> consumer) {
        Objects.requireNonNull(consumer);

        // Попытка через размеры + getLocation(int,int)
        int w = getWidth(island);
        int h = getHeight(island);
        boolean okByGetter = false;
        try {
            Method m = island.getClass().getMethod("getLocation", int.class, int.class);
            okByGetter = true;
            for (int y = 0; y < h; y++)
                for (int x = 0; x < w; x++)
                    consumer.accept((Location) m.invoke(island, x, y));
            return;
        } catch (Exception ignored) {}

        // Через поле grid[][] (двумерный массив)
        if (!okByGetter) {
            Object grid = gridField(island);
            if (grid != null && grid.getClass().isArray()) {
                int rows = Array.getLength(grid);
                for (int i = 0; i < rows; i++) {
                    Object row = Array.get(grid, i);
                    int cols = Array.getLength(row);
                    for (int j = 0; j < cols; j++) {
                        consumer.accept((Location) Array.get(row, j));
                    }
                }
                return;
            }
        }

        // Через список локаций
        List<Location> all = locationsList(island);
        if (!all.isEmpty()) {
            all.forEach(consumer);
        }
    }

    /// --- Доступ к животным/растениям в клетке ---
    @SuppressWarnings("unchecked")
    public static List<Animal> getAnimals(Location loc) {
        try {
            Method m = loc.getClass().getMethod("getAnimalsSnapshot");
            Object r = m.invoke(loc);
            if (r instanceof List) return (List<Animal>) r;
        } catch (Exception ignored) {}
        try {
            Method m = loc.getClass().getMethod("getAnimals");
            Object r = m.invoke(loc);
            if (r instanceof List) return (List<Animal>) r;
        } catch (Exception ignored) {}
        return Collections.emptyList();
    }

    public static void addAnimal(Location loc, Animal a) {
        try {
            Method m = loc.getClass().getMethod("addAnimal", Animal.class);
            m.invoke(loc, a);
        } catch (Exception ignored) {}
    }

    public static void removeAnimal(Location loc, Animal a) {
        try {
            Method m = loc.getClass().getMethod("removeAnimal", Animal.class);
            m.invoke(loc, a);
        } catch (Exception ignored) {}
    }

    public static Plant getPlant(Location loc) {
        try {
            Method m = loc.getClass().getMethod("getPlant");
            return (Plant) m.invoke(loc);
        } catch (Exception ignored) {}
        return null;
    }

    public static void setPlant(Location loc, Plant plant) {
        try {
            Method m = loc.getClass().getMethod("setPlant", Plant.class);
            m.invoke(loc, plant);
        } catch (Exception ignored) {}
    }

    /// --- Вспомогательные reflection-методы ---
    private static Integer invokeIntGetter(Object o, String name) {
        try {
            Method m = o.getClass().getMethod(name);
            Object r = m.invoke(o);
            if (r instanceof Number) return ((Number) r).intValue();
        } catch (Exception ignored) {}
        return null;
    }

    private static int getIntField(Object o, String name, int def) {
        try {
            Field f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            Object r = f.get(o);
            if (r instanceof Number) return ((Number) r).intValue();
        } catch (Exception ignored) {}
        return def;
    }

    private static Object gridField(Island island) {
        for (String fname : List.of("grid", "cells", "locations", "map")) {
            try {
                Field f = island.getClass().getDeclaredField(fname);
                f.setAccessible(true);
                Object val = f.get(island);
                if (val != null && val.getClass().isArray()) return val;
            } catch (Exception ignored) {}
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<Location> locationsList(Island island) {
        // getLocations()
        try {
            Method m = island.getClass().getMethod("getLocations");
            Object r = m.invoke(island);
            if (r instanceof List) return (List<Location>) r;
        } catch (Exception ignored) {}
        // getAllLocations()
        try {
            Method m = island.getClass().getMethod("getAllLocations");
            Object r = m.invoke(island);
            if (r instanceof List) return (List<Location>) r;
        } catch (Exception ignored) {}
        // поле List<Location>
        for (String f : List.of("locations", "cells", "list")) {
            try {
                Field fld = island.getClass().getDeclaredField(f);
                fld.setAccessible(true);
                Object r = fld.get(island);
                if (r instanceof List) return (List<Location>) r;
            } catch (Exception ignored) {}
        }
        return Collections.emptyList();
    }

    private static Location randomFromGridField(Island island) {
        Object grid = gridField(island);
        if (grid == null || !grid.getClass().isArray()) return null;
        int rows = Array.getLength(grid);
        if (rows == 0) return null;
        Object row = Array.get(grid, ThreadLocalRandom.current().nextInt(rows));
        int cols = Array.getLength(row);
        if (cols == 0) return null;
        return (Location) Array.get(row, ThreadLocalRandom.current().nextInt(cols));
    }
}