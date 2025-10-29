package island.service;

import island.entities.plants.Plant;
import island.map.Island;
import island.map.Location;

import java.util.concurrent.ThreadLocalRandom;

/// Рост растений и спонтанное появление.
public class PlantService {
    private final Island island;
    private final double spawnChancePerEmptyCell; /// шанс появления нового растения в пустой клетке

    public PlantService(Island island, double spawnChancePerEmptyCell) {
        this.island = island;
        this.spawnChancePerEmptyCell = spawnChancePerEmptyCell;
    }

    public void run() {
        for (int x = 0; x < island.getWidth(); x++) {
            for (int y = 0; y < island.getHeight(); y++) {
                Location loc = island.getLocation(x, y);
                Plant p = loc.getPlant();
                if (p != null) {
                    p.grow(); // «подрастаем»
                } else {
                    // пустая клетка — шанс заспавнить новый Plant
                    if (ThreadLocalRandom.current().nextDouble() < spawnChancePerEmptyCell) {
                        loc.setPlant(new Plant());
                    }
                }
            }
        }
    }
}