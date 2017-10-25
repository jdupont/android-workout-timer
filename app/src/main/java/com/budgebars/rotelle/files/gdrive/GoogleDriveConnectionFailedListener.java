package com.budgebars.rotelle.files.gdrive;

import android.app.Activity;
import android.content.IntentSender;
import android.util.Log;

import com.budgebars.rotelle.gui.ExerciseListingActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Jules on 10/23/2017.
 */
public class GoogleDriveConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener
{
    private static final int REQUEST_CODE_RESOLUTION = 3;

    private Activity parent;

    public GoogleDriveConnectionFailedListener(final Activity activity)
    {
        this.parent = activity;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.e(ExerciseListingActivity.TAG, "GoogleApiClient connection failed (usually a sign " +
                "that the app needs to be authorized by the user through Google Drive dialog): " + result.toString());

        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this.parent, result.getErrorCode(), 0).show();
            Log.e(ExerciseListingActivity.TAG, "Fatal error because connection problem does not have resolution.");
            return;
        }

        // The failure has a resolution. Resolve it. Called typically when the app is not yet authorized, and an
        // authorization dialog is displayed to the user.
        try {
            result.startResolutionForResult(this.parent, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(ExerciseListingActivity.TAG, "Exception while starting resolution activity", e);
        }
    }
}