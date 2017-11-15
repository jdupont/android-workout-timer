package com.budgebars.rotelle.files;

import com.budgebars.rotelle.workouts.Exercise;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jules on 10/17/2017.
 */

public final class ExerciseFile implements Serializable {

    public static final String EXERCISE_FILE_EXTENSION = ".exercise";
    private static final long serialVersionUID = 1688328007807640738L;

    private final File source;

    private final Exercise exercise;

    private ExerciseFile(final File file)
    {
        this.source = file;

        this.exercise = this.source.exists() ? this.getExercise(this.source) : null;
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

    public void delete()
    {
        if (!this.source.exists())
        {
            throw new IllegalArgumentException("Cannot delete a nonexistent file.");
        }

        if (!this.source.delete())
        {
            throw new IllegalStateException("File was not successfully deleted.");
        }
    }

    public File source()
    {
        return this.source;
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
        return file.toLowerCase(Locale.US).endsWith(ExerciseFile.EXERCISE_FILE_EXTENSION);
    }
}
