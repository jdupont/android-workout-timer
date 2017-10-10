package com.budgebars.rotelle.workouts;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jules on 10/9/2017.
 */

public class ExerciseTimer {

    private static final int NOTIFICATION_TICKS_MILLIS = 500;

    private final Interval interval;

    private final CountDownTimer timer;

    private final List<TimerUpdateConsumer> onTickMethods;

    private final List<ExerciseFinishedConsumer> finishedMethods;

    private final List<ExerciseStartedConsumer> startedMethods;

    private boolean isRunning = false;

    public ExerciseTimer(final Interval interval) {

        this.interval = interval;
        this.timer = new CountDownTimer(this.interval.getLength() * Units.SECONDS_TO_MILLIS_FACTOR, ExerciseTimer.NOTIFICATION_TICKS_MILLIS) {
            @Override
            public void onTick(long l) {

                for (TimerUpdateConsumer consumer : ExerciseTimer.this.onTickMethods)
                {
                    consumer.timerUpdate(l / Units.SECONDS_TO_MILLIS_FACTOR);
                }
            }

            @Override
            public void onFinish() {
                for (ExerciseFinishedConsumer consumer : ExerciseTimer.this.finishedMethods)
                {
                    consumer.exerciseFinished();
                }
            }
        };

        this.onTickMethods = new ArrayList<>();
        this.finishedMethods = new ArrayList<>();
        this.startedMethods = new ArrayList<>();

        this.finishedMethods.add(new ExerciseFinishedConsumer() {
            @Override
            public void exerciseFinished() {
                ExerciseTimer.this.isRunning = false;
            }
        });
    }

    public void addUpdateConsumer(final TimerUpdateConsumer consumer)
    {
        this.onTickMethods.add(consumer);
    }

    public void addUpdateConsumer(final List<TimerUpdateConsumer> consumers)
    {
        this.onTickMethods.addAll(consumers);
    }

    public void addStartedConsumer(final ExerciseStartedConsumer consumer)
    {
        this.startedMethods.add(consumer);
    }

    public void addStartedConsumer(final List<ExerciseStartedConsumer> consumers)
    {
        this.startedMethods.addAll(consumers);
    }

    public void addFinishedConsumer(final ExerciseFinishedConsumer consumer)
    {
        this.finishedMethods.add(consumer);
    }

    public void addFinishedConsumer(final List<ExerciseFinishedConsumer> consumers)
    {
        this.finishedMethods.addAll(consumers);
    }

    public void startTimer()
    {
        if (this.isRunning)
        {
            throw new IllegalStateException("Timer is already running");
        }

        this.timer.start();
        this.isRunning = true;

        for (ExerciseStartedConsumer consumer : this.startedMethods)
        {
            consumer.exerciseStarted();
        }
    }

    public void stopTimer()
    {
        if (!this.isRunning)
        {
            throw new IllegalStateException("Timer is not running");
        }

        this.timer.cancel();
        this.isRunning = false;
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }

    public interface TimerUpdateConsumer
    {
        public void timerUpdate(long remainingTime);
    }

    public interface ExerciseStartedConsumer
    {
        public void exerciseStarted();
    }

    public interface ExerciseFinishedConsumer
    {
        public void exerciseFinished();
    }
}
