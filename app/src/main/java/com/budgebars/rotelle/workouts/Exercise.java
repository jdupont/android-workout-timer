package com.budgebars.rotelle.workouts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 10/9/2017.
 */

public class Exercise implements Serializable {

    private final String name;

    private final List<Interval> intervals;

    public Exercise(final String name, final List<Interval> intervals)
    {
        if (intervals.isEmpty())
        {
            throw new IllegalArgumentException("Cannot create an exercise with no intervals.");
        }

        this.name = name;
        this.intervals = intervals;
    }

    public int totalLength()
    {
        int total = 0;

        for (Interval interval : this.intervals)
        {
            total = total + interval.getLength();
        }

        return total;
    }

    public int numberOfIntervals()
    {
        return this.intervals.size();
    }

    public Interval getIntervalAt(int index)
    {
        return this.intervals.get(index);
    }

    public static Exercise createMockExercise()
    {
        List<Interval> intervals = new ArrayList<Interval>();

        intervals.add(new Interval("Warmup", 15));
        intervals.add(new Interval("Bench 1", 15));
        intervals.add(new Interval("Rest 1", 15));

        return new Exercise("Sample Bench Workout", intervals);
    }
}
