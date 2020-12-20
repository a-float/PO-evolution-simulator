package evolutionSimulator;

public interface IObserver {
    void notify(AnimalEvent event, Animal parent, Animal newborn);
}
