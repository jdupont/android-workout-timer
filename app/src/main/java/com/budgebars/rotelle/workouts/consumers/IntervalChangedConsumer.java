package com.budgebars.rotelle.workouts.consumers;

/**
 * Created by Jules on 10/13/2017.
 */

public interface IntervalChangedConsumer
{
    public void intervalChanged(final String intervalName, final int intervalLength);
}
