package island.service;

import island.config.Species;
import island.entities.Animal;
import island.factory.AnimalFactory;
import island.map.Island;
import island.map.Location;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Размножение v2:
 * 1. Локально в клетке: пары одного вида → шанс pLocalBorn, учитываем maxPerCell.
 * 2. Если локально не родилось, но вида >=2 на острове — даю до N глобальных попыток «склейки» пары
 *    из разных клеток (pGlobalBorn). Детёныш спавнится у родителя или соседа, если есть ёмкость.
 */
public class ReproductionService {
    private final Island island;

    private final double pLocalBorn;             // вероятность на пару в клетке
    private final double pGlobalBorn;            // вероятность глобальной «склейки»
    private final int maxGlobalAttemptsPerSpecies; // ограничение числа глобальных попыток

    private long bornToday = 0; // счётчик рождений

    public ReproductionService(Island island) {
        this(island, 0.35, 0.15, 3);
    }

    public ReproductionService(Island island, double pLocalBorn, double pGlobalBorn, int maxGlobalAttemptsPerSpecies) {
        this.island = island;
        this.pLocalBorn = clamp01(pLocalBorn);
        this.pGlobalBorn = clamp01(pGlobalBorn);
        this.maxGlobalAttemptsPerSpecies = Math.max(0, maxGlobalAttemptsPerSpecies);
    }

    public long getAndResetBorn() {
        long v = bornToday;
        bornToday = 0;
        return v;
    }

    public void run() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        // === 1) Локальные рождения ===
        List<Animal> newbornsLocal = new ArrayList<>();
        EnumSet<Species> speciesBornLocally = EnumSet.noneOf(Species.class);

        for (int x = 0; x < island.getWidth(); x++) {
            for (int y = 0; y < island.getHeight(); y++) {
                Location loc = island.getLocation(x, y);
                List<Animal> here = loc.getAnimalsSnapshot();
                if (here.isEmpty()) continue;

                Map<Species, List<Animal>> bySp = new EnumMap<>(Species.class);
                for (Animal a : here) {
                    if (a != null && a.isAlive()) {
                        bySp.computeIfAbsent(a.getSpecies(), k -> new ArrayList<>()).add(a);
                    }
                }
                for (Map.Entry<Species, List<Animal>> e : bySp.entrySet()) {
                    Species sp = e.getKey();
                    List<Animal> group = e.getValue();
                    int n = group.size();
                    if (n < 2) continue;

                    int sameHere = n;
                    int capLeft = Math.max(0, sp.maxPerCell - sameHere); // свободная ёмкость в клетке
                    if (capLeft == 0) continue;

                    int pairs = n / 2;  // потенциальные пары
                    int births = 0;
                    for (int i = 0; i < pairs && births < capLeft; i++) {
                        if (rnd.nextDouble() < pLocalBorn) {
                            Animal child = AnimalFactory.create(sp, loc);
                            newbornsLocal.add(child);
                            births++;
                        }
                    }
                    if (births > 0) {
                        speciesBornLocally.add(sp);
                        bornToday += births;
                    }
                }
            }
        }
        // добавляю локальных новорождённых в клетки
        for (Animal child : newbornsLocal) {
            Location loc = child.getLocation();
            if (loc != null) loc.addAnimal(child);
        }

        // === 2) Глобальная подпорка редких пар ===
        Map<Species, List<Animal>> global = new EnumMap<>(Species.class);
        for (int x = 0; x < island.getWidth(); x++)
            for (int y = 0; y < island.getHeight(); y++)
                for (Animal a : island.getLocation(x, y).getAnimalsSnapshot())
                    if (a != null && a.isAlive())
                        global.computeIfAbsent(a.getSpecies(), k -> new ArrayList<>()).add(a);

        List<Animal> newbornsGlobal = new ArrayList<>();

        for (Map.Entry<Species, List<Animal>> e : global.entrySet()) {
            Species sp = e.getKey();
            List<Animal> pop = e.getValue();
            if (pop.size() < 2) continue;
            if (speciesBornLocally.contains(sp)) continue; // уже родились локально — глобально не помогаю

            int attempts = Math.min(pop.size() / 2, maxGlobalAttemptsPerSpecies);
            if (attempts <= 0) continue;

            Collections.shuffle(pop, rnd); // перемешиваю список для случайных пар
            int made = 0, i = 0;
            while (made < attempts && i + 1 < pop.size()) {
                Animal p1 = pop.get(i++);
                Animal p2 = pop.get(i++);
                if (rnd.nextDouble() >= pGlobalBorn) continue; // попытка не удалась

                Location base = (rnd.nextBoolean() ? p1 : p2).getLocation(); // базовая клетка
                if (base == null) continue;

                Location where = findSpotWithCapacity(sp, base, rnd); // ищу место с ёмкостью
                if (where == null) continue;

                Animal child = AnimalFactory.create(sp, where);
                newbornsGlobal.add(child);
                made++;
            }
        }
        // добавляю глобальных новорождённых
        for (Animal child : newbornsGlobal) {
            Location loc = child.getLocation();
            if (loc != null) {
                loc.addAnimal(child);
                bornToday++;
            }
        }
    }

    private static double clamp01(double v) { return v < 0 ? 0 : (v > 1 ? 1 : v); }

    private Location findSpotWithCapacity(Species sp, Location base, ThreadLocalRandom rnd) {
        if (hasCapacity(sp, base)) return base;
        List<Location> neigh = neighborsShuffled(base, rnd);
        for (Location l : neigh) if (hasCapacity(sp, l)) return l;
        return null;
    }

    private boolean hasCapacity(Species sp, Location loc) {
        int c = 0;
        for (Animal a : loc.getAnimalsSnapshot())
            if (a != null && a.isAlive() && a.getSpecies() == sp) c++;
        return c < sp.maxPerCell;
    }

    private List<Location> neighborsShuffled(Location base, ThreadLocalRandom rnd) {
        int x = base.getX(), y = base.getY();
        int W = island.getWidth(), H = island.getHeight();
        List<Location> list = new ArrayList<>(8);
        for (int dx = -1; dx <= 1; dx++) {
            int nx = x + dx; if (nx < 0 || nx >= W) continue;
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int ny = y + dy; if (ny < 0 || ny >= H) continue;
                list.add(island.getLocation(nx, ny));
            }
        }
        Collections.shuffle(list, rnd);
        return list;
    }
}