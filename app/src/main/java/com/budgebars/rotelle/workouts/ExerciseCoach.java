package com.budgebars.rotelle.workouts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 10/9/2017.
 */
public class ExerciseCoach {

    private int intervalIndex;

    private Interval currentInterval;

    private IntervalTimer currentTimer;

    /**
     * Saves state of current interval which is necessary during pauses (since Android timer does
     * not offer pause functionality out of the box).
     */
    private long timeRemainingInCurrentInterval;

    private ExerciseState state;

    private final Exercise exercise;

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
        this.timeRemainingInCurrentInterval = this.currentInterval.getLength();

        this.addTimerUpdateConsumer(new IntervalTimer.TimerUpdateConsumer() {
            @Override
            public void timerUpdate(long remainingTime) {
                ExerciseCoach.this.timeRemainingInCurrentInterval = remainingTime;
            }
        });

        this.addFinishedConsumer(new IntervalTimer.IntervalFinishedConsumer() {
            @Override
            public void intervalFinished() {

                if (!ExerciseCoach.this.isFinished()) {
                    if (ExerciseCoach.this.advanceToNextInterval()) {
                        ExerciseCoach.this.currentTimer.startTimer();
                    }
                }
            }
        });

        this.currentTimer = this.makeTimerForCurrentInterval();
        this.state = ExerciseState.READY;
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

    /**
     * Starts the coach from the beginning of the exercise.
     * Only valid when the timer is in the READY state. Transitions coach from READY to running
     * and gets the timer and internals moving.
     */
    public void start()
    {
        if (!this.isReady())
        {
            throw new IllegalStateException("Not ready. Cannot start. Current state: " + this.state.toString());
        }

        this.notifyExerciseStarted();
        this.currentTimer.startTimer();
        this.state = ExerciseState.RUNNING;
    }

    /**
     * Pauses the coach at the current point in the given exercise and saves state so that the coach
     * can resume in same place. Only valid when the coach is in the RUNNING state. Transitions
     * coach from RUNNING to PAUSED.
     */
    public void pause()
    {
        if (!this.isRunning())
        {
            throw new IllegalStateException("Not currently running. Current state: " + this.state.toString());
        }

        this.currentTimer.stopTimer();
        this.state = ExerciseState.PAUSED;
    }

    /**
     * Resumes the coach from the point where it was paused. Only valid when the coach is in the
     * PAUSED state. Transitions the coach from a PAUSED state to a RUNNING state.
     */
    public void resume()
    {
        if (!this.isPaused())
        {
            throw new IllegalStateException("Not currently paused. Current state: " + this.state.toString());
        }

        this.makeTimerForCurrentInterval(this.timeRemainingInCurrentInterval);
        this.currentTimer.startTimer();
        this.state = ExerciseState.RUNNING;
    }

    /**
     * Resets the coach to the READY state at the beginning of the current exercise. Valid from the RUNNING,
     * PAUSED, and DONE states.
     */
    public void reset()
    {
        if (this.isRunning())
        {
            this.currentTimer.stopTimer();
        }

        this.intervalIndex = 0;
        this.currentInterval = this.exercise.getIntervalAt(this.intervalIndex);
        this.makeTimerForCurrentInterval();

        this.notifyIntervalChanged();
    }

    /**
     * Transitions the timer into the DONE state after the last interval finishes. Only valid from the
     * RUNNING state.
     */
    private void done()
    {
        if (!this.isRunning())
        {
            throw new IllegalStateException("Timer not currently running. Current state: " + this.state);
        }
        else if (this.intervalIndex == this.exercise.numberOfIntervals())
        {
            throw new IllegalStateException("The interval index does not indicate that the exercise" +
                    "has been run all the way through.");
        }

        this.state = ExerciseState.DONE;
        this.notifyExerciseDone();
    }

    private boolean advanceToNextInterval()
    {
        this.intervalIndex = this.intervalIndex + 1;

        if (this.intervalIndex < this.exercise.numberOfIntervals())
        {
            this.currentInterval = this.exercise.getIntervalAt(this.intervalIndex);
            this.currentTimer = this.makeTimerForCurrentInterval();
            this.timeRemainingInCurrentInterval = this.currentInterval.getLength();

            this.notifyIntervalChanged();

            return true;
        }
        else if (this.intervalIndex == this.exercise.numberOfIntervals())
        {
            this.done();
            return false;
        }
        else
        {
            throw new ArrayIndexOutOfBoundsException("The requested interval does not exist");
        }
    }

    private boolean isReady()
    {
        return this.state == ExerciseState.READY;
    }

    private boolean isRunning()
    {
        return this.state == ExerciseState.RUNNING;
    }

    private boolean isPaused()
    {
        return this.state == ExerciseState.PAUSED;
    }

    private boolean isFinished()
    {
        return this.state == ExerciseState.DONE;
    }

    private IntervalTimer makeTimerForCurrentInterval()
    {
        return this.makeTimerForCurrentInterval(this.currentIntervalLength());
    }

    private IntervalTimer makeTimerForCurrentInterval(final long timerLength)
    {
        IntervalTimer timer = new IntervalTimer(timerLength);
        timer.addStartedConsumer(this.intervalStartedConsumers);
        timer.addFinishedConsumer(this.finishedConsumers);
        timer.addUpdateConsumer(this.timerUpdateConsumers);
        return timer;
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

    private void notifyExerciseDone()
    {
        for (ExerciseDoneConsumer consumer : this.doneConsumers)
        {
            consumer.exerciseDone();
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
