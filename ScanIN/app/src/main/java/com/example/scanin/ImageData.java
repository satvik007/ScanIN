package com.example.scanin;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImageData {
    private Bitmap originalBitmap;
    private Bitmap currentBitmap;
    private String filterName;
    private Uri fileName;
    private int[] cropPosition;

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
}
