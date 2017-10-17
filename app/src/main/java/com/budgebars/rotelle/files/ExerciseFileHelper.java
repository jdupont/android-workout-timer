package com.budgebars.rotelle.files;

import com.budgebars.rotelle.workouts.Exercise;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jules on 10/17/2017.
 */

public class ExerciseFileHelper {

    public static void writeExerciseToFile(final Exercise exercise, final File file)
    {
        JSONObject json = ExerciseParser.jsonFromExercise(exercise);

        try {
            FileOutputStream outputStream = new FileOutputStream(file);

            outputStream.write(json.toString().getBytes());

            outputStream.close();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
