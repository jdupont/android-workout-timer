package com.budgebars.rotelle.workouts;

import android.os.CountDownTimer;

import com.budgebars.rotelle.workouts.consumers.IntervalFinishedConsumer;
import com.budgebars.rotelle.workouts.consumers.IntervalStartedConsumer;
import com.budgebars.rotelle.workouts.consumers.TimerUpdateConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by Jules on 10/9/2017.
 */

public class IntervalTimer {

  private static final Duration NOTIFICATION_TICKS_MILLIS = Duration.fromMilliSeconds(250);

  private final CountDownTimer timer;

  private final List<TimerUpdateConsumer> onTickMethods;

  private final List<IntervalFinishedConsumer> finishedMethods;

  private final List<IntervalStartedConsumer> startedMethods;

  private boolean isRunning = false;

  public IntervalTimer(final Interval interval) {
      this(interval.getLength());
  }

  public IntervalTimer(final Duration intervalLength) {
    this.timer = new CountDownTimer(intervalLength.get(TimeUnit.MILLISECONDS),
      IntervalTimer.NOTIFICATION_TICKS_MILLIS.get(TimeUnit.MILLISECONDS)) {
      @Override
      public void onTick(final long millisUntilFinished) {
        for (TimerUpdateConsumer consumer : IntervalTimer.this.onTickMethods) {
          consumer.timerUpdate(Duration.fromMilliSeconds(millisUntilFinished));
        }
      }

      @Override
      public void onFinish() {
        for (IntervalFinishedConsumer consumer : IntervalTimer.this.finishedMethods) {
          consumer.intervalFinished();
        }
      }
    };

    this.onTickMethods = new ArrayList<>();
    this.finishedMethods = new ArrayList<>();
    this.startedMethods = new ArrayList<>();

    this.finishedMethods.add(new IntervalFinishedConsumer() {
        @Override
        public void intervalFinished() {
            IntervalTimer.this.isRunning = false;
        }
    });
  }

  public void addUpdateConsumer(final TimerUpdateConsumer consumer) {
    this.onTickMethods.add(consumer);
  }

  public void addUpdateConsumer(final List<TimerUpdateConsumer> consumers) {
    this.onTickMethods.addAll(consumers);
  }

  public void addStartedConsumer(final IntervalStartedConsumer consumer) {
    this.startedMethods.add(consumer);
  }

  public void addStartedConsumer(final List<IntervalStartedConsumer> consumers) {
    this.startedMethods.addAll(consumers);
  }

  public void addFinishedConsumer(final IntervalFinishedConsumer consumer) {
    this.finishedMethods.add(consumer);
  }

  public void addFinishedConsumer(final List<IntervalFinishedConsumer> consumers) {
    this.finishedMethods.addAll(consumers);
  }

  public void startTimer() {
    if (this.isRunning) {
      throw new IllegalStateException("Timer is already running");
    }

    this.timer.start();
    this.isRunning = true;

    for (IntervalStartedConsumer consumer : this.startedMethods) {
      consumer.intervalStarted();
    }
  }

  public void stopTimer() {
    if (!this.isRunning) {
      throw new IllegalStateException("Timer is not running");
    }

    this.timer.cancel();
    this.isRunning = false;
  }
}
