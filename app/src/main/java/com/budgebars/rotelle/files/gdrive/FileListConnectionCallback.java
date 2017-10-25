package com.budgebars.rotelle.files.gdrive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.budgebars.rotelle.gui.ExerciseListingActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 10/23/2017.
 */
public class FileListConnectionCallback implements GoogleApiClient.ConnectionCallbacks
{
    private final GoogleApiClient apiClient;

    private final List<GoogleDriveExerciseFile> fileList;

    public FileListConnectionCallback(final GoogleApiClient apiClient)
    {
        this.apiClient = apiClient;
        this.fileList = new ArrayList<>();
    }

    @Override
    public void onConnected(Bundle connectionHint)
    {
        Log.e(ExerciseListingActivity.TAG, "API client connected. Connected here");
        this.getGoogleDriveFileList();
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.e(ExerciseListingActivity.TAG, "GoogleApiClient connection suspended: " + cause);
    }

    private void getGoogleDriveFileList()
    {
        Drive.DriveApi.getAppFolder(this.apiClient)
            .listChildren(this.apiClient)
            .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult)
                {
                    FileListConnectionCallback.this.fileList.clear();

                    MetadataBuffer buffer = metadataBufferResult.getMetadataBuffer();

                    for (int i = 0; i < buffer.getCount(); ++i)
                    {
                        Metadata data = buffer.get(i);
                        FileListConnectionCallback.this.fileList.add(new GoogleDriveExerciseFile(
                                data.getTitle(),
                                data.getDescription(),
                                data.getDriveId()));
                    }

                    buffer.release();
                }
            });
    }
}