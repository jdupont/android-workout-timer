package com.budgebars.rotelle.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.gui.adapters.EditIntervalAdapter;
import com.budgebars.rotelle.workouts.Exercise;

public class EditExerciseActivity extends AppCompatActivity {

    private Exercise exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        this.exercise = (Exercise) this.getIntent().getSerializableExtra(ExerciseListingActivity.EXERCISE_TO_RUN);

        this.setTitle(this.exercise.name()+ " (" + this.exercise.totalLength() + ")");

        ListView list = (ListView) findViewById(R.id.EditDisplayView);
        list.setAdapter(new EditIntervalAdapter(this.exercise, this));
    }
}
