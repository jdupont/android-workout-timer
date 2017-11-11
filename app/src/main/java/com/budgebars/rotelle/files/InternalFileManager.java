package com.budgebars.rotelle.files;

import android.app.Activity;
import android.content.Context;

import com.budgebars.rotelle.workouts.Exercise;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by Jules on 10/17/2017.
 */

public class InternalFileManager {

    private static final String EXERCISE_FILE_DIRECTORY = "/exercises/";

    private final File exercisesDirectory;

    public InternalFileManager(final Activity context)
    {
        this.exercisesDirectory = new File(context.getFilesDir(), InternalFileManager.EXERCISE_FILE_DIRECTORY);;
    }

    /**
     * Creates the exercises directory inside the directory that was passed to the constructor of this class
     */
    public void createExercisesDirectory()
    {
        if (this.hasExercisesDirectory())
        {
            throw new IllegalStateException("Exercises directory already exists");
        }
        else
        {
            if (!this.exercisesDirectory.mkdir())
            {
                throw new IllegalStateException("Failed to create directory");
            }
        }
    }

    public void addSampleExerciseFile(final Context context)
    {
        if (!this.hasExercisesDirectory())
        {
            throw new IllegalStateException("No exercises directory to place sample file into.");
        }

        Exercise sample = ExerciseParser.readSampleExercise(context);
        InternalFileManager.writeExerciseToFile(sample, this.getFileForExerciseName("sample"), false);
    }

    public boolean hasExercisesDirectory()
    {
        return this.exercisesDirectory.exists();
    }

    public boolean hasFileForExerciseName(final String exerciseName)
    {
        File file = this.getFileForExerciseName(exerciseName);

        return file.exists();
    }

    public File[] getExerciseFiles()
    {
        if (!this.hasExercisesDirectory())
        {
            throw new IllegalArgumentException("No exercises directory so cannot get exercise files.");
        }

        File[] files = this.exercisesDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return ExerciseFile.checkExerciseFileExtension(name);
            }
        });

        return files;
    }

    public void writeExerciseToFile(final Exercise exercise, boolean overwrite)
    {
        File file = this.getFileForExerciseName(exercise.name());
        InternalFileManager.writeExerciseToFile(exercise, file, overwrite);
    }

    private File getFileForExerciseName(final String exerciseName)
    {
        if (exerciseName.trim().isEmpty())
        {
            throw new IllegalArgumentException("Cannot have a blank exercise name");
        }

        return new File(this.exercisesDirectory,
                exerciseName + ExerciseFile.EXERCISE_FILE_EXTENSION);
    }

    private static void writeExerciseToFile(final Exercise exercise, final File file, boolean overwriteIfExists)
    {
        if (file.exists() && !overwriteIfExists)
        {
            throw new IllegalStateException("File already exists but overwrite is not set.");
        }

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
