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

    private String name;

    private final List<EditableInterval> intervals;

    private final List<ExerciseEditedConsumer> editedConsumers = new ArrayList<>();

    public EditableExercise(final Exercise base) {
        this.name = base.name();

        this.intervals = new ArrayList<>();
        for (int i = 0; i < base.numberOfIntervals(); ++i) {
            this.intervals.add(new EditableInterval(base.getIntervalAt(i)));
        }
    }

    private EditableExercise()
    {
        this.name = "";
        this.intervals = new ArrayList<>();
    }

    public static EditableExercise blankEditableExercise()
    {
        return new EditableExercise();
    }

    public String name()
    {
        return this.name;
    }

    public int numberOfIntervals()
    {
        return this.intervals.size();
    }

    public EditableInterval getIntervalAt(final int i)
    {
        return this.intervals.get(i);
    }

    public void changeName(final String updated)
    {
        this.name = updated;

        this.notifyExerciseEditConsumers(ExerciseEditedConsumer.EditAction.TITLE_EDITED);
    }

    public void moveInterval(final int from, final int to)
    {
        if (from < 0 || from >= this.intervals.size())
        {
            throw new IndexOutOfBoundsException("From index was out of bounds: " + from);
        }
        else if (to < 0 || to >= this.intervals.size())
        {
            throw new IndexOutOfBoundsException("To index was out of bounds: " + from);
        }
        else if (to == from)
        {
            throw new IllegalArgumentException("Why are you trying to move an interval back to the same place?");
        }

        EditableInterval moving = this.getIntervalAt(from);

        if (to < from)
        {
            // From greater than to so removing from will not affect position of to
            this.intervals.remove(from);
            this.intervals.add(to, moving);
        }
        else
        {
            // To is greater than from. So, if we remove from, we're going to affect the position of
            // to making the requested operation wrong.
            // So, remove, then subtract
            this.intervals.remove(from);
            this.intervals.add(to, moving);
        }

        this.notifyExerciseEditConsumers(ExerciseEditedConsumer.EditAction.MOVE_INTERVAL);
    }

    public void removeInterval(final int position)
    {
        this.intervals.remove(position);

        this.notifyExerciseEditConsumers(ExerciseEditedConsumer.EditAction.DELETE_INTERVAL);
    }

    public void addInterval()
    {
        this.intervals.add(EditableInterval.getDefaultInterval());
        this.notifyExerciseEditConsumers(ExerciseEditedConsumer.EditAction.ADD_INTERVAL);
    }

    public Exercise toExercise()
    {
        List<Interval> editableIntervals = new ArrayList<>();

        for (EditableInterval editable : this.intervals)
        {
            editableIntervals.add(editable.toInterval());
        }

        Exercise exercise = new Exercise(this.name(), editableIntervals);

        return exercise;
    }

    public void addExerciseEditedConsumer(final ExerciseEditedConsumer consumer)
    {
        this.editedConsumers.add(consumer);
    }

    private void notifyExerciseEditConsumers(final ExerciseEditedConsumer.EditAction action)
    {
        if (action == null)
        {
            throw new IllegalArgumentException("Must be a valid EditAction");
        }

        for (ExerciseEditedConsumer consumers : this.editedConsumers)
        {
            consumers.exerciseEdited(action);
        }
    }
}
