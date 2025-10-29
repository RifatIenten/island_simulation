package island.entities;

import island.config.Species;
import island.entities.plants.Plant;
import island.map.Island;
import island.map.Location;

// Травоядное: питается растениями, логика поедания в eatPlant()
public class Herbivore extends Animal {
    public Herbivore(Species species, Location location) { super(species, location); }

    @Override public boolean isPredator() { return false; }

    @Override public double nutritionOnEat() { return species.foodNeed * 0.8; } // одно «сытое» кормление почти насыщает

    /// Травоядное ест растение (если есть)
    public boolean eatPlant(Island island) {
        if (!alive) return false;
        Plant p = location.getPlant();
        if (p != null && p.isEdible()) {
            feed(species.foodNeed * 0.6); // растение не «фулл» порция
            p.reset();                    // «съели» — сброс роста
            return true;
        }
        return false;
    }
}