package com.budgebars.rotelle.files;

import android.app.Activity;
import android.content.Context;

import com.budgebars.rotelle.workouts.Exercise;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import org.json.JSONObject;

/**
 * Created by Jules on 10/17/2017.
 */

public class InternalFileManager {
  private static final String CHARSET = "UTF-8";

  private static final String SAMPLE_FILE_NAME = "sample";

  @SuppressWarnings("StringConcatenationMissingWhitespace")
  private static final String EXERCISE_FILE_DIRECTORY =
      File.pathSeparatorChar + "exercises" + File.pathSeparatorChar;

  private final File exercisesDirectory;

  public InternalFileManager(final Activity context) {
    this.exercisesDirectory =
        new File(context.getFilesDir(), InternalFileManager.EXERCISE_FILE_DIRECTORY);
  }

  /**
   * Creates the exercises directory inside the directory that
   * was passed to the constructor of this class.
   */
  public void createExercisesDirectory() {
    if (this.hasExercisesDirectory()) {
      throw new IllegalStateException("Exercises directory already exists");
    } else {
      if (!this.exercisesDirectory.mkdir()) {
        throw new IllegalStateException("Failed to create directory");
      }
    }
  }

  /**
   * Adds the sample exercise file to the internal file location containing this user's
   * saved exercises. Mainly useful so that a new user can have an example exercise to start with.
   * @param context The context of the caller. Used to access the android file system.
   */
  public void addSampleExerciseFile(final Context context) {
    if (!this.hasExercisesDirectory()) {
      throw new IllegalStateException("No exercises directory to place sample file into.");
    }

    Exercise sample = ExerciseParser.readSampleExercise(context);
    InternalFileManager.writeExerciseToFile(sample,
        this.getFileForExerciseName(InternalFileManager.SAMPLE_FILE_NAME),
        false);
  }

  public boolean hasExercisesDirectory() {
    return this.exercisesDirectory.exists();
  }

  public boolean hasExercises() {
    return this.hasExercisesDirectory() && (this.getExerciseFiles().length > 0);
  }

  public boolean hasFileForExerciseName(final String exerciseName) {
    File file = this.getFileForExerciseName(exerciseName);
    return file.exists();
  }

  /**
   * Gets the exercise files this user has saved in the app's internal storage.
   * @return The exercise files this user has saved in the app's internal storage.
   */
  public File[] getExerciseFiles() {
    if (!this.hasExercisesDirectory()) {
      throw new IllegalArgumentException("No exercises directory so cannot get exercise files.");
    }

    return this.exercisesDirectory.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(final File dir, final String name) {
        return ExerciseFile.checkExerciseFileExtension(name);
      }
    });
  }

  public void writeExerciseToFile(final Exercise exercise, final boolean overwrite) {
    File file = this.getFileForExerciseName(exercise.name());
    InternalFileManager.writeExerciseToFile(exercise, file, overwrite);
  }

  private static void writeExerciseToFile(final Exercise exercise,
                                          final File file,
                                          final boolean overwriteIfExists) {
    if (file.exists() && !overwriteIfExists) {
      throw new IllegalStateException("File already exists but overwrite is not set.");
    }

    JSONObject json = ExerciseParser.jsonFromExercise(exercise);

    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      outputStream.write(json.toString().getBytes(InternalFileManager.CHARSET));

      outputStream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File getFileForExerciseName(final String exerciseName) {
    if (exerciseName.trim().isEmpty()) {
      throw new IllegalArgumentException("Cannot have a blank exercise name");
    }

    //noinspection StringConcatenationMissingWhitespace
    return new File(this.exercisesDirectory,
            exerciseName + ExerciseFile.EXERCISE_FILE_EXTENSION);
  }
}
