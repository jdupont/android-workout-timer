package com.budgebars.rotelle.files;

import android.content.Context;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.workouts.Duration;
import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.Interval;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jules on 10/13/2017.
 */
public final class ExerciseParser {

    private static final String EXERCISE_TAG = "exercise";

    private static final String EXERCISE_NAME_TAG = "name";

    private static final String EXERCISE_VERSION_TAG = "version";

    private static final String EXERCISE_TYPE_TAG = "type";

    private static final String INTERVAL_NAME_TAG = "name";

    private static final String INTERVAL_LENGTH_TAG = "length";

    private static final String INTERVALS_ARRAY_TAG = "intervals";

    private static final String TYPE = "CustomInterval";

    private static final String VERSION = "0.1";

    private ExerciseParser()
    {
        // Prevent instantiation of utility class.
    }

    public static JSONObject jsonFromExercise(final Exercise exercise)
    {
        try {
            JSONObject json = new JSONObject();

            json.put(ExerciseParser.EXERCISE_NAME_TAG, exercise.name());
            json.put(ExerciseParser.EXERCISE_VERSION_TAG, ExerciseParser.TYPE);
            json.put(ExerciseParser.EXERCISE_TYPE_TAG, ExerciseParser.VERSION);

            JSONArray array = new JSONArray();

            for (int i = 0; i < exercise.numberOfIntervals(); ++i) {
                JSONObject interval = new JSONObject();
                interval.put(ExerciseParser.INTERVAL_NAME_TAG, exercise.getIntervalAt(i).getName());
                interval.put(ExerciseParser.INTERVAL_LENGTH_TAG, exercise.getIntervalAt(i).getLength().get(TimeUnit.SECONDS));
                array.put(interval);
            }

            json.put(ExerciseParser.INTERVALS_ARRAY_TAG, array);

            JSONObject overall = new JSONObject();
            overall.put(ExerciseParser.EXERCISE_TAG, json);

            return overall;
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Exercise readSampleExercise(final Context context)
    {
        InputStream stream = ExerciseParser.getInputStreamForSample(context);
        return ExerciseParser.readExerciseFromStream(stream);
    }

    public static Exercise readExerciseFromFile(final File file)
    {
        try {
            FileInputStream stream = new FileInputStream(file);
            return ExerciseParser.readExerciseFromStream(stream);
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Exercise readExerciseFromStream(final InputStream stream)
    {
        try
        {
            String asString = ExerciseParser.streamToString(stream);
            Exercise exercise = ExerciseParser.parse(asString);
            return exercise;
        }
        finally
        {
            try
            {
                stream.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private static InputStream getInputStreamForSample(final Context context)
    {
        return context.getResources().openRawResource(R.raw.sample_exercise);
    }

    public static Exercise parse(final String jsonAsString)
    {
        try
        {
            JSONObject json = new JSONObject(jsonAsString).getJSONObject(ExerciseParser.EXERCISE_TAG);

            String exerciseName = json.getString(ExerciseParser.EXERCISE_NAME_TAG);
            List<Interval> intervals = ExerciseParser.readIntervals(json.getJSONArray(ExerciseParser.INTERVALS_ARRAY_TAG));

            return new Exercise(exerciseName, intervals);
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Interval> readIntervals(final JSONArray array)
    {
        List<Interval> intervals = new ArrayList<>();

        for (int i=0; i < array.length(); i++)
        {
            try
            {
                JSONObject oneObject = array.getJSONObject(i);

                String name = oneObject.getString(ExerciseParser.INTERVAL_NAME_TAG);
                int length = oneObject.getInt(ExerciseParser.INTERVAL_LENGTH_TAG);

                intervals.add(new Interval(name, Duration.fromSeconds(length)));
            }
            catch (JSONException e)
            {
                throw new RuntimeException(e);
            }
        }

        return intervals;
    }

    private static String streamToString(final InputStream stream)
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8))
        {
            StringBuilder sb = new StringBuilder();

            try {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(java.io.File.pathSeparator).append('n');
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return sb.toString();
        }
        catch (java.io.UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
