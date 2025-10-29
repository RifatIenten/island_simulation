package island.config;

/// Все числовые параметры симуляции сведены сюда.
/// Я добавил АЛИАСЫ старых имен (WIDTH/HEIGHT, TICK_MS, NUTRITION_PER_KG, WOLF_PACK_BONUS),
/// чтобы существующий код и сервисы продолжили компилироваться без изменений.
public final class SimulationConfig {

    /// --------------------- Базовые размеры и тайминги ---------------------
    public static final int ISLAND_WIDTH = 10;          /// ширина острова (колонок)
    public static final int ISLAND_HEIGHT = 5;          /// высота острова (строк)
    public static final int TICK_DURATION_MS = 600;     /// длительность "дня" в миллисекундах (если бы был таймер/пауза)
    public static final int MAX_DAYS = 30;              /// сколько дней идёт симуляция (ограничитель основного цикла)

    /// Алиасы под старые имена (чтобы ничего не падало)
    public static final int WIDTH = ISLAND_WIDTH;       /// алиас для старого кода (совместимость)
    public static final int HEIGHT = ISLAND_HEIGHT;     /// алиас для старого кода
    public static final int TICK_MS = TICK_DURATION_MS; /// алиас для старого кода

    /// --------------------- Стартовые количества животных ------------------
    // Эти константы у меня сейчас не читаются напрямую фабрикой, но оставляю как «точки настройки».
    public static final int START_RABBITS = 8;
    public static final int START_DEER = 4;
    public static final int START_MOUSE = 4;
    public static final int START_GOAT = 2;
    public static final int START_SHEEP = 2;
    public static final int START_BUFFALO = 1;

    public static final int START_WOLF = 2;
    public static final int START_FOX = 2;
    public static final int START_BEAR = 1;
    public static final int START_PYTHON = 1;
    public static final int START_EAGLE = 1;

    /// --------------------- Растения ---------------------------------------
    /// вероятность появления нового растения в клетке за день
    public static final double PLANT_SPAWN_PROB = 0.22; /// чем больше — тем больше корма для травоядных
    /// максимум растений, которое мы храним «на клетке» (если у тебя счётчик растений)
    public static final int MAX_PLANTS_PER_CELL = 3;    /// в текущей реализации храню 1 Plant с уровнем роста

    /// --------------------- Сытость / голод --------------------------------
    /// сколько от "потребности в еде" сгорает каждый день
    public static final double SATIETY_DECAY_PER_DAY = 0.20; /// 20% дневной потребности «сгорает»
    /// сколько «долей потребности» даёт одно растение травоядному
    public static final double PLANT_NUTRITION = 0.55;       /// растение покрывает ~половину потребности
    /// сколько «долей потребности» даёт съеденная жертва хищнику
    public static final double MEAT_NUTRITION = 0.85;        /// мясо почти «закрывает день»

    /// Алиас под старое имя, если в сервисах использовался NUTRITION_PER_KG
    public static final double NUTRITION_PER_KG = MEAT_NUTRITION;

    /// --------------------- Движение / размножение -------------------------
    /// вероятность, что животное вообще двинется в этот день
    public static final double MOVE_PROB = 0.85;             /// можно задействовать в будущем MovementService
    /// шанс размножения при наличии пары в клетке
    public static final double REPRODUCTION_PROB = 0.18;     /// базовая вероятность (сейчас логика в ReproductionService)
    /// максимум детёнышей единовременно
    public static final int MAX_OFFSPRING = 1;               /// ограничение рождаемости

    /// --------------------- Охота ------------------------------------------
    /// глобальный множитель к шансам из DietMatrix (1.0 — как в матрице)
    public static final double HUNTING_MODIFIER = 0.90;      /// задел для глобального тюнинга сложности охоты

    /// Премия «стаи волков» (если сервис охоты это учитывает) — алиас под старое имя.
    /// Если у тебя логика стаи есть — оставляем; если нет — просто не используется.
    public static final double WOLF_PACK_BONUS = 0.15;

    private SimulationConfig() {
        /// приватный конструктор — класс только с константами
    }
}