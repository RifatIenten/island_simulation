package island;

import island.map.Island;

// Точка входа: создаю остров, собираю движок, заселяю и запускаю симуляцию.
public class Main {
    public static void main(String[] args) {
        Island island = new Island(10, 5);
        SimulationEngine engine = new SimulationEngine(island);
        engine.populate(); // начальная генерация животных
        engine.start(30);  // основной цикл на 30 «дней»
    }
}