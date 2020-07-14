package com.example.scanin.DatabaseModule;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName="image_info")
public class ImageInfo {
    @PrimaryKey
    private @NonNull Uri uri;

    @ColumnInfo(name="status")
    private int status;

    @ColumnInfo(name="document_id")
    private long document_id;

    public ImageInfo(long document_id1, @NotNull Uri uri1){
        uri = uri1;
        document_id = document_id1;
        status = 1;
    }

    public ImageInfo(long document_id1){
        document_id = document_id1;
        status = 1;
        uri = Uri.parse("");
    }

    public ImageInfo(@NotNull Uri uri1){
        uri = uri1;
        status = 1;
    }
    
    public ImageInfo(){
        uri = Uri.parse("");
    }

    @NotNull
    public Uri getUri() {
        return uri;
    }

    public int getStatus() {
        return status;
    }

    public long getDocument_id() {
        return document_id;
    }

    public void setDocument_id(long document_id) {
        this.document_id = document_id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUri(@NotNull Uri uri) {
        this.uri = uri;
    }
}
