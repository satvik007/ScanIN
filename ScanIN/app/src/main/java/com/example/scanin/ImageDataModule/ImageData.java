package com.example.scanin.ImageDataModule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;

import android.graphics.Paint;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.scanin.DatabaseModule.ImageInfo;

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
    private int origWidth;
    private int origHeight;
    public static int MAX_SIZE=1500;

    public ImageData(Uri uri) {
        this.originalBitmap = null;
        this.filterName = null;
        this.fileName = uri;
        this.cropPosition = null;
        this.rotationConfig = 0;
    }

    public ImageData(ImageInfo imgInfo) {
        this.originalBitmap = null;
        this.filterName = ImageEditUtil.getFilterName(imgInfo.getFilterId());
        this.fileName = imgInfo.getUri();
        this.cropPosition = ImageEditUtil.convertMap2ArrayList(imgInfo.getCropPositionMap());
        this.rotationConfig = imgInfo.getRotationConfig();
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

    public ArrayList <Point> getCropPosition() {
        return cropPosition;
    }

    public void setOriginalBitmap(Bitmap originalBitmap) {
        this.originalBitmap = originalBitmap;
        // Intentionally not updating origWidth and origHeight.
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

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void setOriginalBitmap(Context context) throws IOException {
        try {
            this.originalBitmap =  MediaStore.Images.Media.getBitmap(context.getContentResolver() , fileName);
            this.originalBitmap = ImageData.rotateBitmap(this.originalBitmap);
            this.originalBitmap = getResizedBitmap(this.originalBitmap, MAX_SIZE);
            this.croppedBitmap = originalBitmap;
            this.currentBitmap = originalBitmap;
            this.origHeight = this.originalBitmap.getHeight();
            this.origWidth = this.originalBitmap.getWidth();
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

    public static Bitmap rotateBitmap (Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void rotateBitmap () {
        originalBitmap = rotateBitmap(originalBitmap);
        rotateCropPosition ();
        rotationConfig = (rotationConfig + 1) % 4;
    }

    public int getWidth() {
        return origWidth;
    }

    public int getHeight() {
        return origHeight;
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
    public static Bitmap changeContrastAndBrightness(Bitmap bitmap, float contrast, int beta) {
//        Bitmap bitmap;
//        Mat imgToProcess = new Mat();
//        Utils.bitmapToMat(source, imgToProcess);
//        Mat outMat = new Mat();
//        ImageEditUtil.changeContrastAndBrightness(imgToProcess.getNativeObjAddr(), outMat.getNativeObjAddr(), alpha, beta);
////        ImageEditUtil.filterImage(imgToProcess.getNativeObjAddr(), outMat.getNativeObjAddr(), 1);
//        Bitmap currentBitmap = Bitmap.createBitmap(outMat.cols(),
//                              outMat.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(outMat, currentBitmap);
        float[] colorTransform = new float[]{
                contrast, 0, 0, 0, 0,
                0, contrast, 0, 0, 0,
                0, 0, contrast, 0, 0,
                0, 0, 0, 1, 0};

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0f);
        colorMatrix.set(colorTransform);

        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);

        Bitmap resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);

        return resultBitmap;
//        return currentBitmap;
    }

    // cropped is applied on originalBitmap and saved in croppedBitmap
    public void applyCropImage () {
        if (originalBitmap != null && cropPosition != null) {
            Mat imgToProcess = new Mat();
            Utils.bitmapToMat(originalBitmap, imgToProcess);
            Mat outMat = new Mat();
            Mat pts = new Mat(4, 2, CvType.CV_16U);
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

    public Bitmap getSmallOriginalImage(Context context) {
        return this.originalBitmap;
    }
}
