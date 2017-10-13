package com.budgebars.rotelle.files;

import android.content.Context;

import com.budgebars.rotelle.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Jules on 10/13/2017.
 */

public class WorkoutParser {

    public String readFileContents(final Context context)
    {
        InputStream inputStream = context.getResources().openRawResource(R.raw.sample_exercise);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}
