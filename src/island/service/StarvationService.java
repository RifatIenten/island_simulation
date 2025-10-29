package island.service;

import island.entities.Animal;
import island.map.Island;
import island.map.Location;

import java.util.List;

/// Применяем спад сытости и фиксируем смерть от голода.
public class StarvationService {
    private final Island island;
    private long starvedToday = 0; // сколько умерло от голода за этот день

    public StarvationService(Island island) { this.island = island; }

    public long getAndResetStarved() { long v = starvedToday; starvedToday = 0; return v; }

    public void run() {
        for (int x = 0; x < island.getWidth(); x++) {
            for (int y = 0; y < island.getHeight(); y++) {
                Location loc = island.getLocation(x, y);
                List<Animal> animals = loc.getAnimalsSnapshot();
                int before = (int) animals.stream().filter(Animal::isAlive).count();
                for (Animal a : animals) a.starveTick(); // прожигаю сытость
                int after = (int) animals.stream().filter(Animal::isAlive).count();
                if (after < before) starvedToday += (before - after); // фиксирую разницу
            }
        }
    }
}