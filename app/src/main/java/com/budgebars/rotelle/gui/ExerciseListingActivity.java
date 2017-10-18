package com.budgebars.rotelle.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.files.ExerciseFile;
import com.budgebars.rotelle.files.InternalFileManager;
import com.budgebars.rotelle.gui.adapters.FileAdapter;
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

        ListView listing = (ListView) this.findViewById(R.id.FileListView);
        listing.setAdapter(new FileAdapter(exercises, this));
        listing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ExerciseFile exerciseFile = (ExerciseFile) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(ExerciseListingActivity.this, ExerciseActivity.class);
                intent.putExtra(ExerciseListingActivity.EXERCISE_TO_RUN, exerciseFile.getExercise());
                startActivity(intent);
            }
        });

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
        throw new UnsupportedOperationException("Have not implemented create yet.");
    }
}
