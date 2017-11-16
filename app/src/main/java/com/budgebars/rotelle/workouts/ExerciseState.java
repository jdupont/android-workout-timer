package com.budgebars.rotelle.workouts;

/**
 * Created by Jules on 10/12/2017.
 */

public enum ExerciseState {
  READY("Ready"),
  RUNNING("Running"),
  PAUSED("Paused"),
  DONE("Done");

  private final String description;

  private ExerciseState(final String description) {
    this.description = description;
  }

  public String toString() {
    return this.description;
  }
}
