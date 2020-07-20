package com.example.scanin.DatabaseModule;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.scanin.ImageDataModule.ImageEditUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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

    @ColumnInfo(name="filterId")
    private int filterId;

    @ColumnInfo(name="cropPosition")
    private String cropPosition;

    @ColumnInfo(name="rotationConfig")
    private int rotationConfig;

    @ColumnInfo(name="rotationAfter")
    private int rotationAfter;

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
        filterId = -1;
        cropPosition = null;
        rotationConfig = 0;
        rotationAfter = 0;
    }

    public ImageInfo(long document_id1, long position1){
        img_document_id = document_id1;
        position = position1;
        status = 1;
        filterId = -1;
        cropPosition = null;
        rotationConfig = 0;
        rotationAfter = 0;
    }

    public ImageInfo(Uri uri1, long position1){
        uri = uri1;
        position = position1;
        status = 1;
        filterId = -1;
        cropPosition = null;
        rotationConfig = 0;
        rotationAfter = 0;
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

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filter_id) {
        this.filterId = filter_id;
    }

    public String getCropPosition () {
        return cropPosition;
    }

    public void setCropPosition (String cropPosition) {
        this.cropPosition = cropPosition;
    }

    public Map <Integer, PointF> getCropPositionMap() {
        if (cropPosition == null) {
            return null;
        } else {
            String[] spl = cropPosition.split(" ");
            Map <Integer, PointF> res = new HashMap <> ();
            for (int i = 0; i < 4; i++) {
                res.put (i, new PointF (Float.parseFloat(spl[2 * i]), Float.parseFloat(spl[2 * i + 1])));
            }
            return res;
        }
    }

    public void setCropPositionMap (Map <Integer, PointF> cropPosition) throws NullPointerException {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            float x = cropPosition.get(i).x;
            float y = cropPosition.get(i).y;
            res.append(Float.toString(x)).append(" ").append(Float.toString(y)).append(" ");
        }
        this.cropPosition = res.toString();
    }

    public int getRotationConfig() {
        return rotationConfig;
    }

    public void setRotationConfig(int rotationValue) {
        this.rotationConfig = rotationValue;
    }

    public int getRotationAfter () {
        return rotationAfter;
    }

    public void setRotationAfter (int rotationAfter) {
        this.rotationAfter = rotationAfter;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
        this.filterId = ImageEditUtil.getFilterId(filterName);
    }
}
