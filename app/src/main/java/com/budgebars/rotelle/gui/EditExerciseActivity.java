package com.budgebars.rotelle.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        this.setTitle("Editing an Exercise");

        this.editableExercise = new EditableExercise((Exercise) this.getIntent().getSerializableExtra(ExerciseListingActivity.EXERCISE_TO_RUN));

        EditText titleEditor = (EditText) this.findViewById(R.id.EditTitleView);
        titleEditor.setText(this.editableExercise.name());
        titleEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                {
                    EditText titleText = (EditText) view;
                    String updated = titleText.getText().toString();
                    EditExerciseActivity.this.editableExercise.changeName(updated);
                }
            }
        });

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

        Button saveButton = (Button) this.findViewById(R.id.SaveExerciseButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new UnsupportedOperationException("Not yet implemented");
            }
        });
    }
}
