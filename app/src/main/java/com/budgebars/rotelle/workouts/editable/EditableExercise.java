package com.budgebars.rotelle.workouts.editable;

import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.Interval;
import com.budgebars.rotelle.workouts.consumers.ExerciseEditedConsumer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 10/18/2017.
 */

public class EditableExercise {

    private String name;

    private final List<EditableInterval> intervals;

    private final List<ExerciseEditedConsumer> editedConsumers = new ArrayList<>();

    public EditableExercise(final Exercise base)
    {
        this.name = base.name();

        this.intervals = new ArrayList<>();
        for (int i = 0; i < base.numberOfIntervals(); ++i) {
            this.intervals.add(new EditableInterval(base.getIntervalAt(i)));
        }
    }

    public String name()
    {
        return this.name;
    }

    public int numberOfIntervals()
    {
        return this.intervals.size();
    }

    public EditableInterval getIntervalAt(int i)
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

        this.notifyExerciseEditConsumers(null);

        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void addInterval()
    {
        this.intervals.add(EditableInterval.getDefaultInterval());
        this.notifyExerciseEditConsumers(ExerciseEditedConsumer.EditAction.ADD_INTERVAL);
    }

    public Exercise toExercise()
    {
        List<Interval> intervals = new ArrayList<>();

        for (EditableInterval editable : this.intervals)
        {
            intervals.add(editable.toInterval());
        }

        Exercise exercise = new Exercise(this.name(), intervals);

        return exercise;
    }

    public void addExerciseEditedConsumer(final ExerciseEditedConsumer consumer)
    {
        this.editedConsumers.add(consumer);
    }

    private void notifyExerciseEditConsumers(final ExerciseEditedConsumer.EditAction action)
    {
        for (ExerciseEditedConsumer consumers : this.editedConsumers)
        {
            consumers.exerciseEdited(action);
        }
    }
}
