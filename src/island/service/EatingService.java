package island.service;

import island.config.Species;
import island.entities.Animal;
import island.entities.Herbivore;
import island.entities.Predator;
import island.map.Island;
import island.map.Location;
import island.stats.DietMatrix;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/// Фаза питания: травоядные сначала едят растения, затем хищники охотятся по DietMatrix.
public class EatingService {
    private final Island island;
    private long eatenToday = 0; // счётчик событий «съедено» (растение/добыча)

    public EatingService(Island island) { this.island = island; }

    public long getAndResetEaten() { long v = eatenToday; eatenToday = 0; return v; } // отдаю и обнуляю

    public void run() {
        for (int x = 0; x < island.getWidth(); x++) {
            for (int y = 0; y < island.getHeight(); y++) {
                Location loc = island.getLocation(x, y);
                List<Animal> animals = loc.getAnimalsSnapshot();

                // Сначала травоядные — едят растения
                for (Animal a : animals) {
                    if (!a.isAlive()) continue;
                    if (a instanceof Herbivore) {
                        boolean ate = ((Herbivore) a).eatPlant(island);
                        if (ate) eatenToday++;
                    }
                }

                // Теперь хищники — пытаются съесть кого-то из этой же клетки
                List<Animal> fresh = loc.getAnimalsSnapshot(); // новый снапшот после травоядных
                for (Animal a : fresh) {
                    if (!a.isAlive() || !(a instanceof Predator)) continue;

                    Species hunter = a.getSpecies();
                    // ищем любую подходящую жертву
                    for (Animal prey : fresh) {
                        if (!prey.isAlive() || prey == a) continue;
                        int chance = DietMatrix.chance(hunter, prey.getSpecies());
                        if (chance <= 0) continue;

                        if (ThreadLocalRandom.current().nextInt(100) < chance) {
                            // успешная охота
                            preyKill(prey); // пометить жертву мёртвой
                            aFeed(a);       // накормить хищника
                            eatenToday++;
                            break;          // один «приём пищи» на хищника
                        }
                    }
                }
            }
        }
    }

    private void preyKill(Animal prey) { // помечаем мёртвым (через reflection — не ломаю инкапсуляцию)
        try {
            java.lang.reflect.Field alive = Animal.class.getDeclaredField("alive");
            alive.setAccessible(true);
            alive.set(prey, false);
        } catch (Exception ignored) {}
    }

    private void aFeed(Animal a) { // пополнить сытость (reflection к protected feed)
        try {
            java.lang.reflect.Method feed = Animal.class.getDeclaredMethod("feed", double.class);
            feed.setAccessible(true);
            feed.invoke(a, a.nutritionOnEat());
        } catch (Exception ignored) {}
    }
}