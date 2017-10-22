package com.budgebars.rotelle.gui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.files.ExerciseFile;
import com.budgebars.rotelle.files.InternalFileManager;
import com.budgebars.rotelle.gui.adapters.FileAdapter;
import com.budgebars.rotelle.workouts.editable.EditableExercise;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExerciseListingActivity extends AppCompatActivity {

    public static final String EXERCISE_TO_RUN = "EXERCISE_EXTRA";

    public static final String EDITABLE_EXERCISE = "EDITABLE_EXTRA";

    private static final int REQUEST_CODE_RESOLUTION = 3;

    private static final int REQUEST_CODE_CREATOR = 2;

    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;

    private Callback callback;

    private Listener listener;

    private GoogleApiClient googleApiClient;

    private Bitmap bitmapToSave;

    private FileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_listing);

        InternalFileManager files = new InternalFileManager(this);
        if (!files.hasExercisesDirectory())
        {
            files.createExercisesDirectory();
            files.addSampleExerciseFile(this);
        }

        File[] exerciseFiles = files.getExerciseFiles();
        final List<ExerciseFile> exercises = ExerciseFile.fromFiles(exerciseFiles);

        ListView listing = (ListView) this.findViewById(R.id.FileListView);
        this.adapter = new FileAdapter(exercises, this);
        listing.setAdapter(this.adapter);

        listing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.e("test", "activated");

                ExerciseFile exerciseFile = (ExerciseFile) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(ExerciseListingActivity.this, ExerciseActivity.class);
                intent.putExtra(ExerciseListingActivity.EXERCISE_TO_RUN, exerciseFile.getExercise());
                startActivity(intent);
            }
        });

        Button createButton = (Button) this.findViewById(R.id.CreateExerciseButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExerciseListingActivity.this.createExercise();
            }
        });

        this.callback = new Callback();
        this.listener = new Listener(this);

        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this.callback)
                .addOnConnectionFailedListener(this.listener)
                .build();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.populateExerciseListing();

        if (this.googleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            this.googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this.callback)
                    .addOnConnectionFailedListener(this.listener)
                    .build();
        }

        // Connect the client. Once connected, the camera is launched.
        this.googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (this.googleApiClient != null) {
            this.googleApiClient.disconnect();
        }
        super.onPause();
    }

    private void populateExerciseListing()
    {
        InternalFileManager files = new InternalFileManager(this);

        File[] exerciseFiles = files.getExerciseFiles();
        final List<ExerciseFile> exercises = ExerciseFile.fromFiles(exerciseFiles);

        this.adapter.updateFileList(exercises);
    }

    private void createExercise()
    {
        EditableExercise blank = EditableExercise.blankEditableExercise();

        Intent intent = new Intent(ExerciseListingActivity.this, EditExerciseActivity.class);
        intent.putExtra(ExerciseListingActivity.EDITABLE_EXERCISE, blank);
        startActivity(intent);
    }

    /**
     * Create a new file and save it to Drive.
     */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.e("GDriveTesting", "Creating new contents.");
        final Bitmap image = this.bitmapToSave;

        Drive.DriveApi.newDriveContents(this.googleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.e("GDriveTesting", "Failed to create new contents.");
                            return;
                        }

                        // Otherwise, we can write our data to the new contents.
                        Log.e("GDriveTesting", "New contents created.");

                        // Get an output stream for the contents.
                        OutputStream outputStream = result.getDriveContents().getOutputStream();

                        // Write the bitmap data from it.
                        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);

                        try {
                            outputStream.write(bitmapStream.toByteArray());
                        } catch (IOException e1) {
                            Log.e("GDriveTesting", "Unable to write file contents.");
                        }
                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                                .setMimeType("image/jpeg").setTitle("Android Photo.png").build();

                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(ExerciseListingActivity.this.googleApiClient);

                        try {
                            startIntentSenderForResult(
                                    intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("GDriveTesting", "Failed to launch file chooser.");
                        }
                    }
                });
    }

    private static class Listener implements GoogleApiClient.OnConnectionFailedListener
    {
        private Activity parent;

        public Listener(final Activity activity)
        {
            this.parent = activity;
        }

        @Override
        public void onConnectionFailed(ConnectionResult result) {
            // Called whenever the API client fails to connect.
            Log.e("GDriveTesting", "GoogleApiClient connection failed: " + result.toString());

            if (!result.hasResolution()) {
                // show the localized error dialog.
                GoogleApiAvailability.getInstance().getErrorDialog(this.parent, result.getErrorCode(), 0).show();
                return;
            }

            // The failure has a resolution. Resolve it.
            // Called typically when the app is not yet authorized, and an
            // authorization
            // dialog is displayed to the user.
            try {
                result.startResolutionForResult(this.parent, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                Log.e("GDriveTesting", "Exception while starting resolution activity", e);
            }
        }
    }

    private class Callback implements GoogleApiClient.ConnectionCallbacks
    {
        @Override
        public void onConnected(Bundle connectionHint) {
            Log.e("GDriveTesting", "API client connected.");

            if (bitmapToSave == null) {
                // This activity has no UI of its own. Just start the camera.
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                        REQUEST_CODE_CAPTURE_IMAGE);
                return;
            }

            saveFileToDrive();

            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.e("GDriveTesting", "GoogleApiClient connection suspended");
        }
    }
}
