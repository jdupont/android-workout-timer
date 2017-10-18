package com.budgebars.rotelle.files;

import com.budgebars.rotelle.workouts.Exercise;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 10/17/2017.
 */

public class ExerciseFile {

    public static final String EXERCISE_FILE_EXTENSION = ".exercise";

    private final File source;

    private Exercise exercise;

    public ExerciseFile(final File exerciseFile)
    {
        this.source = exerciseFile;

        if (this.source.exists())
        {
            this.exercise = getExercise(this.source);
        }
        else
        {
            this.exercise = null;
        }
    }

    private Exercise getExercise(final File file) {

        return ExerciseParser.readExerciseFromFile(file);
    }

    public String name()
    {
        return this.exercise.name();
    }

    public Exercise getExercise()
    {
        return this.exercise;
    }

    public static List<ExerciseFile> fromFiles(final File[] files)
    {
        List<ExerciseFile> parsed = new ArrayList<>();

        for (File file : files)
        {
            parsed.add(new ExerciseFile(file));
        }

        return parsed;
    }

    public static boolean checkExerciseFileExtension(final File file)
    {
        return ExerciseFile.checkExerciseFileExtension(file.getName());
    }

    public static boolean checkExerciseFileExtension(final String file)
    {
        return file.toLowerCase().endsWith(ExerciseFile.EXERCISE_FILE_EXTENSION);
    }
}
