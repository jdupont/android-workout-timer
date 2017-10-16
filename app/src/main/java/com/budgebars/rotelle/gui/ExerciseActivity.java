package com.budgebars.rotelle.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.workouts.Exercise;

public class ExerciseActivity extends AppCompatActivity {

    private Exercise exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        this.exercise = (Exercise) this.getIntent().getSerializableExtra(ExerciseListingActivity.EXERCISE_TO_RUN);

        this.setTitle(this.exercise.name()+ " (" + this.exercise.totalLength() + ")");

        Button startExerciseButton = (Button) this.findViewById(R.id.StartExerciseButton);
        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExerciseActivity.this, RunningTimerActivity.class);
                intent.putExtra(ExerciseListingActivity.EXERCISE_TO_RUN, ExerciseActivity.this.exercise);
                startActivity(intent);
            }
        });

        Button editExerciseButton = (Button) this.findViewById(R.id.EditExerciseButton);
        editExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        });
    }
}
