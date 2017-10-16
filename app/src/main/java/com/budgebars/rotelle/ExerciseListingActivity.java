package com.budgebars.rotelle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.budgebars.rotelle.files.ExerciseParser;
import com.budgebars.rotelle.workouts.Exercise;

public class ExerciseListingActivity extends AppCompatActivity {

    public static final String EXERCISE_TO_RUN = "EXERCISE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_listing);

        Button allPurpose = (Button) this.findViewById(R.id.AllPurposeButton);
        allPurpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExerciseListingActivity.this.runAllPurposeButton();
            }
        });
    }

    private void runAllPurposeButton()
    {
        ExerciseParser parser = new ExerciseParser();
        Exercise exercise = parser.readExerciseFromFile(this);

        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtra(ExerciseListingActivity.EXERCISE_TO_RUN, exercise);
        startActivity(intent);
    }
}
