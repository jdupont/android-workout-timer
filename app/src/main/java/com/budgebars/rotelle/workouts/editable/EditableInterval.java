package com.budgebars.rotelle.workouts.editable;

import com.budgebars.rotelle.workouts.Duration;
import com.budgebars.rotelle.workouts.Interval;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jules on 10/18/2017.
 */

public class EditableInterval implements Serializable {

	private static final long serialVersionUID = -7539378757342639929L;

	private static final int DEFAULT_INTERVAL_LENGTH_SECONDS = 30;

	private static final String DEFAULT_INTERVAL_NAME = "New Interval";

	private String name;

    private Duration length;

    public EditableInterval(final Interval interval)
    {
        this(interval.getName(), interval.getLength());
    }

    private EditableInterval(final String name, final Duration length)
    {
        this.name = name;
        this.length = length;
    }

    public String name()
    {
        return this.name;
    }

    public Duration length()
    {
        return this.length;
    }

    public void changeName(final String updated)
    {
        this.name = updated;
    }

    public void changeDuration(final Duration updated)
    {
        this.length = updated;
    }

    public Interval toInterval()
    {
        return new Interval(this.name(), this.length());
    }

    public static EditableInterval getDefaultInterval()
    {
        return new EditableInterval(EditableInterval.DEFAULT_INTERVAL_NAME, new Duration(EditableInterval.DEFAULT_INTERVAL_LENGTH_SECONDS, TimeUnit.SECONDS));
    }
}
