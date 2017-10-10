package com.budgebars.rotelle.workouts;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jules on 10/9/2017.
 */

public class ExerciseTimer {

    private static final int NOTIFICATION_TICKS_MILLIS = 500;

    private final CountDownTimer timer;

    private final List<TimerUpdateConsumer> onTickMethods;

    private final List<ExerciseFinishedConsumer> finishedMethods;

    public ExerciseTimer(final int intervalLength) {

        this.timer = new CountDownTimer(intervalLength * Units.SECONDS_TO_MILLIS_FACTOR, ExerciseTimer.NOTIFICATION_TICKS_MILLIS) {
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
    }

    public void addUpdateConsumer(final TimerUpdateConsumer consumer)
    {
        this.onTickMethods.add(consumer);
    }

    public void addFinishedConsumer(final ExerciseFinishedConsumer consumer)
    {
        this.finishedMethods.add(consumer);
    }

    public void startTimer()
    {
        this.timer.start();
    }

    public void stopTimer()
    {
        this.timer.cancel();
    }

    public interface TimerUpdateConsumer
    {
        public void timerUpdate(long remainingTime);
    }

    public interface ExerciseFinishedConsumer
    {
        public void exerciseFinished();
    }

}
