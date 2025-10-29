package island.util;

import java.util.concurrent.ThreadLocalRandom;

/// Вспомогательный класс для работы со случайностями.
public final class Randoms {
    private Randoms() {}

    /// true с вероятностью p (0..1)
    public static boolean chance(double p) {
        return ThreadLocalRandom.current().nextDouble() < p;
    }

    /// Случайное число в диапазоне [min, max] включительно
    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}