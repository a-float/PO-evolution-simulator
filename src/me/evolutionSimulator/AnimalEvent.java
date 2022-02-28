package me.evolutionSimulator;

/**
 * used in observers notifications
 */
public enum AnimalEvent {
    NEW_ANIMAL, //animal notifies its observers when its born
    NEW_CHILD,  //parents notify their observers when they have a baby
    DEATH       //animal notifies its observes when it dies
}
