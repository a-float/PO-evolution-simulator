package me.evolutionSimulator;

public interface ISubject {
    void addObserver(IObserver observer);
    void removeObserver(IObserver observer);
    boolean isObservedBy(IObserver observer); //helps avoid double observations
    void notifyObservers(AnimalEvent event, Animal newborn);
}
