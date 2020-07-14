package com.example.scanin.DatabaseModule;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="image_info")
public class ImageInfo {
    @PrimaryKey(autoGenerate = true)
    private Uri uri;

    @ColumnInfo(name="status")
    private int status;

    @ColumnInfo(name="document_id")
    private long document_id;

    public ImageInfo(long document_id1, Uri uri1){
        uri = uri1;
        document_id = document_id1;
    }

    public Uri getUri() {
        return uri;
    }

    public int getStatus() {
        return status;
    }

    public long getDocument_id() {
        return document_id;
    }
}
