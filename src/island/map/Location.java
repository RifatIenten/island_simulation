package island.map;

import island.entities.Animal;
import island.entities.plants.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class Location {
    private final int x, y;                               // координаты клетки
    private volatile Plant plant;                         // растение на клетке (может быть null)
    private final CopyOnWriteArrayList<Animal> animals = new CopyOnWriteArrayList<>(); // список животных

    public Location(int x, int y) { this.x = x; this.y = y; }

    public int getX() { return x; }
    public int getY() { return y; }

    public Plant getPlant() { return plant; }
    public void setPlant(Plant plant) { this.plant = plant; }

    public void addAnimal(Animal a) { if (a != null) animals.add(a); }     // добавить животное
    public void removeAnimal(Animal a) { animals.remove(a); }              // убрать животное
    public List<Animal> getAnimalsSnapshot() { return new ArrayList<>(animals); } // безопасный снапшот

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location that = (Location) o;
        return x == that.x && y == that.y;
    }
    @Override public int hashCode() { return Objects.hash(x, y); }
    @Override public String toString() { return "(" + x + "," + y + ")"; }
}