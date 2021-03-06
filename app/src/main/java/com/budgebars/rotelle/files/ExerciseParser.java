package com.budgebars.rotelle.files;

import android.content.Context;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.workouts.Duration;
import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.Interval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Jules on 10/13/2017.
 */
public final class ExerciseParser {

  private static final String CHARSET = "UTF-8";

  @SuppressWarnings("HardcodedFileSeparator")
  private static final String DELIMITER = "\\A";

  private static final String EXERCISE_TAG = "exercise";

  private static final String EXERCISE_NAME_TAG = "name";

  private static final String EXERCISE_VERSION_TAG = "version";

  private static final String EXERCISE_TYPE_TAG = "type";

  private static final String INTERVAL_NAME_TAG = ExerciseParser.EXERCISE_NAME_TAG;

  private static final String INTERVAL_LENGTH_TAG = "length";

  private static final String INTERVALS_ARRAY_TAG = "intervals";

  private static final String TYPE = "CustomInterval";

  private static final String VERSION = "0.1";

  private ExerciseParser() {
    // Prevent instantiation of utility class.
  }

  /**
   * Creates a JSON object representing the provided exercise.
   * @param exercise The exercise to convert to json.
   * @return A JSON object containing all of the information in the provided exercise.
   */
  public static JSONObject jsonFromExercise(final Exercise exercise) {
    try {
      JSONObject json = new JSONObject();

      json.put(ExerciseParser.EXERCISE_NAME_TAG, exercise.name());
      json.put(ExerciseParser.EXERCISE_VERSION_TAG, ExerciseParser.TYPE);
      json.put(ExerciseParser.EXERCISE_TYPE_TAG, ExerciseParser.VERSION);

      JSONArray array = new JSONArray();

      for (int i = 0; i < exercise.numberOfIntervals(); ++i) {
        JSONObject interval = new JSONObject();
        interval.put(ExerciseParser.INTERVAL_NAME_TAG, exercise.getIntervalAt(i).getName());
        interval.put(ExerciseParser.INTERVAL_LENGTH_TAG,
            exercise.getIntervalAt(i).getLength().get(TimeUnit.SECONDS));
        array.put(interval);
      }

      json.put(ExerciseParser.INTERVALS_ARRAY_TAG, array);

      JSONObject overall = new JSONObject();
      overall.put(ExerciseParser.EXERCISE_TAG, json);

      return overall;
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  public static Exercise readSampleExercise(final Context context) {
    InputStream stream = ExerciseParser.getInputStreamForSample(context);
    return ExerciseParser.readExerciseFromStream(stream);
  }

  /**
   * Reads an exercise from an exercise file.
   * @param file A file containing a JSON exercise.
   * @return The exercise, parsed out of the file.
   */
  public static Exercise readExerciseFromFile(final File file) {
    try {
      FileInputStream stream = new FileInputStream(file);
      return ExerciseParser.readExerciseFromStream(stream);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private static Exercise readExerciseFromStream(final InputStream stream) {
    try {
      String asString = ExerciseParser.streamToString(stream);
      return ExerciseParser.parse(asString);
    } finally    {
      try {
        stream.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static InputStream getInputStreamForSample(final Context context) {
    return context.getResources().openRawResource(R.raw.sample_exercise);
  }

  /**
   * Parses an exercise from a string containing the json representation of that exercise.
   * @param jsonAsString The json representation of the exercise.
   * @return The parsed exercise.
   */
  public static Exercise parse(final String jsonAsString) {
    try {
      JSONObject json = new JSONObject(jsonAsString).getJSONObject(ExerciseParser.EXERCISE_TAG);

      String exerciseName = json.getString(ExerciseParser.EXERCISE_NAME_TAG);
      List<Interval> intervals = ExerciseParser.readIntervals(
          json.getJSONArray(ExerciseParser.INTERVALS_ARRAY_TAG));

      return new Exercise(exerciseName, intervals);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<Interval> readIntervals(final JSONArray array) {
    List<Interval> intervals = new ArrayList<>();

    for (int i = 0; i < array.length(); i++) {
      try {
        JSONObject oneObject = array.getJSONObject(i);

        String name = oneObject.getString(ExerciseParser.INTERVAL_NAME_TAG);
        int length = oneObject.getInt(ExerciseParser.INTERVAL_LENGTH_TAG);

        intervals.add(new Interval(name, Duration.fromSeconds(length)));
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
    }

    return intervals;
  }

  private static String streamToString(final InputStream stream) {
    try (Scanner scanner = new Scanner(stream, ExerciseParser.CHARSET)
        .useDelimiter(ExerciseParser.DELIMITER)) {
      return scanner.hasNext() ? scanner.next() : "";
    }
  }
}
