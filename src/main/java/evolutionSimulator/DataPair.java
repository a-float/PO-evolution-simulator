package evolutionSimulator;


public class DataPair<N,V> implements IDataPair<N,V> {
    private N first;
    private V second;

    DataPair(N first, V second){
        this.first = first;
        this.second = second;
    }

    public String toString(){
        return "{name: "+ first +", value: "+ second +"}";
    }

    public String getStringPair(){
        return String.format("%s: %s", first, second);
    }

    public N getFirst() {
        return first;
    }

    @Override
    public void setFirst(N first) {
        this.first = first;
    }

    @Override
    public N getFirst(N name) {
        return this.first;
    }

    @Override
    public void setSecond(V second) {
        this.second = second;
    }
    @Override
    public V getSecond() {
        return second;
    }
}
