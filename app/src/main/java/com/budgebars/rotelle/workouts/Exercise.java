package com.budgebars.rotelle.workouts;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jules on 10/9/2017.
 */

public class Exercise implements Serializable {
  private static final long serialVersionUID = -8871430239563028072L;

  private final String name;

  private final List<Interval> intervals;

  /**
   * Creates an exercise.
   * @param name The name of the exercise.
   * @param intervals The intervals to populate this exercise with.
   */
  public Exercise(final String name, final List<Interval> intervals) {
    if (intervals.isEmpty()) {
      throw new IllegalArgumentException("Cannot create an exercise with no intervals.");
    } else if (name.trim().isEmpty()) {
      throw new IllegalArgumentException("Cannot have an empty exercise name.");
    }

    this.name = name;
    this.intervals = intervals;
  }

  /**
   * Gets the total length of all of the intervals in this exercise.
   * @return The sum of the lengths of the intervals in this exercise.
   */
  public Duration totalLength() {
    Duration total = Duration.zero();

    for (Interval interval : this.intervals) {
      total = total.plus(interval.getLength());
    }

    return total;
  }

  public String name() {
    return this.name;
  }

  public int numberOfIntervals() {
    return this.intervals.size();
  }

  public Interval getIntervalAt(final int index) {
    return this.intervals.get(index);
  }

  @Override
  public String toString() {
    return this.name;
  }
}
