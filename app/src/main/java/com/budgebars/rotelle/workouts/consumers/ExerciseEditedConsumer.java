package com.budgebars.rotelle.workouts.consumers;

import java.io.Serializable;

/**
 * Created by Jules on 10/18/2017.
 */

public interface ExerciseEditedConsumer extends Serializable {
  public void exerciseEdited(final EditAction action);

  public enum EditAction {
      ADD_INTERVAL,
      MOVE_INTERVAL,
      DELETE_INTERVAL,
      TITLE_EDITED
  }
}
