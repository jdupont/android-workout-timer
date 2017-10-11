package com.budgebars.rotelle;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.ExerciseCoach;
import com.budgebars.rotelle.workouts.IntervalTimer;

public class RunningTimerActivity extends AppCompatActivity {

    private Exercise exercise;

    private ExerciseCoach coach;

    private MediaPlayer openingBell;

    private MediaPlayer closingBell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_timer_activty);

        this.openingBell = MediaPlayer.create(this, R.raw.openingbell);
        this.closingBell = MediaPlayer.create(this, R.raw.closingbell);

        this.exercise = (Exercise) this.getIntent().getSerializableExtra(MainActivity.EXERCISE_TO_RUN);
        this.coach = new ExerciseCoach(this.exercise);
        this.coach.addTimerUpdateConsumer(new IntervalTimer.TimerUpdateConsumer() {
            @Override
            public void timerUpdate(long remainingTime) {
                RunningTimerActivity.this.updateTextTimerTo(remainingTime);
            }
        });
        this.coach.addChangedConsumer(new ExerciseCoach.IntervalChangedConsumer() {
            @Override
            public void intervalChanged(String intervalName, int intervalLength) {
                RunningTimerActivity.this.intervalChangedUpdate(intervalName, intervalLength);
            }
        });
        this.coach.addDoneConsumer(new ExerciseCoach.ExerciseDoneConsumer() {
            @Override
            public void exerciseDone() {
                RunningTimerActivity.this.exerciseDone();
            }
        });
        this.coach.addIntervalStartedConsumer(new IntervalTimer.IntervalStartedConsumer() {
            @Override
            public void intervalStarted() {
                RunningTimerActivity.this.intervalStarted();
            }
        });
        this.coach.addExerciseStartedConsumer(new ExerciseCoach.ExerciseStartedConsumer() {
            @Override
            public void exerciseStarted() {
                RunningTimerActivity.this.setRunningConfiguration();
            }
        });

        this.intervalChangedUpdate(this.coach.currentIntervalName(), this.coach.currentIntervalLength());

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

        Button resetButton = (Button) this.findViewById(R.id.ResetTimerButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunningTimerActivity.this.coach.reset();
            }
        });

        this.setNotRunningConfiguration();
    }

    private void updateTextTimerTo(long remaining)
    {
        TextView secondsText = (TextView) this.findViewById(R.id.TimerDisplay);
        secondsText.setText(Long.toString(remaining));
    }

    private void intervalChangedUpdate(final String intervalName, final int totalLength)
    {
        TextView secondsText = (TextView) this.findViewById(R.id.TimerDisplay);
        secondsText.setText(Long.toString(totalLength));

        TextView exerciseNameText = (TextView) this.findViewById(R.id.CurrentIntervalName);
        exerciseNameText.setText(intervalName);
    }

    private void exerciseDone()
    {
        TextView secondsText = (TextView) this.findViewById(R.id.TimerDisplay);
        secondsText.setText(Long.toString(0));

        TextView exerciseNameText = (TextView) this.findViewById(R.id.CurrentIntervalName);
        exerciseNameText.setText("Done.");

        this.setNotRunningConfiguration();

        this.closingBell.start();
    }

    private void intervalStarted()
    {
        this.openingBell.start();
    }

    private void setRunningConfiguration()
    {
        this.enableButton((Button) this.findViewById(R.id.StopTimerButton));
        this.disableButton((Button) this.findViewById(R.id.StartTimerButton));
    }

    private void setNotRunningConfiguration()
    {
        this.enableButton((Button) this.findViewById(R.id.StartTimerButton));
        this.disableButton((Button) this.findViewById(R.id.StopTimerButton));
    }

    private void enableButton(final Button button)
    {
        button.setEnabled(true);
    }

    private void disableButton(final Button button)
    {
        button.setEnabled(false);
    }
}
