package com.example.scanin.ImageDataModule;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

import com.squareup.picasso.Transformation;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.Map;

import static com.example.scanin.ImageDataModule.ImageEditUtil.cropRequired;
import static com.example.scanin.ImageDataModule.ImageEditUtil.getScale;
import static com.example.scanin.ImageDataModule.ImageEditUtil.scalePoints;

public class CropTransformation implements Transformation {
    private Map<Integer, PointF> cropPoints;

    public CropTransformation(Map<Integer, PointF> cropPoints) {
        this.cropPoints = cropPoints;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap bitmap;
        bitmap = source.copy(source.getConfig(), true);
        Log.d("Filter-Trans", String.valueOf(bitmap.getHeight()));
        Log.d("Filter-Trans", String.valueOf(bitmap.getWidth()));
        source.recycle();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Log.d("Transformation", "Reached crop transformation.");

        if (cropRequired(this.cropPoints, width, height)) {
            Log.d("Transformation", "crop transformation code used.");
            float scale = getScale(width, height);
            cropPoints = scalePoints(cropPoints, scale);
            Mat imgToProcess = new Mat();
            Utils.bitmapToMat(bitmap, imgToProcess);
            Mat outMat = new Mat();
            Mat pts = new Mat(4, 2, CvType.CV_16U);

            // the order is wrong but there is an order function inside warp, should handle that.
            for (int i = 0; i < 4; i++) {
                pts.put(i, 0, cropPoints.get(i).x);
                pts.put(i, 1, cropPoints.get(i).y);
            }
            ImageEditUtil.cropImage(imgToProcess.getNativeObjAddr(),
                    outMat.getNativeObjAddr(), pts.getNativeObjAddr());
            Bitmap currentBitmap = Bitmap.createBitmap(outMat.cols(),
                    outMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(outMat, currentBitmap);
            Log.d ("Transform", "Didn't crash till now");
            return currentBitmap;
        } else {
            return bitmap;
        }
    }

    @Override
    public String key() {
        if (this.cropPoints == null) {
            return "null";
        } else {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                float x = this.cropPoints.get(i).x;
                float y = this.cropPoints.get(i).y;
                res.append(Float.toString(x)).append(" ").append(Float.toString(y)).append(" ");
            }
            return res.toString();
        }
    }
}