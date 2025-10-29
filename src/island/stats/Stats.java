package island.stats;

import island.config.Species;
import island.entities.Animal;

import java.util.EnumMap;
import java.util.Map;

/// Статистика за день и накопительная.
public class Stats {

    /// За день: родилось / съедено / умерло от голода
    public int bornToday = 0;
    public int eatenToday = 0;
    public int starvedToday = 0;

    /// Текущие убийства по видам хищников
    private final Map<Species, Integer> predatorKills = new EnumMap<>(Species.class);

    public void resetDaily() {
        bornToday = 0;
        eatenToday = 0;
        starvedToday = 0;
    }

    public void onEat(Animal predator, Animal prey) {
        eatenToday++;
        predatorKills.merge(predator.getSpecies(), 1, Integer::sum);
    }

    public void onEatPlant(Animal eater) {
        /// можно считать как "еды съедено", но в метриках поедание растений считаем отдельно в Island.countPlants()
    }

    public void onStarved(Animal a) {
        starvedToday++;
    }

    public void onBorn(Animal baby) {
        bornToday++;
    }

    public Map<Species, Integer> getPredatorKills() {
        return predatorKills;
    }
}