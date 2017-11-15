package com.budgebars.rotelle.gui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.files.ExerciseFile;
import com.budgebars.rotelle.gui.adapters.IntervalAdapter;
import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.editable.EditableExercise;

import java.io.File;

public class ExerciseActivity extends AppCompatActivity {

    private ExerciseFile exerciseFile;

    private Exercise exercise;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_exercise);

        this.exerciseFile = (ExerciseFile) this.getIntent().getSerializableExtra(ExerciseListingActivity.EXERCISE_FILE);
        this.exercise = this.exerciseFile.getExercise();

        this.setTitle(this.exercise.name()+ " (" + this.exercise.totalLength() + ')');

        ListView list = this.findViewById(R.id.ExerciseDisplayView);
        list.setAdapter(new IntervalAdapter(this.exercise, this));

        Button startExerciseButton = this.findViewById(R.id.StartExerciseButton);
        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(ExerciseActivity.this, RunningTimerActivity.class);
                intent.putExtra(ExerciseListingActivity.EXERCISE_TO_RUN, ExerciseActivity.this.exercise);
				com.budgebars.rotelle.gui.ExerciseActivity.this.startActivity(intent);
            }
        });

        Button editExerciseButton = this.findViewById(R.id.EditExerciseButton);
        editExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(ExerciseActivity.this, EditExerciseActivity.class);
                intent.putExtra(ExerciseListingActivity.EDITABLE_EXERCISE, new EditableExercise(ExerciseActivity.this.exercise));
				com.budgebars.rotelle.gui.ExerciseActivity.this.startActivity(intent);
            }
        });

        Button emailExerciseButton = this.findViewById(R.id.EmailExerciseButton);
        emailExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ExerciseActivity.this.startEmailIntent();
            }
        });
    }

    private void startEmailIntent()
    {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, this.exercise.name());

        File requestFile = this.exerciseFile.source();
        Uri fileUri;
        try {
            fileUri = FileProvider.getUriForFile(this, "com.budgebars.rotelle.fileprovider", requestFile);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri); //, this.getContentResolver().getType(fileUri));

        String title = "Share exercise file:";
        Intent chooser = Intent.createChooser(emailIntent, title);

        if (emailIntent.resolveActivity(this.getPackageManager()) != null) {
            this.startActivity(chooser);
        }
        else
        {
            Toast.makeText(this, "No sharing app found.", Toast.LENGTH_SHORT).show();
        }
    }
}
