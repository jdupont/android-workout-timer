package com.budgebars.rotelle.files;

import android.content.Context;
import android.net.Uri;

import com.budgebars.rotelle.workouts.Exercise;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Jules on 10/26/2017.
 */

public class IncomingFileManager {

    private final Context context;

    public IncomingFileManager(final Context context)
    {
        this.context = context;
    }

    public Exercise fromUri(final Uri uri)
    {
        try {
            String json = IncomingFileManager.streamToString(this.context.getContentResolver().openInputStream(uri));

            return ExerciseParser.parse(json);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private static String streamToString(final InputStream stream) {
        try (Scanner scanner = new Scanner(stream).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}

