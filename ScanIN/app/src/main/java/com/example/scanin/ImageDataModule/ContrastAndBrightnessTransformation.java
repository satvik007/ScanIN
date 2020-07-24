package com.example.scanin.ImageDataModule;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class ContrastAndBrightnessTransformation implements Transformation {
    private double alpha;
    private int beta;

    public ContrastAndBrightnessTransformation(double alpha, int beta){
        this.alpha = alpha;
        this.beta = beta;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap bitmap;
        bitmap = source.copy(source.getConfig(), true);
        source.recycle();
        Mat imgToProcess = new Mat();
        Utils.bitmapToMat(bitmap, imgToProcess);
        Mat outMat = new Mat();
        ImageEditUtil.changeContrastAndBrightness(imgToProcess.getNativeObjAddr(), outMat.getNativeObjAddr(), this.alpha, this.beta);
        Bitmap currentBitmap = Bitmap.createBitmap(outMat.cols(),
                outMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outMat, currentBitmap);
        return currentBitmap;
    }

    @Override
    public String key() {
        return "brightness"+this.beta+"contrast"+this.alpha+"";
    }
}
