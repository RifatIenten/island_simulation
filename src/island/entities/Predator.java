package island.entities;

import island.config.Species;
import island.map.Location;

// Хищник: насыщается охотой (см. EatingService + DietMatrix)
public class Predator extends Animal {
    public Predator(Species species, Location location) { super(species, location); }

    @Override public boolean isPredator() { return true; }

    @Override public double nutritionOnEat() { return species.foodNeed; } // удачная охота — полный «бак»
}