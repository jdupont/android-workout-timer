package com.budgebars.rotelle.files.gdrive;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Jules on 10/23/2017.
 */

public class GDriveManager
{
    private final GoogleApiClient apiClient;

    public GDriveManager(final GoogleApiClient apiClient)
    {
        this.apiClient = apiClient;
    }
}
