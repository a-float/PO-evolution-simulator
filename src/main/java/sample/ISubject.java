package sample;

public interface ISubject {
    void addObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObservers(Animal baby);  //TODO change baby to some other name?
}
