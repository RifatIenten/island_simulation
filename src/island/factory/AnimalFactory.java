package island.factory;

import island.config.Species;
import island.entities.Animal;
import island.entities.Herbivore;
import island.entities.Predator;
import island.map.Island;
import island.map.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/// Создание отдельных животных и стартовой популяции.
public final class AnimalFactory {
    private AnimalFactory() {}

    public static Animal create(Species sp, Location loc) {
        return sp.predator ? new Predator(sp, loc) : new Herbivore(sp, loc); // выбираю подкласс по флагу predator
    }

    /// Стартовая генерация — распределяем случайно по карте, не превышая maxPerCell.
    public static List<Animal> createAnimals(Island island) {
        List<Animal> all = new ArrayList<>();
        // Базовые стартовые количества (подгони по вкусу)
        int perTypeSmall = 6;    // для мелких (RABBIT, MOUSE, DUCK, CATERPILLAR)
        int perTypeMid   = 3;    // средние
        int perTypeBig   = 1;    // крупные хищники/копытные

        for (Species sp : Species.values()) {
            int count = sp == Species.RABBIT || sp == Species.MOUSE || sp == Species.DUCK || sp == Species.CATERPILLAR
                    ? perTypeSmall
                    : (sp.weight >= 150 ? perTypeBig : perTypeMid);

            for (int i = 0; i < count; i++) {
                int x = ThreadLocalRandom.current().nextInt(island.getWidth());
                int y = ThreadLocalRandom.current().nextInt(island.getHeight());
                Location loc = island.getLocation(x, y);

                // ограничение по количеству вида в клетке
                long sameHere = loc.getAnimalsSnapshot().stream().filter(a -> a.getSpecies() == sp).count();
                if (sameHere >= sp.maxPerCell) { i--; continue; } // перегенерирую попытку

                Animal a = create(sp, loc);
                loc.addAnimal(a);
                all.add(a);
            }
        }
        return all;
    }
}