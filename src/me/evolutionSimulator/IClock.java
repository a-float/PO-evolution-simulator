package me.evolutionSimulator;

/**
 * Notifies sleepers when a certain amount of time(generations) has passed.
 * Could add a method tick() to hide currentGen counting
 * Every sleeper should have only one alarm set at a time.
 * Once an alarm has been set, it has to go off.
 *
 */
public interface IClock {
    void addAlarm(ISleeper sleeper, int wakeUpTime);
    void fireAlarmEarly(ISleeper sleeper);
    void checkAlarmSchedule();
}
