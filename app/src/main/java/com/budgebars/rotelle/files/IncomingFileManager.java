package com.budgebars.rotelle.files;

import android.content.Context;
import android.net.Uri;

import com.budgebars.rotelle.workouts.Exercise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static String streamToString(final InputStream stream)
    {
         try (BufferedReader reader =  new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8)) {
             StringBuilder sb = new StringBuilder();

             String line;
             while ((line = reader.readLine()) != null) {
                 sb.append(line).append(java.io.File.pathSeparator).append('n');
             }

             return sb.toString();
         }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}

