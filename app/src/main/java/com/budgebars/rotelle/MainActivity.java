package com.budgebars.rotelle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.budgebars.rotelle.workouts.Exercise;

public class MainActivity extends AppCompatActivity {

    public static final String EXERCISE_TO_RUN = "EXERCISE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button allPurpose = (Button) this.findViewById(R.id.AllPurposeButton);
        allPurpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.runAllPurposeButton();
            }
        });
    }

    private void runAllPurposeButton()
    {
        Intent intent = new Intent(this, RunningTimerActivity.class);
        intent.putExtra(MainActivity.EXERCISE_TO_RUN, Exercise.createMockExercise());
        startActivity(intent);

    }
}
