package com.budgebars.rotelle.gui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.workouts.consumers.ExerciseEditedConsumer;
import com.budgebars.rotelle.workouts.editable.EditableExercise;
import com.budgebars.rotelle.workouts.editable.EditableInterval;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 10/16/2017.
 */

public class EditIntervalAdapter  extends BaseAdapter {

    private final EditableExercise exercise;

    private final Activity parent;

    private final List<IntervalAddedConsumer> intervalAddedConsumers = new ArrayList<>();

    public EditIntervalAdapter(final EditableExercise exercise, final Activity parent)
    {
        super();

        this.exercise = exercise;
        this.parent = parent;

        this.exercise.addExerciseEditedConsumer(new ExerciseEditedConsumer() {
            @Override
            public void exerciseEdited(final EditAction action) {

                if (action != EditAction.TITLE_EDITED)
                {
                    // Title is outside of the list view so only need to re-render the
                    // list view when something other than the title has changed
                    EditIntervalAdapter.this.notifyDataSetChanged();
                }

                if (action == EditAction.ADD_INTERVAL)
                {
                    EditIntervalAdapter.this.notifyIntervalAdded();
                }
            }
        });
    }

    public void addItemAddedConsumer(final IntervalAddedConsumer item)
    {
        this.intervalAddedConsumers.add(item);
    }

    @Override
    public int getCount() {
        return this.exercise.numberOfIntervals();
    }

    @Override
    public Object getItem(int i) {
        return this.exercise.getIntervalAt(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup container) {
        if (convertView == null) {
            LayoutInflater inflater = this.parent.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_interval_list_edit, container, false);
        }

        final EditableInterval current = (EditableInterval) this.getItem(position);

        final EditText nameView = convertView.findViewById(R.id.IntervalNameEdit);
        nameView.setText(current.name());
        nameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (!hasFocus) {
                    EditText editor = (EditText) view;
                    current.changeName(editor.getText().toString());
                }
            }
        });

        TextView lengthView = convertView.findViewById(R.id.IntervalLengthEdit);
        lengthView.setText(current.length().toString());

        return convertView;
    }

    private void notifyIntervalAdded()
    {
        for (IntervalAddedConsumer consumer : this.intervalAddedConsumers)
        {
            consumer.intervalAddedToAdapter();
        }
    }

    public interface IntervalAddedConsumer
    {
        public void intervalAddedToAdapter();
    }
}