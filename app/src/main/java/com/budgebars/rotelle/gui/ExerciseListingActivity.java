package com.budgebars.rotelle.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.files.ExerciseFile;
import com.budgebars.rotelle.files.InternalFileManager;
import com.budgebars.rotelle.workouts.Exercise;

import java.io.File;
import java.util.List;

public class ExerciseListingActivity extends AppCompatActivity {

    public static final String EXERCISE_TO_RUN = "EXERCISE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_listing);

        InternalFileManager files = new InternalFileManager(this.getFilesDir());
        if (!files.hasExercisesDirectory())
        {
            files.createExercisesDirectory();
            files.addSampleExerciseFile(this);
        }

        File[] exerciseFiles = files.getExerciseFiles();
        final List<ExerciseFile> exercises = ExerciseFile.fromFiles(exerciseFiles);

        TextView listing = (TextView) this.findViewById(R.id.ListingTextView);

        // TODO -- ACTUAL DISPLAY OF FILES
        listing.setText("Number of exercises found: " + exercises.size());

        Button allPurpose = (Button) this.findViewById(R.id.AllPurposeButton);
        allPurpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExerciseListingActivity.this.runAllPurposeButton(exercises.get(0).getExercise());
            }
        });
    }

    private void runAllPurposeButton(final Exercise exercise)
    {
        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtra(ExerciseListingActivity.EXERCISE_TO_RUN, exercise);
        startActivity(intent);
    }
}
