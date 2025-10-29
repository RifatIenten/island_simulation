package island.service;

import island.entities.Animal;
import island.map.Island;
import island.map.Location;

import java.util.List;

/**
 * Сервис перемещения: идём по всем клеткам, берём снапшот и зовём move() у живых.
 * Сейчас Animal.move() — заглушка, но сервис оставляю как точку расширения.
 */
public class MovementService {
    private final Island island;

    public MovementService(Island island) { this.island = island; }

    public void run() {
        for (int x = 0; x < island.getWidth(); x++) {
            for (int y = 0; y < island.getHeight(); y++) {
                Location loc = island.getLocation(x, y);
                List<Animal> animals = loc.getAnimalsSnapshot();
                for (Animal a : animals) {
                    if (a.isAlive()) {
                        a.move(island); // логика движения может удалять/добавлять в клетки
                    }
                }
            }
        }
    }
}