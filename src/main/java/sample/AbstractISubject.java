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
    public boolean isObservedBy(IObserver observer){
        for(IObserver obs: observers){
            if(obs == observer)return true; //checking just the references
        }
        return false;
    }
}
