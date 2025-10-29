package island.service;

/// Счётчики за «день».
public class StatsCounters {
    public int born = 0;    // родилось
    public int eaten = 0;   // съедено (растений/добычи)
    public int starved = 0; // умерло от голода

    public void reset() { born = eaten = starved = 0; } // обнуляю на новый день
}