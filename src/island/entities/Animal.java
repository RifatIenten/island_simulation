package island.entities;

import island.config.SimulationConfig;
import island.config.Species;
import island.map.Island;
import island.map.Location;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Animal {
    protected final Species species;  // вид животного
    protected Location location;      // текущая клетка
    protected boolean alive = true;   // жив ли

    // сытость в «кг корма» (используем foodNeed как «ёмкость» на день)
    protected double satiety;         // текущая «ёмкость» сытости
    protected final double foodNeed;  // суточная потребность (ёмкость)
    protected final double satietyDecay; // сколько «сгорает» за день

    public Animal(Species species, Location location) {
        this.species = species;
        this.location = location;
        this.foodNeed = species.foodNeed;
        // из конфигурации: доля суточной потребности, сгорающая в день
        this.satietyDecay = SimulationConfig.SATIETY_DECAY_PER_DAY * foodNeed;
        this.satiety = foodNeed; // стартуем сытыми (полный бак)
    }

    public Species getSpecies() { return species; }   // нужно сервисам (охота, статистика)
    public boolean isAlive() { return alive; }
    public Location getLocation() { return location; }
    public void setLocation(Location newLoc) { this.location = newLoc; }

    public void starveTick() {             // тик голодания: уменьшаю сытость и проверяю смерть
        if (!alive) return;
        satiety -= satietyDecay;
        if (satiety <= 0) alive = false;
    }

    protected void feed(double nutrition) { // «накормить» животное на nutrition (не больше ёмкости)
        if (!alive) return;
        satiety = Math.max(0.0, Math.min(foodNeed, satiety + nutrition));
    }

    public void move(Island island) {
        // Перемещение теперь в MovementService v2 — этот метод можно оставить пустым,
        // если где-то ещё вызывается a.move(island), он не навредит.
    }

    public void reproduce(Island island) { /* логика в ReproductionService v2 */ }

    public abstract boolean isPredator();     // удобно проверять тип поведения
    public abstract double nutritionOnEat();  // сколько «даёт» удачное кормление
}