package com.example.scanin.ImageDataModule;

import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.picasso.Transformation;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class FilterTransformation implements Transformation {
    private String filter_name;

    public FilterTransformation(String filter_name){
        this.filter_name = filter_name;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap bitmap;
        bitmap = source.copy(source.getConfig(), true);
        Log.d("Filter-Trans", String.valueOf(bitmap.getHeight()));
        Log.d("Filter-Trans", String.valueOf(bitmap.getWidth()));
        source.recycle();
        if(this.filter_name.equals("original_filter")){
            return bitmap;
        }
        else if (ImageEditUtil.isValidFilter(this.filter_name)) {
            Mat imgToProcess = new Mat();
            Utils.bitmapToMat(bitmap, imgToProcess);
            Mat outMat = new Mat();
            int filter_id = ImageEditUtil.getFilterId (this.filter_name);
            ImageEditUtil.filterImage(imgToProcess.getNativeObjAddr(), outMat.getNativeObjAddr(), filter_id);
            Bitmap currentBitmap = Bitmap.createBitmap(outMat.cols(),
                    outMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(outMat, currentBitmap);
            return currentBitmap;
        }else{
            return null;
        }
    }

    @Override
    public String key() {
        return this.filter_name;
    }
}
