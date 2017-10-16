package com.budgebars.rotelle.gui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.Interval;

/**
 * Created by Jules on 10/16/2017.
 */

public class EditIntervalAdapter  extends BaseAdapter {

    private final Exercise exercise;

    private final Activity parent;

    public EditIntervalAdapter(final Exercise exercise, final Activity parent)
    {
        super();

        this.exercise = exercise;
        this.parent = parent;
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
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            LayoutInflater inflater = this.parent.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_interval_list_edit, container, false);
        }

        Interval current = (Interval) this.getItem(position);

        TextView nameView = convertView.findViewById(R.id.IntervalNameEdit);
        nameView.setText(current.getName());

        TextView lengthView = convertView.findViewById(R.id.IntervalLengthEdit);
        lengthView.setText(current.getLength().toString());

        return convertView;
    }

}