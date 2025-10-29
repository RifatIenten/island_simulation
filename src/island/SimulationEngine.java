package island;

import island.entities.Animal;
import island.factory.AnimalFactory;
import island.map.Island;
import island.service.*;

import java.util.List;

public class SimulationEngine {
    private final Island island;

    // Сервисы-фазы экосистемы
    private final PlantService plantService;
    private final MovementService movementService;
    private final EatingService eatingService;
    private final ReproductionService reproductionService;
    private final StarvationService starvationService;

    private List<Animal> animals; // просто держу ссылку на стартовый список (для инфо)

    public SimulationEngine(Island island) {
        this.island = island;
        // Чуть увеличил спавн растений, чтобы травоядные не вымирали слишком рано
        this.plantService = new PlantService(island, 0.12);
        this.movementService = new MovementService(island);
        this.eatingService = new EatingService(island);
        this.reproductionService = new ReproductionService(island);
        this.starvationService = new StarvationService(island);
    }

    public void populate() {
        this.animals = AnimalFactory.createAnimals(island); // стартовая генерация по карте
        System.out.println("На острове появились животные: " + animals.size());
    }

    private long countAlive() { // считаю живых по всем клеткам
        long c = 0;
        for (int x = 0; x < island.getWidth(); x++)
            for (int y = 0; y < island.getHeight(); y++)
                c += island.getLocation(x, y).getAnimalsSnapshot().stream().filter(Animal::isAlive).count();
        return c;
    }

    public void start(int days) {
        System.out.println("🌴 Старт симуляции острова " + island.getWidth() + "x" + island.getHeight());
        for (int day = 1; day <= days; day++) {
            plantService.run();           // растения растут/спавнятся
            movementService.run();        // фаза перемещения (пока заглушка)
            eatingService.run();          // кормёжка
            long eaten = eatingService.getAndResetEaten();

            reproductionService.run();    // размножение
            long born = reproductionService.getAndResetBorn();

            starvationService.run();      // спад сытости и смерть от голода
            long starved = starvationService.getAndResetStarved();

            long alive = countAlive();
            StatsPrinter.printDay(day, island, alive, born, eaten, starved); // сводка + карта
            if (alive == 0) break;        // если все вымерли — ранний стоп
        }
        System.out.println("\n🌅 Симуляция завершена!");
    }
}