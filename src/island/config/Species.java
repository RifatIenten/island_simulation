package island.config;

/// Описание вида: параметры и эмодзи для отображения.
public enum Species {
    WOLF("🐺", true, 30.0, 3, 8.0, 30),      // волк: хищник, скоростной, средняя потребность
    PYTHON("🐍", true, 15.0, 1, 3.0, 30),    // питон: медленный хищник, ниже потребность
    FOX("🦊", true, 8.0, 2, 2.0, 30),        // лиса: мелкий хищник
    BEAR("🐻", true, 500.0, 2, 80.0, 5),     // медведь: большой хищник, мало в клетке
    EAGLE("🦅", true, 6.0, 3, 1.0, 20),      // орёл: быстрый хищник с малой потребностью

    // травоядные/всеядные с ограничениями по численности в клетке
    HORSE("🐎", false, 400.0, 4, 60.0, 20),
    DEER("🦌", false, 300.0, 4, 50.0, 20),
    RABBIT("🐇", false, 2.0, 2, 0.45, 150),
    MOUSE("🐁", false, 0.05, 1, 0.01, 500),
    GOAT("🐐", false, 60.0, 3, 10.0, 140),
    SHEEP("🐑", false, 70.0, 3, 15.0, 140),
    BOAR("🐗", false, 200.0, 2, 50.0, 50),
    BUFFALO("🐃", false, 700.0, 3, 100.0, 10),
    DUCK("🦆", false, 1.0, 4, 0.15, 200),
    CATERPILLAR("🐛", false, 0.01, 0, 0.002, 1000);

    public final String emoji;      // эмодзи для печати карты
    public final boolean predator;  // true если хищник
    public final double weight;     // информативный вес (на баланс не влияет)
    public final int speed;         // скорость (пока не задействована в move)
    public final double foodNeed;   /// суточная потребность
    public final int maxPerCell;    // лимит особей этого вида в одной клетке

    Species(String emoji, boolean predator, double weight, int speed, double foodNeed, int maxPerCell) {
        this.emoji = emoji;
        this.predator = predator;
        this.weight = weight;
        this.speed = speed;
        this.foodNeed = foodNeed;
        this.maxPerCell = maxPerCell;
    }
}