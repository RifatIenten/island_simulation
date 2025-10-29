package island.service;

import island.config.Species;
import island.entities.Animal;
import island.entities.plants.Plant;
import island.map.Island;
import island.map.Location;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/// Красивый вывод статистики и карты.
public class StatsPrinter {

    /// /// Печать «итогов дня»
    public static void printDay(
            int day,
            Island island,
            long alive,
            long born,
            long eaten,
            long starved
    ) {
        System.out.println();
        System.out.println("=== День " + day + " ===");
        int plants = countPlants(island);
        System.out.printf("Статистика: живых=%d, родилось=%d, съедено=%d, умерло_от_голода=%d, растений=%d%n",
                alive, born, eaten, starved, plants);

        Map<Species, Integer> perSpecies = countBySpecies(island);
        if (perSpecies.isEmpty()) {
            System.out.println("(нет животных)");
        } else {
            StringBuilder line = new StringBuilder();
            for (var e : perSpecies.entrySet()) {
                line.append(e.getKey().emoji).append(":").append(e.getValue()).append("  ");
            }
            System.out.println(line);
        }

        printMap(island); // визуализация карты эмодзи
    }

    private static int countPlants(Island island) {
        int c = 0;
        for (int x = 0; x < island.getWidth(); x++) {
            for (int y = 0; y < island.getHeight(); y++) {
                Plant p = island.getLocation(x, y).getPlant();
                if (p != null && p.getGrowth() > 0) c++;
            }
        }
        return c;
    }

    private static Map<Species, Integer> countBySpecies(Island island) {
        Map<Species, Integer> map = new EnumMap<>(Species.class);
        for (int x = 0; x < island.getWidth(); x++) {
            for (int y = 0; y < island.getHeight(); y++) {
                List<Animal> animals = island.getLocation(x, y).getAnimalsSnapshot();
                for (Animal a : animals) {
                    if (!a.isAlive()) continue;
                    map.merge(a.getSpecies(), 1, Integer::sum);
                }
            }
        }
        return map;
    }

    private static void printMap(Island island) {
        for (int y = 0; y < island.getHeight(); y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < island.getWidth(); x++) {
                Location loc = island.getLocation(x, y);
                List<Animal> list = loc.getAnimalsSnapshot().stream().filter(Animal::isAlive).toList();
                if (!list.isEmpty()) {
                    // показываем первого животного в клетке
                    row.append(list.get(0).getSpecies().emoji).append(' ');
                } else if (loc.getPlant() != null && loc.getPlant().getGrowth() > 0) {
                    row.append("🌿 ");
                } else {
                    row.append("⬛️ ");
                }
            }
            System.out.println(row);
        }
    }
}