package sample;

public interface IDataPair<F, S> {
    void setFirst(F first);
    F getFirst(F first);
    void setSecond(S second);
    S getSecond();
    String getStringPair();
}
