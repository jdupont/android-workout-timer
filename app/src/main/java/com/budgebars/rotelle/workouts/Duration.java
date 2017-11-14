package com.budgebars.rotelle.workouts;

import java.io.Serializable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jules on 10/16/2017.
 */

public class Duration implements Serializable {

    private static final TimeUnit UNIT = TimeUnit.MILLISECONDS;
	private static final long serialVersionUID = 5229438907976239730L;

	private final long milliseconds;

    public Duration(final long value, final TimeUnit unit)
    {
        this.milliseconds = TimeUnit.MILLISECONDS.convert(value, unit);
    }

    public Duration add(final Duration other)
    {
        return new Duration(this.milliseconds + other.milliseconds, Duration.UNIT);
    }

    public long get(final TimeUnit unit)
    {
        return unit.convert(this.milliseconds, Duration.UNIT);
    }

    private boolean hasHours()
    {
        return (Duration.UNIT.toHours(this.milliseconds) > 0);
    }

    private boolean hasMinutes()
    {
        return (Duration.UNIT.toMinutes(this.milliseconds) > 0);
    }

    private boolean hasSeconds()
    {
        return (Duration.UNIT.toSeconds(this.milliseconds) > 0);
    }

    @Override
    public String toString()
    {
        return String.format(Locale.US,"%02dm%02ds",
                Duration.UNIT.toMinutes(this.milliseconds),
                Duration.UNIT.toSeconds(this.milliseconds) -
                        TimeUnit.MINUTES.toSeconds(Duration.UNIT.toMinutes(this.milliseconds))
        );
    }

    public static Duration zero()
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
