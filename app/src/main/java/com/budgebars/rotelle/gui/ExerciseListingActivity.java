package com.budgebars.rotelle.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.budgebars.rotelle.R;
import com.budgebars.rotelle.files.ExerciseFile;
import com.budgebars.rotelle.files.InternalFileManager;
import com.budgebars.rotelle.files.gdrive.FileListConnectionCallback;
import com.budgebars.rotelle.files.gdrive.GoogleDriveConnectionFailedListener;
import com.budgebars.rotelle.gui.adapters.FileAdapter;
import com.budgebars.rotelle.workouts.editable.EditableExercise;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.util.List;

public class ExerciseListingActivity extends AppCompatActivity {

    public static final String TAG = "TestingTag";

    public static final String EXERCISE_TO_RUN = "EXERCISE_EXTRA";

    public static final String EDITABLE_EXERCISE = "EDITABLE_EXTRA";

    private static final int REQUEST_CODE_CREATOR = 2;

    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;

    private FileListConnectionCallback fileListCallback;

    private GoogleDriveConnectionFailedListener listener;

    private GoogleApiClient googleApiClient;

    private Bitmap bitmapToSave;

    private FileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_listing);

        this.fileListCallback = new FileListConnectionCallback(this.googleApiClient);
        this.listener = new GoogleDriveConnectionFailedListener(this);

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
                Log.e(ExerciseListingActivity.TAG, "activated");

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

        Button saveToDriveButton = (Button) this.findViewById(R.id.SaveToDriveButton);
        saveToDriveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ExerciseListingActivity.this.googleApiClient == null) {
                    // Create the API client and bind it to an instance variable.
                    // We use this instance as the callback for connection and connection
                    // failures.
                    // Since no account name is passed, the user is prompted to choose.
                    ExerciseListingActivity.this.createGoogleApiClient();
                }

                // Connect the client. Once connected, the camera is launched.
                ExerciseListingActivity.this.googleApiClient.connect();
            }
        });

        this.createGoogleApiClient();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.populateExerciseListing();

        if (this.googleApiClient == null) {
            this.createGoogleApiClient();
        }
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

    private void createGoogleApiClient()
    {
        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE) // TODO -- DETERMINE IF THIS SCOPE IS ACTUALLY NEEDED (THINK THE FOLLOWING SCOPE IS SUFFICIENT)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this.fileListCallback)
                .addOnConnectionFailedListener(this.listener)
                .build();

        Log.e(ExerciseListingActivity.TAG, "API client created.");
    }

    /**
     * Create a new file and save it to Drive.
     */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.e(ExerciseListingActivity.TAG, "Creating new contents.");
        final Bitmap image = this.bitmapToSave;

        final MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("appconfig.txt")
                .setMimeType("text/plain")
                .setDescription("Sample crap to put up")
                .build();

        // First create the contents
        Drive.DriveApi.newDriveContents(this.googleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult)
            {
                if (driveContentsResult.getStatus().isSuccess())
                {
                    // If the contents were succcesfully saved, commit them to a file.
                    Drive.DriveApi.getAppFolder(ExerciseListingActivity.this.googleApiClient)
                            .createFile(ExerciseListingActivity.this.googleApiClient,
                                    changeSet,
                                    driveContentsResult.getDriveContents())
                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>()
                            {
                                @Override
                                public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                                    Log.e(ExerciseListingActivity.TAG, "created new file: " + driveFileResult.getDriveFile().toString());
                                }
                            });
                }
                else
                {
                    Log.e(ExerciseListingActivity.TAG, "Failed to create new contents.");
                }
            }
        });
    }
}
