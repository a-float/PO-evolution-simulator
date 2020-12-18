package sample;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractISubject implements ISubject {
    List<IObserver> observers = new ArrayList<>();
    @Override
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public abstract void notifyObservers(Animal baby);
}
