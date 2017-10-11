package com.budgebars.rotelle.workouts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 10/9/2017.
 */

public class ExerciseCoach {

    private final Exercise exercise;

    private int intervalIndex;

    private Interval currentInterval;

    private IntervalTimer currentTimer;

    private boolean isFinished;

    private final List<IntervalTimer.TimerUpdateConsumer> timerUpdateConsumers  = new ArrayList<>();;

    private final List<IntervalTimer.IntervalFinishedConsumer> finishedConsumers = new ArrayList<>();

    private final List<IntervalTimer.IntervalStartedConsumer> intervalStartedConsumers = new ArrayList<>();

    private final List<IntervalChangedConsumer> changedConsumers = new ArrayList<>();

    private final List<ExerciseDoneConsumer> doneConsumers = new ArrayList<>();

    private final List<ExerciseStartedConsumer> exerciseStartedConsumers = new ArrayList<>();

    public ExerciseCoach(final Exercise exercise)
    {
        this.exercise = exercise;
        this.intervalIndex = 0;
        this.currentInterval = this.exercise.getIntervalAt(this.intervalIndex);
        this.isFinished = false;

        this.currentTimer = this.makeTimerForCurrentInterval();

        this.addFinishedConsumer(new IntervalTimer.IntervalFinishedConsumer() {
            @Override
            public void intervalFinished() {

                if (!ExerciseCoach.this.isFinished) {
                    if (ExerciseCoach.this.advanceToNextInterval()) {
                        ExerciseCoach.this.currentTimer.startTimer();
                    }
                }
            }
        });
    }

    public String currentIntervalName()
    {
        return this.currentInterval.getName();
    }

    public int currentIntervalLength()
    {
        return this.currentInterval.getLength();
    }

    public void addTimerUpdateConsumer(final IntervalTimer.TimerUpdateConsumer consumer)
    {
        this.timerUpdateConsumers.add(consumer);
        this.currentTimer.addUpdateConsumer(consumer);
    }

    public void addFinishedConsumer(final IntervalTimer.IntervalFinishedConsumer consumer)
    {
        this.finishedConsumers.add(consumer);
        this.currentTimer.addFinishedConsumer(consumer);
    }

    public void addIntervalStartedConsumer(final IntervalTimer.IntervalStartedConsumer consumer)
    {
        this.intervalStartedConsumers.add(consumer);
        this.currentTimer.addStartedConsumer(consumer);
    }

    public void addDoneConsumer(final ExerciseDoneConsumer consumer)
    {
        this.doneConsumers.add(consumer);
    }

    public void addExerciseStartedConsumer(final ExerciseStartedConsumer consumer)
    {
        this.exerciseStartedConsumers.add(consumer);
    }

    public void addChangedConsumer(final IntervalChangedConsumer consumer)
    {
        this.changedConsumers.add(consumer);
    }

    public void start()
    {
        if (this.isRunning())
        {
            throw new IllegalStateException("Already running");
        }

        this.notifyExerciseStarted();
        this.currentTimer.startTimer();
    }

    public void stop()
    {
        if (!this.isRunning())
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

            this.notifyIntervalChanged();

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

        for (ExerciseDoneConsumer consumer : this.doneConsumers)
        {
            consumer.exerciseDone();
        }
    }

    private IntervalTimer makeTimerForCurrentInterval()
    {
        IntervalTimer timer = new IntervalTimer(this.currentInterval);
        timer.addStartedConsumer(this.intervalStartedConsumers);
        timer.addFinishedConsumer(this.finishedConsumers);
        timer.addUpdateConsumer(this.timerUpdateConsumers);
        return timer;
    }

    private boolean isRunning()
    {
        return this.currentTimer.isRunning();
    }

    public void reset()
    {
        if (this.isRunning())
        {
            this.stop();
        }

        this.intervalIndex = 0;
        this.currentInterval = this.exercise.getIntervalAt(this.intervalIndex);
        this.makeTimerForCurrentInterval();

        this.notifyIntervalChanged();
    }

    private void notifyIntervalChanged()
    {
        for (IntervalChangedConsumer consumer : this.changedConsumers)
        {
            consumer.intervalChanged(this.currentInterval.getName(), this.currentInterval.getLength());
        }
    }

    private void notifyExerciseStarted()
    {
        for (ExerciseStartedConsumer consumer : this.exerciseStartedConsumers)
        {
            consumer.exerciseStarted();
        }
    }

    public interface IntervalChangedConsumer
    {
        public void intervalChanged(final String intervalName, final int intervalLength);
    }

    public interface ExerciseStartedConsumer
    {
        public void exerciseStarted();
    }

    public interface ExerciseDoneConsumer
    {
        public void exerciseDone();
    }
}
