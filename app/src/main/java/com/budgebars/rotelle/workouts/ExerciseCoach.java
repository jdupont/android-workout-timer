package com.budgebars.rotelle.workouts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 10/9/2017.
 */

public class ExerciseCoach {

    private static final int NOTIFICATION_INTERVAL_MILLIS = 500;

    private final Exercise exercise;

    private int intervalIndex;

    private Interval currentInterval;

    private ExerciseTimer currentTimer;

    private boolean isFinished;

    private final List<ExerciseTimer.TimerUpdateConsumer> timerUpdateConsumers  = new ArrayList<>();;

    private final List<ExerciseTimer.ExerciseFinishedConsumer> finishedConsumers = new ArrayList<>();

    private final List<ExerciseTimer.ExerciseStartedConsumer> startedConsumers = new ArrayList<>();

    public ExerciseCoach(final Exercise exercise)
    {
        this.exercise = exercise;
        this.intervalIndex = 0;
        this.currentInterval = this.exercise.getIntervalAt(this.intervalIndex);
        this.isFinished = false;

        this.currentTimer = this.makeTimerForCurrentInterval();

        this.addFinishedConsumer(new ExerciseTimer.ExerciseFinishedConsumer() {
            @Override
            public void exerciseFinished() {

                if (!ExerciseCoach.this.isFinished) {
                    ExerciseCoach.this.advanceToNextInterval();
                    ExerciseCoach.this.currentTimer.startTimer();
                }
            }
        });
    }

    public void addTimerUpdateConsumer(final ExerciseTimer.TimerUpdateConsumer consumer)
    {
        this.timerUpdateConsumers.add(consumer);
        this.currentTimer.addUpdateConsumer(consumer);
    }

    public void addFinishedConsumer(final ExerciseTimer.ExerciseFinishedConsumer consumer)
    {
        this.finishedConsumers.add(consumer);
        this.currentTimer.addFinishedConsumer(consumer);
    }

    public void addStartedConsumer(final ExerciseTimer.ExerciseStartedConsumer consumer)
    {
        this.startedConsumers.add(consumer);
        this.currentTimer.addStartedConsumer(consumer);
    }

    public void start()
    {
        if (this.currentTimer.isRunning())
        {
            throw new IllegalStateException("Already running");
        }

        this.currentTimer.startTimer();
    }

    public void stop()
    {
        if (!this.currentTimer.isRunning())
        {
            throw new IllegalStateException("Not currently running");
        }

        this.currentTimer.stopTimer();
    }

    private boolean advanceToNextInterval()
    {
        this.intervalIndex = this.intervalIndex + 1;

        if (this.intervalIndex < this.exercise.numberOfIntervals())
        {
            this.currentInterval = this.exercise.getIntervalAt(this.intervalIndex);
            this.currentTimer = this.makeTimerForCurrentInterval();

            return true;
        }
        else if (this.intervalIndex == this.exercise.numberOfIntervals())
        {
            this.setFinished();
            return false;
        }
        else
        {
            throw new ArrayIndexOutOfBoundsException("The requested interval does not exist");
        }
    }

    private void setFinished()
    {
        this.isFinished = true;
    }

    private ExerciseTimer makeTimerForCurrentInterval()
    {
        ExerciseTimer timer = new ExerciseTimer(this.currentInterval);
        timer.addStartedConsumer(this.startedConsumers);
        timer.addFinishedConsumer(this.finishedConsumers);
        timer.addUpdateConsumer(this.timerUpdateConsumers);
        return timer;
    }
}
