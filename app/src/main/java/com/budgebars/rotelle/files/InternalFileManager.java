package com.budgebars.rotelle.files;

import android.content.Context;

import com.budgebars.rotelle.workouts.Exercise;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Jules on 10/17/2017.
 */

public class InternalFileManager {

    private static final String EXERCISE_FILE_DIRECTORY = "/exercises/";

    private final File exercisesDirectory;

    public InternalFileManager(final File internalStorageDirectory)
    {
        this.exercisesDirectory = new File(internalStorageDirectory, InternalFileManager.EXERCISE_FILE_DIRECTORY);;
    }

    /**
     * Creates the exercises directory inside the directory that was passed to the consutrctor of this class
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
        Exercise sample = ExerciseParser.readSampleExercise(context);
        ExerciseFileHelper.writeExerciseToFile(sample, new File(this.exercisesDirectory,
                "sample" + ExerciseFile.EXERCISE_FILE_EXTENSION));
    }

    public boolean hasExercisesDirectory()
    {
        return this.exercisesDirectory.exists();
    }

    public File[] getExerciseFiles()
    {
        if (!this.hasExercisesDirectory())
        {
            throw new IllegalArgumentException("No exercises directory so cannot get exercise files.");
        }

        File[] files = this.exercisesDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return ExerciseFile.checkExerciseFileExtension(s);
            }
        });

        return files;
    }
}
