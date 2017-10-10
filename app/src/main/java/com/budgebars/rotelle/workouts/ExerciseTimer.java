package com.budgebars.rotelle.workouts;

import android.os.CountDownTimer;

import java.util.function.LongConsumer;


/**
 * Created by Jules on 10/9/2017.
 */

public class ExerciseTimer extends CountDownTimer {

    private static final int NOTIFICATION_TICKS_MILLIS = 500;

    private final ExerciseTimer.TimerUpdateConsumer onTickMethod;

    private final ExerciseTimer.ExerciseFinishedConsumer finishedMethod;
    
    public ExerciseTimer(final int intervalLength, final TimerUpdateConsumer onTickMethod, final ExerciseFinishedConsumer finishedMethod) {
        super(intervalLength * Units.SECONDS_TO_MILLIS_FACTOR, ExerciseTimer.NOTIFICATION_TICKS_MILLIS);

        this.onTickMethod = onTickMethod;
        this.finishedMethod = finishedMethod;
    }

    @Override
    public void onTick(long l) {
        this.onTickMethod.timerUpdate(l / Units.SECONDS_TO_MILLIS_FACTOR);
    }

    @Override
    public void onFinish() {
        this.finishedMethod.exerciseFinished();
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
