package com.budgebars.rotelle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.ExerciseCoach;
import com.budgebars.rotelle.workouts.ExerciseTimer;

public class RunningTimerActivity extends AppCompatActivity {

    private Exercise exercise;

    private ExerciseCoach coach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_timer_activty);

        this.exercise = (Exercise) this.getIntent().getSerializableExtra(MainActivity.EXERCISE_TO_RUN);
        this.coach = new ExerciseCoach(this.exercise);
        this.coach.addTimerUpdateConsumer(new ExerciseTimer.TimerUpdateConsumer() {
            @Override
            public void timerUpdate(long remainingTime) {
                RunningTimerActivity.this.updateTextTimerTo(remainingTime);
            }
        });
        this.coach.addChangedConsumer(new ExerciseCoach.ExerciseChangedConsumer() {
            @Override
            public void exerciseChanged(String exerciseName, int exerciseLength) {
                RunningTimerActivity.this.exerciseChangedUpdate(exerciseName, exerciseLength);
            }
        });
        this.coach.addDoneConsumer(new ExerciseCoach.ExerciseDoneConsumer() {
            @Override
            public void exerciseDone() {
                RunningTimerActivity.this.exerciseDone();
            }
        });

        this.exerciseChangedUpdate(this.coach.currentIntervalName(), this.coach.currentIntervalLength());

        Button startButton = (Button) this.findViewById(R.id.StartTimerButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunningTimerActivity.this.coach.start();
            }
        });

        Button stopButton = (Button) this.findViewById(R.id.StopTimerButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunningTimerActivity.this.coach.stop();
            }
        });
    }

    private void updateTextTimerTo(long remaining)
    {
        TextView secondsText = (TextView) this.findViewById(R.id.TimerDisplay);
        secondsText.setText(Long.toString(remaining));
    }

    private void exerciseChangedUpdate(final String exerciseName, final int totalLength)
    {
        TextView secondsText = (TextView) this.findViewById(R.id.TimerDisplay);
        secondsText.setText(Long.toString(totalLength));

        TextView exerciseNameText = (TextView) this.findViewById(R.id.CurrentIntervalName);
        exerciseNameText.setText(exerciseName);
    }

    private void exerciseDone()
    {
        TextView secondsText = (TextView) this.findViewById(R.id.TimerDisplay);
        secondsText.setText(Long.toString(0));

        TextView exerciseNameText = (TextView) this.findViewById(R.id.CurrentIntervalName);
        exerciseNameText.setText("Done.");
    }
}
