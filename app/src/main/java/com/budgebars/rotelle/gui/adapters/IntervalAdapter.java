package com.budgebars.rotelle.gui.adapters;

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

public class IntervalAdapter extends BaseAdapter {
  private final Exercise exercise;

  private final Activity activity;

  public IntervalAdapter(final Exercise exercise, final Activity parent) {
    super();

    this.exercise = exercise;
    this.activity = parent;
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
    if (convertView == null) {
      LayoutInflater inflater = this.activity.getLayoutInflater();
      inflated = inflater.inflate(R.layout.item_interval_list, parent, false);
    }

    Interval current = (Interval) this.getItem(position);

    TextView nameView = inflated.findViewById(R.id.IntervalListName);
    nameView.setText(current.getName());

    TextView lengthView = inflated.findViewById(R.id.IntervalListLength);
    lengthView.setText(current.getLength().toString());

    return inflated;
  }
}
