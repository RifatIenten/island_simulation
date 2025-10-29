package island.factory;

import island.entities.Animal;
import island.map.Island;
import island.map.Location;
import island.util.Compat;

import java.util.List;

/// Утилита случайного размещения животных по острову (без коллизий по потокам).
public final class ThreadLocalRandomWrapper {
    private ThreadLocalRandomWrapper(){}

    public static void placeAnimalsRandomly(List<Animal> animals, Island island) {
        for (Animal a : animals) {
            Location loc = Compat.getRandomLocation(island); // беру случайную клетку через слой совместимости
            Compat.addAnimal(loc, a);                        // добавляю в клетку
            Compat.setLocation(a, loc);                      // фиксирую локацию на самом животном
        }
    }
}