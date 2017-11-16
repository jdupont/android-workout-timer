package com.budgebars.rotelle.gui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.workouts.Duration;
import com.budgebars.rotelle.workouts.consumers.ExerciseEditedConsumer;
import com.budgebars.rotelle.workouts.editable.EditableExercise;
import com.budgebars.rotelle.workouts.editable.EditableInterval;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jules on 10/16/2017.
 */

public class EditIntervalAdapter extends BaseAdapter {
  private static final String SECONDS_FORMAT = "%d";

  private final EditableExercise exercise;

  private final Activity activity;

  private final List<IntervalAddedConsumer> intervalAddedConsumers = new ArrayList<>();

  /**
   * Creates a new adapter that will take an editable exercise and display a form for editing all
   * intervals and attributes of the exercise.
   * @param exercise The exercise to edit.
   * @param parent The parent activity.
   */
  public EditIntervalAdapter(final EditableExercise exercise, final Activity parent) {
    super();

    this.exercise = exercise;
    this.activity = parent;

    this.exercise.addExerciseEditedConsumer(new ExerciseEditedConsumer() {
      @Override
      public void exerciseEdited(final EditAction action) {

        if (action != EditAction.TITLE_EDITED) {
          // Title is outside of the list view so only need to re-render the
          // list view when something other than the title has changed
          EditIntervalAdapter.this.notifyDataSetChanged();
        }

        if (action == EditAction.ADD_INTERVAL) {
          EditIntervalAdapter.this.notifyIntervalAdded();
        }
      }
    });
  }

  public void addItemAddedConsumer(final IntervalAddedConsumer item) {
    this.intervalAddedConsumers.add(item);
  }

  @Override
  public int getCount() {
    return this.exercise.numberOfIntervals();
  }

  @Override
  public Object getItem(final int position) {
    return this.exercise.getIntervalAt(position);
  }

  @Override
  public long getItemId(final int position) {
    return position;
  }

  @Override
  public View getView(final int position, final View convertView, final ViewGroup parent) {
    View inflated = convertView;
    if (inflated == null) {
      LayoutInflater inflater = this.activity.getLayoutInflater();
      inflated = inflater.inflate(R.layout.item_interval_list_edit, parent, false);
    }

    final EditableInterval current = (EditableInterval) this.getItem(position);

    final EditText nameView = inflated.findViewById(R.id.IntervalNameEdit);
    nameView.setText(current.name());
    nameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(final View view, final boolean hasFocus) {
        if (!hasFocus) {
            EditText editor = (EditText) view;
            current.changeName(editor.getText().toString());
        }
      }
    });

    TextView lengthView = inflated.findViewById(R.id.IntervalLengthEdit);
    lengthView.setText(String.format(Locale.US,
        EditIntervalAdapter.SECONDS_FORMAT, current.length().get(TimeUnit.SECONDS)));
    lengthView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(final View view, final boolean hasFocus) {
        if (!hasFocus) {
            EditText editor = (EditText) view;
            current.changeDuration(
                Duration.fromSeconds(Long.parseLong(editor.getText().toString())));
        }
      }
    });

    ImageButton upButton = inflated.findViewById(R.id.UpIntervalButton);
    upButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            EditIntervalAdapter.this.exercise.moveInterval(position, position - 1);
        }
    });
    upButton.setEnabled(position != 0); // Can move up unless its the first item

    ImageButton downButton = inflated.findViewById(R.id.DownIntervalButton);
    downButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            EditIntervalAdapter.this.exercise.moveInterval(position, position + 1);
        }
    });
    downButton.setEnabled(position != (this.getCount() - 1));

    ImageButton deleteButton = inflated.findViewById(R.id.DeleteIntervalButton);
    deleteButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            EditIntervalAdapter.this.exercise.removeInterval(position);
        }
    });

    return inflated;
  }

  private void notifyIntervalAdded() {
    for (IntervalAddedConsumer consumer : this.intervalAddedConsumers) {
      consumer.intervalAddedToAdapter();
    }
  }

  public interface IntervalAddedConsumer {
    public void intervalAddedToAdapter();
  }
}