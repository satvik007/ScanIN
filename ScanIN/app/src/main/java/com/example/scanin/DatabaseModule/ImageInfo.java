package com.example.scanin.DatabaseModule;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="image_info")
public class ImageInfo {
    @PrimaryKey
    private Uri uri;

    @ColumnInfo(name="status")
    private int status;

    public ImageInfo(Uri uri1){
        uri = uri1;
    }

    public Uri getUri() {
        return uri;
    }

    public int getStatus() {
        return status;
    }
}
