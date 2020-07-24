package com.example.scanin.ImageDataModule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.min;

public class ImageData {
    private Bitmap originalBitmap;
    private Bitmap croppedBitmap;
    private Bitmap currentBitmap;
    private String filterName;
    private Uri fileName;
    private ArrayList <Point> cropPosition;
    private int THUMBNAIL_SIZE = 64;
    private final double EPS = 1e-10;
    private int rotationConfig = 0;

    public ImageData(Uri uri) {
        this.originalBitmap = null;
        this.croppedBitmap = null;
        this.currentBitmap = null;
        this.filterName = null;
        this.fileName = uri;
        this.cropPosition = null;
        this.rotationConfig = 0;
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public Bitmap getCroppedBitmap() {
        return croppedBitmap;
    }

    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public Uri getFileName() {
        return fileName;
    }

    public String getFilterName() {
        return filterName;
    }

    public ArrayList <Point> getCropPosition() {
        return cropPosition;
    }

    public void setOriginalBitmap(Bitmap originalBitmap) {
        this.originalBitmap = originalBitmap;
    }

    public void setCroppedBitmap(Bitmap croppedBitmap) {
        this.croppedBitmap = croppedBitmap;
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

    public void setRotationConfig (int rotationConfig) {
        this.rotationConfig = rotationConfig;
    }

    public int getRotationConfig () {
        return rotationConfig;
    }

    public void setCropPosition(ArrayList <Point> cropPosition) {
        this.cropPosition = cropPosition;
    }

    public void setOriginalBitmap(Context context) throws IOException {
        try {
            this.originalBitmap =  MediaStore.Images.Media.getBitmap(context.getContentResolver() , fileName);
            this.originalBitmap = ImageData.rotateBitmap(this.originalBitmap);
            this.croppedBitmap = originalBitmap;
            this.currentBitmap = originalBitmap;
        } catch (Exception e){
            throw e;
        }
    }

    public static Bitmap rotateBitmap(Bitmap source) {
        float angle = 90.0f;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void rotateBitmap () {
        originalBitmap = rotateBitmap(originalBitmap);
        rotateCropPosition ();
        rotationConfig = (rotationConfig + 1) % 4;
    }

    public void rotateCropPosition () {
        if (cropPosition != null) {
            ArrayList <Point> res = new ArrayList<>();
            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();

            for (int i = 0; i < 4; i++) {
                int j = (i + 4) % 4;
                res.add (new Point (width - cropPosition.get(j).y, cropPosition.get(j).x));
            }
            cropPosition = res;
        }
    }

    public Bitmap getThumbnail() {
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(currentBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
        return thumbImage;
    }

    public double getScale (int cwidth, int cheight) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        double fx = (double) cwidth / width;
        double fy = (double) cheight / height;
        double scale = min (fx, fy);
        return scale;
    }

    //Change Brightness and contrast
    public static Bitmap changeContrastAndBrightness(Bitmap source, double alpha, int beta) {
        Bitmap bitmap;
        Mat imgToProcess = new Mat();
        Utils.bitmapToMat(source, imgToProcess);
        Mat outMat = new Mat();
        ImageEditUtil.changeContrastAndBrightness(imgToProcess.getNativeObjAddr(), outMat.getNativeObjAddr(), alpha, beta);
//        ImageEditUtil.filterImage(imgToProcess.getNativeObjAddr(), outMat.getNativeObjAddr(), 1);
        Bitmap currentBitmap = Bitmap.createBitmap(outMat.cols(),
                              outMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outMat, currentBitmap);
        return currentBitmap;
    }

    // cropped is applied on originalBitmap and saved in croppedBitmap
    public void applyCropImage () {
        if (originalBitmap != null && cropPosition != null) {
            Mat imgToProcess = new Mat();
            Utils.bitmapToMat(originalBitmap, imgToProcess);
            Mat outMat = new Mat();
            Mat pts = new Mat(4, 2, 1);
            for (int i = 0; i < 4; i++) {
                pts.put(i, 0, cropPosition.get(i).x);
                pts.put(i, 1, cropPosition.get(i).y);
            }
            ImageEditUtil.cropImage(imgToProcess.getNativeObjAddr(),
                    outMat.getNativeObjAddr(), pts.getNativeObjAddr());

            croppedBitmap = Bitmap.createBitmap(outMat.cols(),
                                    outMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(outMat, croppedBitmap);
        }
    }

    // filter is applied on croppedBitmap and saved in currentBitmap.
    public void applyFilter () {
        if (ImageEditUtil.isValidFilter(filterName)) {
            Mat imgToProcess = new Mat();
            Utils.bitmapToMat(croppedBitmap, imgToProcess);
            Mat outMat = new Mat();
            int filter_id = ImageEditUtil.getFilterId (filterName);
            ImageEditUtil.filterImage(imgToProcess.getNativeObjAddr(), outMat.getNativeObjAddr(), filter_id);
            currentBitmap = Bitmap.createBitmap(outMat.cols(),
                    outMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(outMat, currentBitmap);
        }
    }

    public void applyFilter (String filterName) {
        this.filterName = filterName;
        applyFilter();
    }

    public void applyCropImage (ArrayList <Point> cropPosition) {
        setCropPosition(cropPosition);
        applyCropImage();
    }

    public ArrayList <Point> getBestPoints () {
        ArrayList <Point> res = new ArrayList<Point>();
        if (originalBitmap != null) {
            Mat imgToProcess = new Mat();
            Utils.bitmapToMat(originalBitmap, imgToProcess);
            Mat pts = new Mat(4, 2, CvType.CV_16U);
            pts.setTo(new Scalar(-1));
            ImageEditUtil.getBestPoints(imgToProcess.getNativeObjAddr(), pts.getNativeObjAddr());
            for (int i = 0; i < 4; ++i) {
                res.add(new Point (pts.get(i, 0)[0], pts.get(i, 1)[0]));
            }
        }
        return res;
    }

    private Bitmap getSmallImage (Context context, Bitmap bitmap) {
        Log.d("onCreateEdit", "getSmallImage");
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int context_width = displayMetrics.widthPixels;
        int context_height = displayMetrics.heightPixels;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double fx = (double) context_width / width;
        double fy = (double) context_height / height;
        double scale = min (fx, fy);
        // adjusting for floating point errors.
        int new_width = (int) (width * scale - EPS);
        int new_height = (int) (height * scale - EPS);
        return ThumbnailUtils.extractThumbnail(bitmap, new_width, new_height);
    }

    public Bitmap getSmallOriginalImage(Context context) {
        return getSmallImage(context, originalBitmap);
    }

    public Bitmap getSmallCroppedImage(Context context) {
        return getSmallImage(context, croppedBitmap);
    }

    public Bitmap getSmallCurrentImage(Context context) {
        return getSmallImage(context, currentBitmap);
    }
}
