package evolutionSimulator;

/**
 * Notifies sleepers when a certain amount of time(generations) has passed.
 * Could add a method tick() to hide currentGen counting
 * Every sleeper should have only one alarm set at a time.
 * Once an alarm has been set, it has to go off.
 *
 * TODO maybe should pass a time and a function? i wouldn't be able to cancel the alarm though
 */
public interface IClock {
    void addAlarm(ISleeper sleeper, int wakeUpTime);
    void fireAlarmEarly(ISleeper sleeper);
    void checkAlarmSchedule();
}
