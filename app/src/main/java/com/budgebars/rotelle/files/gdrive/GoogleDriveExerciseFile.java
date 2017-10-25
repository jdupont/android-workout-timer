package com.budgebars.rotelle.files.gdrive;

import com.google.android.gms.drive.DriveId;

/**
 * Created by Jules on 10/23/2017.
 */

public class GoogleDriveExerciseFile
{
    private final String title;

    private final String description;

    private final DriveId id;

    public GoogleDriveExerciseFile(final String title, final String description, final DriveId id)
    {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public DriveId driveId()
    {
        return this.id;
    }
}
