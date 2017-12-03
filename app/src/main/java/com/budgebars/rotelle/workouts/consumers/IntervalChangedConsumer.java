package com.budgebars.rotelle.workouts.consumers;

import com.budgebars.rotelle.workouts.Duration;

/**
 * Created by Jules on 10/13/2017.
 */

public interface IntervalChangedConsumer {
  public void intervalChanged(final String intervalName, final Duration intervalLength);
}
