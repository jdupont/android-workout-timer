package com.budgebars.rotelle.workouts;

import java.io.Serializable;

/**
 * Created by Jules on 10/9/2017.
 */

public class Interval implements Serializable {
  private static final long serialVersionUID = -2806873883004933748L;

  private final String name;

  private final Duration length;

  public Interval(final String name, final Duration length) {
    if (name.trim().isEmpty()) {
      throw new IllegalArgumentException("Cannot have an empty interval name.");
    }

    this.name = name;
    this.length = length;
  }

  public Duration getLength() {
    return this.length;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String toString() {
    return this.name + " (" + this.length + ") ";
  }
}
