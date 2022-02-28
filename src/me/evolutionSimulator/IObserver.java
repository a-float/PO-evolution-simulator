package me.evolutionSimulator;

public interface IObserver {
    void notify(AnimalEvent event, Animal subject, Animal newborn);
}
