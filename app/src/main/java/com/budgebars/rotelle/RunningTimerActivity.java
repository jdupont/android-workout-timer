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

    private ExerciseTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_timer_activty);

        this.exercise = (Exercise) this.getIntent().getSerializableExtra(MainActivity.EXERCISE_TO_RUN);
        this.coach = new ExerciseCoach(this.exercise);

        Button startButton = (Button) this.findViewById(R.id.StartTimerButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunningTimerActivity.this.startTimer();
            }
        });

        Button stopButton = (Button) this.findViewById(R.id.StopTimerButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunningTimerActivity.this.timer.stopTimer();
            }
        });
    }

    private void startTimer()
    {
        this.timer = this.getTimerFromCurrentInterval();
        this.timer.startTimer();
    }

    private void moveToNextInterval()
    {
        this.timer = this.getTimerFromCurrentInterval();
        this.timer.startTimer();
    }

    private void updateTextTimerTo(long remaining)
    {
        TextView secondsText = (TextView) this.findViewById(R.id.TimerDisplay);
        secondsText.setText(Long.toString(remaining));
    }

    private ExerciseTimer getTimerFromCurrentInterval()
    {
        return this.coach.getTimerForCurrentInterval(new ExerciseTimer.TimerUpdateConsumer() {
            @Override
            public void timerUpdate(long remaining) {
                RunningTimerActivity.this.updateTextTimerTo(remaining);
            }
        }, new ExerciseTimer.ExerciseFinishedConsumer() {
            @Override
            public void exerciseFinished() {
                RunningTimerActivity.this.moveToNextInterval();
            }
        });
    }
}
