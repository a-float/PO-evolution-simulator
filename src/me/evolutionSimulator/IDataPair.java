package me.evolutionSimulator;

/**
 * used for storing data
 * Integer values are used, when incrementing them is needed.
 */
public interface IDataPair<F, S> {
    void setFirst(F first);
    F getFirst(F first);
    void setSecond(S second);
    S getSecond();
    String getStringPair();
}
