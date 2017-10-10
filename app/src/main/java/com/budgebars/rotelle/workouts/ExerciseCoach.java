package com.budgebars.rotelle.workouts;

/**
 * Created by Jules on 10/9/2017.
 */

public class ExerciseCoach {

    private static final int NOTIFICATION_INTERVAL_MILLIS = 500;

    private final Exercise exercise;

    private int intervalIndex;

    private Interval currentInterval;

    public ExerciseCoach(final Exercise exercise)
    {
        this.exercise = exercise;
        this.intervalIndex = 0;
        this.currentInterval = this.exercise.getIntervalAt(this.intervalIndex);
    }

    public ExerciseTimer getTimerForCurrentInterval(final ExerciseTimer.TimerUpdateConsumer onTickMethod, ExerciseTimer.ExerciseFinishedConsumer finishedMethod)
    {
        ExerciseTimer timer = new ExerciseTimer(this.currentIntervalLength());
        timer.addUpdateConsumer(onTickMethod);
        timer.addFinishedConsumer(finishedMethod);
        return timer;
    }

    public int currentIntervalLength()
    {
        return this.currentInterval.getLength();
    }

    public String currentIntervalName()
    {
        return this.currentInterval.getName();
    }

    public void advanceToNextInterval()
    {
        if (this.intervalIndex < this.exercise.numberOfIntervals())
        {
            throw new ArrayIndexOutOfBoundsException("There is no next interval. Current: "
                    + this.intervalIndex + " length: " + this.exercise.numberOfIntervals());
        }

        this.intervalIndex = this.intervalIndex + 1;
        this.currentInterval = this.exercise.getIntervalAt(this.intervalIndex);
    }
}
