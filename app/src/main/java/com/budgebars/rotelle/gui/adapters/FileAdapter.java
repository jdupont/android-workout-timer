package com.budgebars.rotelle.gui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.files.ExerciseFile;

import java.util.List;

/**
 * Created by Jules on 10/18/2017.
 */

public class FileAdapter extends BaseAdapter {

    private final List<ExerciseFile> exercise;

    private final Activity parent;

    public FileAdapter(final List<ExerciseFile> exercise, final Activity parent)
    {
        super();

        this.exercise = exercise;
        this.parent = parent;
    }

    @Override
    public int getCount() {
        return this.exercise.size();
    }

    @Override
    public Object getItem(int i) {
        return this.exercise.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            LayoutInflater inflater = this.parent.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_file_list, container, false);
        }

        final ExerciseFile current = (ExerciseFile) this.getItem(position);

        TextView nameView = convertView.findViewById(R.id.ExerciseFileName);
        nameView.setText(current.name());

        ImageButton deleteButton = convertView.findViewById(R.id.DeleteExerciseFileButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current.delete();
                FileAdapter.this.exercise.remove(position);
                FileAdapter.this.notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void updateFileList(List<ExerciseFile> exerciseFiles)
    {
        this.exercise.clear();
        this.exercise.addAll(exerciseFiles);

        this.notifyDataSetChanged();
    }
}