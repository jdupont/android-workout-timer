package com.budgebars.rotelle.workouts.editable;

import com.budgebars.rotelle.workouts.Duration;
import com.budgebars.rotelle.workouts.Interval;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jules on 10/18/2017.
 */

public class EditableInterval {

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

    public static EditableInterval getDefaultInterval()
    {
        return new EditableInterval("New Interval", new Duration(30, TimeUnit.SECONDS));
    }
}
