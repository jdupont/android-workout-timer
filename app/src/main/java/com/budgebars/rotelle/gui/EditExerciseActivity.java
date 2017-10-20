package com.budgebars.rotelle.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.files.InternalFileManager;
import com.budgebars.rotelle.gui.adapters.EditIntervalAdapter;
import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.editable.EditableExercise;

public class EditExerciseActivity extends AppCompatActivity {

    private EditableExercise editableExercise;

    private EditIntervalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        this.setTitle("Editing an Exercise");

        this.editableExercise = new EditableExercise((Exercise) this.getIntent().getSerializableExtra(ExerciseListingActivity.EXERCISE_TO_RUN));

        EditText titleEditor = (EditText) this.findViewById(R.id.EditTitleView);
        titleEditor.setText(this.editableExercise.name());
        titleEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                {
                    EditExerciseActivity.this.commitTitleChanges();
                }
            }
        });

        final ListView list = (ListView) findViewById(R.id.IntervalListView);
        list.setItemsCanFocus(true);

        this.adapter = new EditIntervalAdapter(this.editableExercise, this);
        list.setAdapter(adapter);

        adapter.addItemAddedConsumer(new EditIntervalAdapter.IntervalAddedConsumer() {
            @Override
            public void intervalAddedToAdapter() {
                list.setSelection(adapter.getCount() - 1);
            }
        });

        Button addIntervalButton = (Button) this.findViewById(R.id.AddIntervalButton);
        addIntervalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditExerciseActivity.this.editableExercise.addInterval();
            }
        });

        final Button saveButton = (Button) this.findViewById(R.id.SaveExerciseButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Make sure focus comes off of edit views so that all changes are committed
                //EditExerciseActivity.this.commitAllChanges();
                EditExerciseActivity.this.getCurrentFocus().clearFocus();
                saveButton.requestFocus();

                final Exercise saveTarget = EditExerciseActivity.this.editableExercise.toExercise();

                final InternalFileManager fileManager = new InternalFileManager(EditExerciseActivity.this);

                if (fileManager.hasFileForExerciseName(saveTarget.name()))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditExerciseActivity.this);
                    builder.setMessage("An exercise with that name already exists. Overwrite?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    fileManager.writeExerciseToFile(saveTarget, true);
                                    EditExerciseActivity.this.goBackToListingActivity();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Do nothing so user has a chance to change exercise name
                                }
                            });

                    builder.create().show();
                }
                else
                {
                    fileManager.writeExerciseToFile(saveTarget, false);
                    EditExerciseActivity.this.goBackToListingActivity();
                }
            }
        });
    }

    private void goBackToListingActivity()
    {
        Intent intent = new Intent(this, ExerciseListingActivity.class);
        startActivity(intent);
    }

    private void commitAllChanges()
    {
        this.commitTitleChanges();
    }

    private void commitTitleChanges()
    {
        EditText titleText = (EditText) this.findViewById(R.id.EditTitleView);
        String updated = titleText.getText().toString();
        EditExerciseActivity.this.editableExercise.changeName(updated);
    }
}
