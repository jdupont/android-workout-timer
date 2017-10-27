package com.budgebars.rotelle.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.files.ExerciseFile;
import com.budgebars.rotelle.gui.adapters.IntervalAdapter;
import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.editable.EditableExercise;

public class ExerciseActivity extends AppCompatActivity {

    private ExerciseFile exerciseFile;

    private Exercise exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        this.exerciseFile = (ExerciseFile) this.getIntent().getSerializableExtra(ExerciseListingActivity.EXERCISE_FILE);
        this.exercise = this.exerciseFile.getExercise();

        this.setTitle(this.exercise.name()+ " (" + this.exercise.totalLength() + ")");

        ListView list = (ListView) findViewById(R.id.ExerciseDisplayView);
        list.setAdapter(new IntervalAdapter(this.exercise, this));

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
                Intent intent = new Intent(ExerciseActivity.this, EditExerciseActivity.class);
                intent.putExtra(ExerciseListingActivity.EDITABLE_EXERCISE, new EditableExercise(ExerciseActivity.this.exercise));
                startActivity(intent);
            }
        });

        Button emailExerciseButton = (Button) this.findViewById(R.id.EmailExerciseButton);
        emailExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExerciseActivity.this.startEmailIntent();
            }
        });
    }

    private void startEmailIntent()
    {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, this.exercise.name());

        // TODO -- ATTACHING EXERCISE FILE DOES NOT WORK -- FIX THIS.
        emailIntent.putExtra(Intent.EXTRA_STREAM, this.exerciseFile.uri());

        String title = "Email exercise file:";
        Intent chooser = Intent.createChooser(emailIntent, title);

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            this.startActivity(chooser);
        }
        else
        {
            Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT);
        }
    }
}
