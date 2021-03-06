package com.budgebars.rotelle.workouts.editable;

import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.Interval;
import com.budgebars.rotelle.workouts.consumers.ExerciseEditedConsumer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 10/18/2017.
 */

public class EditableExercise implements Serializable {
  private static final long serialVersionUID = -5369871572648213687L;

  private String name;

  private final List<EditableInterval> intervals;

  private final List<ExerciseEditedConsumer> editedConsumers = new ArrayList<>();

  /**
   * Creates a new editable exercise with base values from the provided exercise. Exercises are
   * immutable, so editable exercises provide the mechanism for editing a given exercise and
   * creating a final immutable result.
   * @param base The exercise with the information that the edit should start with.
   */
  public EditableExercise(final Exercise base) {
    this.name = base.name();

    this.intervals = new ArrayList<>();

    for (int i = 0; i < base.numberOfIntervals(); ++i) {
      this.intervals.add(new EditableInterval(base.getIntervalAt(i)));
    }
  }

  private EditableExercise() {
    this.name = "";
    this.intervals = new ArrayList<>();
  }

  public static EditableExercise blankEditableExercise() {
    return new EditableExercise();
  }

  public String name() {
    return this.name;
  }

  /**
   * The current number of intervals in this editable exercise.
   * @return The current number of intervals.
   */
  public int numberOfIntervals() {
    return this.intervals.size();
  }

  public EditableInterval getIntervalAt(final int i) {
    return this.intervals.get(i);
  }

  /**
   * Changes the name of this exercise.
   * @param updated The new name for this exercise.
   */
  public void changeName(final String updated) {
    this.name = updated;

    this.notifyExerciseEditConsumers(ExerciseEditedConsumer.EditAction.TITLE_EDITED);
  }

  /**
   * Changes the orders of the intervals already in this editable exercise.
   * @param from The index of the interval to move.
   * @param to The index where the specified interval should be moved to.
   */
  public void moveInterval(final int from, final int to) {
    if ((from < 0) || (from >= this.intervals.size())) {
      throw new IndexOutOfBoundsException("From index was out of bounds: " + from);
    } else if ((to < 0) || (to >= this.intervals.size())) {
      throw new IndexOutOfBoundsException("To index was out of bounds: " + from);
    } else if (to == from) {
      throw new IllegalArgumentException(
        "Why are you trying to move an interval back to the same place?");
    }

    EditableInterval moving = this.getIntervalAt(from);

    this.intervals.remove(from);
    this.intervals.add(to, moving);

    this.notifyExerciseEditConsumers(ExerciseEditedConsumer.EditAction.MOVE_INTERVAL);
  }

  /**
   * Removes an interval from this exercise.
   * @param position The index specifying the interval to remove.
   */
  public void removeInterval(final int position) {
    this.intervals.remove(position);

    this.notifyExerciseEditConsumers(ExerciseEditedConsumer.EditAction.DELETE_INTERVAL);
  }

  public void addInterval() {
    this.intervals.add(EditableInterval.getDefaultInterval());
    this.notifyExerciseEditConsumers(ExerciseEditedConsumer.EditAction.ADD_INTERVAL);
  }

  /**
   * Creates an immutable exercise from the information edited in this editable exercise.
   * @return An immutable exercise object that can be run or saved.
   */
  public Exercise toExercise() {
    List<Interval> editableIntervals = new ArrayList<>();

    for (EditableInterval editable : this.intervals) {
      editableIntervals.add(editable.toInterval());
    }

    return new Exercise(this.name(), editableIntervals);
  }

  public void addExerciseEditedConsumer(final ExerciseEditedConsumer consumer) {
    this.editedConsumers.add(consumer);
  }

  private void notifyExerciseEditConsumers(final ExerciseEditedConsumer.EditAction action) {
    if (action == null) {
      throw new IllegalArgumentException("Must be a valid EditAction");
    }

    for (ExerciseEditedConsumer consumers : this.editedConsumers) {
      consumers.exerciseEdited(action);
    }
  }
}
