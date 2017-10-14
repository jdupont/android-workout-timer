package com.budgebars.rotelle.workouts;

import java.io.Serializable;

/**
 * Created by Jules on 10/9/2017.
 */

public class Interval implements Serializable {

    private final String name;

    private final int length;

    public Interval(final String name, final int length)
    {
        this.name = name;
        this.length = length;
    }

    public int getLength()
    {
        return this.length;
    }

    public String getName()
    {
        return this.name;
    }

    @Override
    public String toString()
    {
        return this.name + " (" + this.length + ")";
    }
}
