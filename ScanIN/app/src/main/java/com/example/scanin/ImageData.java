package com.example.scanin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

public class ImageData {
    private Bitmap originalBitmap;
    private Bitmap currentBitmap;
    private String filterName;
    private Uri fileName;
    private int[] cropPosition;
    private int THUMBNAIL_SIZE = 64;

    ImageData(Uri fileName){
        this.originalBitmap = null;
        this.currentBitmap = null;
        this.filterName = null;
        this.fileName = fileName;
        this.cropPosition = null;
    }

    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public Uri getFileName() {
        return fileName;
    }

    public String getFilterName() {
        return filterName;
    }

    public int[] getCropPosition() {
        return cropPosition;
    }

    public void setOriginalBitmap(Bitmap originalBitmap) {
        this.originalBitmap = originalBitmap;
    }

    public void setCurrentBitmap(Bitmap currentBitmap) {
        this.currentBitmap = currentBitmap;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public void setFileName(Uri fileName) {
        this.fileName = fileName;
    }

    public void setCropPosition(int[] cropPosition) {
        this.cropPosition = cropPosition;
    }

    public void setOriginalBitmap(Context context) throws IOException {
        try {
            this.originalBitmap =  MediaStore.Images.Media.getBitmap(context.getContentResolver() , fileName);
            this.originalBitmap = ImageData.RotateBitmap(this.originalBitmap);
        }catch (Exception e){
            throw e;
        }
    }

    public static Bitmap RotateBitmap(Bitmap source)
    {
        float angle = 90.0f;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Bitmap getThumbnail(){
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(originalBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        return thumbImage;
    }

    public Bitmap getSmallImage(){
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(originalBitmap, 480, 760);
        return thumbImage;
    }
}
