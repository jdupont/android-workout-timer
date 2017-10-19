package com.budgebars.rotelle.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.gui.adapters.EditIntervalAdapter;
import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.editable.EditableExercise;

public class EditExerciseActivity extends AppCompatActivity {

    private EditableExercise editableExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        this.editableExercise = new EditableExercise((Exercise) this.getIntent().getSerializableExtra(ExerciseListingActivity.EXERCISE_TO_RUN));

        this.setTitle(this.editableExercise.name());

        final ListView list = (ListView) findViewById(R.id.EditDisplayView);
        list.setItemsCanFocus(true);

        final EditIntervalAdapter adapter = new EditIntervalAdapter(this.editableExercise, this);
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

        Button overwriteButton = (Button) this.findViewById(R.id.OverwriteExerciseButton);
        overwriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        });

        Button saveNewButton = (Button) this.findViewById(R.id.SaveNewExerciseButton);
        saveNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        });
    }
}
