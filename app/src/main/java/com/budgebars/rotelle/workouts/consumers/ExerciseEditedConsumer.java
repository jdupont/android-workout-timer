package com.budgebars.rotelle.workouts.consumers;

/**
 * Created by Jules on 10/18/2017.
 */

public interface ExerciseEditedConsumer {
    public void exerciseEdited(final EditAction action);

    public enum EditAction
    {
        ADD_INTERVAL,
        MOVE_INTERVAL,
        DELETE_INTERVAL,
        TITLE_EDITED
    }
}
