package com.budgebars.rotelle.files;

import android.content.Context;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.workouts.Exercise;
import com.budgebars.rotelle.workouts.Interval;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 10/13/2017.
 */
public class ExerciseParser {

    private static final String NAMESPACE = null;

    private static final String EXERCISE_TAG = "exercise";

    private static final String INTERVAL_TAG = "interval";

    private static final String EXERCISE_NAME_TAG = "name";

    private static final String INTERVAL_NAME_TAG = "name";

    private static final String INTERVAL_LENGTH_TAG = "length";

    private static final String INTERVALS_ARRAY_TAG = "intervals";

    public Exercise readExerciseFromFile(final Context context)
    {
        InputStream stream = this.getXmlFileInputStream(context);

        try
        {
            Exercise exercise = this.parse(stream);
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

    private InputStream getXmlFileInputStream(final Context context)
    {
        return context.getResources().openRawResource(R.raw.sample_exercise);
    }

    private Exercise parse(final InputStream in)
    {
        try
        {
            JSONObject json = new JSONObject(this.streamToString(in)).getJSONObject(ExerciseParser.EXERCISE_TAG);

            String exerciseName = json.getString(ExerciseParser.EXERCISE_NAME_TAG);
            List<Interval> intervals = this.readIntervals(json.getJSONArray(ExerciseParser.INTERVALS_ARRAY_TAG));

            return new Exercise(exerciseName, intervals);
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Interval> readIntervals(final JSONArray array)
    {
        List<Interval> intervals = new ArrayList<>();

        for (int i=0; i < array.length(); i++)
        {
            try
            {
                JSONObject oneObject = array.getJSONObject(i);

                String name = oneObject.getString(ExerciseParser.INTERVAL_NAME_TAG);
                int length = oneObject.getInt(ExerciseParser.INTERVAL_LENGTH_TAG);

                intervals.add(new Interval(name, length));
            }
            catch (JSONException e)
            {
                throw new RuntimeException(e);
            }
        }

        return intervals;
    }

    private String streamToString(final InputStream stream)
    {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder();

        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }
}
