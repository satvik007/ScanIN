package com.example.scanin.DatabaseModule;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(indices = {@Index(value = {"img_document_id"})},
        tableName="image_info", foreignKeys = @ForeignKey(entity = Document.class,
         parentColumns = "document_id", childColumns = "img_document_id", onDelete = ForeignKey.CASCADE))
public class ImageInfo {
    @PrimaryKey(autoGenerate = true)
    private long image_id;

    private long position;

    @ColumnInfo(name="uri")
    private  Uri uri;

    @ColumnInfo(name="status")
    private int status;

    @ColumnInfo(name="img_document_id")
    private long img_document_id;

    @Ignore
    private Bitmap originalBitmap;
    @Ignore
    private Bitmap croppedBitmap;
    @Ignore
    private Bitmap currentBitmap;
    @Ignore
    private String filterName;

    public ImageInfo(long document_id1, @NotNull Uri uri1, long position1){
        uri = uri1;
        img_document_id = document_id1;
        status = 1;
        position = position1;
    }

    public ImageInfo(long document_id1, long position1){
        img_document_id = document_id1;
        position = position1;
        status = 1;
    }

    public ImageInfo(Uri uri1, long position1){
        uri = uri1;
        position = position1;
        status = 1;
    }
    
    public ImageInfo(){

    }

    public Uri getUri() {
        return uri;
    }

    public int getStatus() {
        return status;
    }

    public long getImg_document_id() {
        return img_document_id;
    }

    public void setImg_document_id(long img_document_id) {
        this.img_document_id = img_document_id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUri(@NotNull Uri uri) {
        this.uri = uri;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public long getImage_id() {
        return image_id;
    }

    public void setImage_id(long image_id) {
        this.image_id = image_id;
    }
}
