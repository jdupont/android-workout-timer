package com.budgebars.rotelle.gui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.workouts.Duration;
import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.ExerciseCoach;
import com.budgebars.rotelle.workouts.consumers.ExerciseDoneConsumer;
import com.budgebars.rotelle.workouts.consumers.ExercisePausedConsumer;
import com.budgebars.rotelle.workouts.consumers.ExerciseResetConsumer;
import com.budgebars.rotelle.workouts.consumers.ExerciseResumedConsumer;
import com.budgebars.rotelle.workouts.consumers.ExerciseStartedConsumer;
import com.budgebars.rotelle.workouts.consumers.IntervalChangedConsumer;
import com.budgebars.rotelle.workouts.consumers.IntervalStartedConsumer;
import com.budgebars.rotelle.workouts.consumers.TimerUpdateConsumer;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RunningTimerActivity extends AppCompatActivity {

    private static final String SECONDS_FORMATTER = "%d";

    private ExerciseCoach coach;

    private MediaPlayer openingBell;

    private MediaPlayer closingBell;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_running_timer_activty);

        Exercise exercise = (Exercise) this.getIntent().getSerializableExtra(ExerciseListingActivity.EXERCISE_TO_RUN);
        this.coach = new ExerciseCoach(exercise);

        this.openingBell = MediaPlayer.create(this, R.raw.opening_bell_trimmed);
        this.closingBell = MediaPlayer.create(this, R.raw.closing_bell_trimmed);

        this.coach.addTimerUpdateConsumer(new TimerUpdateConsumer() {
            @Override
            public void timerUpdate(final Duration remainingTime) {
                RunningTimerActivity.this.updateTextTimerTo(remainingTime);
            }
        });
        this.coach.addIntervalChangedConsumer(new IntervalChangedConsumer() {
            @Override
            public void intervalChanged(final String intervalName, final Duration intervalLength) {
                RunningTimerActivity.this.intervalChangedUpdate(intervalName, intervalLength);
            }
        });
        this.coach.addExerciseDoneConsumer(new ExerciseDoneConsumer() {
            @Override
            public void exerciseDone() {
                RunningTimerActivity.this.exerciseDone();
            }
        });
        this.coach.addIntervalStartedConsumer(new IntervalStartedConsumer() {
            @Override
            public void intervalStarted() {
                RunningTimerActivity.this.intervalStarted();
            }
        });
        this.coach.addExerciseStartedConsumer(new ExerciseStartedConsumer() {
            @Override
            public void exerciseStarted() {
                RunningTimerActivity.this.setRunningConfiguration();
            }
        });
        this.coach.addExercisePausedConsumer(new ExercisePausedConsumer() {
            @Override
            public void exercisePaused() {
                RunningTimerActivity.this.setPausedConfiguration();
            }
        });
        this.coach.addExerciseResumedConsumer(new ExerciseResumedConsumer() {
            @Override
            public void exerciseResumed() {
                RunningTimerActivity.this.setRunningConfiguration();
            }
        });
        this.coach.addExerciseResetConsumer(new ExerciseResetConsumer() {
            @Override
            public void exerciseReset() {
                RunningTimerActivity.this.setReadyConfiguration();
                RunningTimerActivity.this.intervalChangedUpdate(RunningTimerActivity.this.coach.currentIntervalName(),
                        RunningTimerActivity.this.coach.currentIntervalLength());
            }
        });

        this.intervalChangedUpdate(this.coach.currentIntervalName(), this.coach.currentIntervalLength());

        Button resetButton = this.findViewById(R.id.ResetTimerButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                RunningTimerActivity.this.coach.reset();
            }
        });

        this.setReadyConfiguration();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        this.openingBell.release();
        this.openingBell = null;

        this.closingBell.release();
        this.closingBell = null;
    }

    private void updateTextTimerTo(final Duration remaining)
    {
        TextView secondsText = this.findViewById(R.id.TimerDisplay);
        secondsText.setText(String.format(Locale.US, RunningTimerActivity.SECONDS_FORMATTER, remaining.get(TimeUnit.SECONDS)));
    }

    private void intervalChangedUpdate(final String intervalName, final Duration totalLength)
    {
        TextView secondsText = this.findViewById(R.id.TimerDisplay);
        secondsText.setText(String.format(Locale.US, RunningTimerActivity.SECONDS_FORMATTER, totalLength.get(TimeUnit.SECONDS)));

        TextView exerciseNameText = this.findViewById(R.id.CurrentIntervalName);
        exerciseNameText.setText(intervalName);
    }

    private void exerciseDone()
    {
        TextView secondsText = this.findViewById(R.id.TimerDisplay);
        secondsText.setText(String.format(Locale.US, RunningTimerActivity.SECONDS_FORMATTER, 0));

        TextView exerciseNameText = this.findViewById(R.id.CurrentIntervalName);
        exerciseNameText.setText(R.string.timer_finished_label);

        this.setDoneConfiguration();

        this.closingBell.start();
    }

    private void intervalStarted()
    {
        this.openingBell.start();
    }

    private void setReadyConfiguration()
    {
        Button startPauseResumeButton = this.findViewById(R.id.StartPauseResumeButton);
        this.enableButton(startPauseResumeButton);
        startPauseResumeButton.setText(R.string.start_timer_label);
        startPauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                RunningTimerActivity.this.coach.start();
            }
        });

        this.disableButton((Button) this.findViewById(R.id.ResetTimerButton));
    }

    private void setRunningConfiguration()
    {
        Button startPauseResumeButton = this.findViewById(R.id.StartPauseResumeButton);
        this.enableButton(startPauseResumeButton);
        startPauseResumeButton.setText(R.string.pause_timer_label);
        startPauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                RunningTimerActivity.this.coach.pause();
            }
        });

        this.enableButton((Button) this.findViewById(R.id.ResetTimerButton));
    }

    private void setPausedConfiguration()
    {
        Button startPauseResumeButton = this.findViewById(R.id.StartPauseResumeButton);
        this.enableButton(startPauseResumeButton);
        startPauseResumeButton.setText(R.string.resume_timer_label);
        startPauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                RunningTimerActivity.this.coach.resume();
            }
        });

        this.enableButton((Button) this.findViewById(R.id.ResetTimerButton));
    }

    private void setDoneConfiguration()
    {
        this.disableButton((Button) this.findViewById(R.id.StartPauseResumeButton));

        this.enableButton((Button) this.findViewById(R.id.ResetTimerButton));
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
