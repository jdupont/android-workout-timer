package com.budgebars.rotelle.workouts.consumers;

import com.budgebars.rotelle.workouts.Duration;

/**
 * Created by Jules on 10/13/2017.
 */

public interface TimerUpdateConsumer
{
    public void timerUpdate(final Duration remainingTime);
}