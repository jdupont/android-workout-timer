package com.budgebars.rotelle.workouts;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jules on 10/16/2017.
 */

public class Duration implements Serializable {

    private static final TimeUnit UNIT = TimeUnit.MILLISECONDS;

    private final long milliseconds;

    public Duration(final long value, final TimeUnit unit)
    {
        this.milliseconds = Duration.UNIT.MILLISECONDS.convert(value, unit);
    }

    public Duration add(final Duration other)
    {
        return new Duration(this.milliseconds + other.milliseconds, Duration.UNIT);
    }

    public long get(final TimeUnit unit)
    {
        return unit.convert(milliseconds, Duration.UNIT);
    }

    public static Duration ZERO()
    {
        return new Duration(0, Duration.UNIT);
    }

    public static Duration fromSeconds(final long value)
    {
        return new Duration(value, TimeUnit.SECONDS);
    }

    public static Duration fromMilliSeconds(final long value)
    {
        return new Duration(value, TimeUnit.MILLISECONDS);
    }
}
