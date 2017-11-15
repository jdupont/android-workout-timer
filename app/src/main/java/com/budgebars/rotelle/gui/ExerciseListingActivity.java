package com.budgebars.rotelle.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.files.ExerciseFile;
import com.budgebars.rotelle.files.InternalFileManager;
import com.budgebars.rotelle.gui.adapters.FileAdapter;
import com.budgebars.rotelle.workouts.editable.EditableExercise;

import java.io.File;
import java.util.List;

public class ExerciseListingActivity extends AppCompatActivity {

    public static final String TAG = ExerciseListingActivity.class.getName();

    public static final String EXERCISE_FILE = "EXERCISE_FILE";

    public static final String EDITABLE_EXERCISE = "EDITABLE_EXTRA";

    public static final String EXERCISE_TO_RUN = "EXERCISE_TO_RUN";

    private FileAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_exercise_listing);

        InternalFileManager files = new InternalFileManager(this);
        if (!files.hasExercisesDirectory())
		{
			files.createExercisesDirectory();
		}

		if (!files.hasExercises())
		{
			Log.e(ExerciseListingActivity.TAG, "Creating sample exercise.");
			files.addSampleExerciseFile(this);
		}

        File[] exerciseFiles = files.getExerciseFiles();
        final List<ExerciseFile> exercises = ExerciseFile.fromFiles(exerciseFiles);

		Log.e(ExerciseListingActivity.TAG, "About to display exercises: " + exerciseFiles.length);

        ListView listing = (ListView) this.findViewById(R.id.FileListView);
        this.adapter = new FileAdapter(exercises, this);
        listing.setAdapter(this.adapter);

        listing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                Log.e(ExerciseListingActivity.TAG, "activated");

                ExerciseFile exerciseFile = (ExerciseFile) parent.getItemAtPosition(position);

                Intent intent = new Intent(ExerciseListingActivity.this, ExerciseActivity.class);
                intent.putExtra(ExerciseListingActivity.EXERCISE_FILE, exerciseFile);
                com.budgebars.rotelle.gui.ExerciseListingActivity.this.startActivity(intent);
            }
        });

        Button createButton = (Button) this.findViewById(R.id.CreateExerciseButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                ExerciseListingActivity.this.createExercise();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.populateExerciseListing();
    }

    private void populateExerciseListing()
    {
        InternalFileManager files = new InternalFileManager(this);

        File[] exerciseFiles = files.getExerciseFiles();
        final List<ExerciseFile> exercises = ExerciseFile.fromFiles(exerciseFiles);

        this.adapter.updateFileList(exercises);
    }

    private void createExercise()
    {
        EditableExercise blank = EditableExercise.blankEditableExercise();

        Intent intent = new Intent(ExerciseListingActivity.this, EditExerciseActivity.class);
        intent.putExtra(ExerciseListingActivity.EDITABLE_EXERCISE, blank);
        this.startActivity(intent);
    }
}
