package com.example.scanin.ImageDataModule;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ImageEditUtil {
    public static String[] filterList = {"magic_filter", "gray_filter", "dark_magic_filter", "sharpen_filter"};

    static {
        System.loadLibrary("image-edit-util");
    }

    // check for filter_name == null;
    public static boolean isValidFilter(String filter_name){
        return Arrays.asList(ImageEditUtil.filterList).contains(filter_name);
    }

    public static int getFilterId (String filter_name) {
        return Arrays.asList(filterList).indexOf (filter_name);
    }

    public static String getFilterName(int filter_id){
        if(filter_id == -1) return "original_filter";
        return filterList[filter_id];
    }

    public static Bitmap ImageProxyToBitmap(Image image){
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer yBuffer = planes[0].getBuffer();
            ByteBuffer uBuffer = planes[1].getBuffer();
            ByteBuffer vBuffer = planes[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            byte[] nv21 = new byte[ySize + uSize + vSize];
            //U and V are swapped
            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

            byte[] imageBytes = out.toByteArray();
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public static native void getTestGray (long imgAddr, long grayImgAddr);

    public static native void getBestPoints (long imgAddr, long pts);

    public static native void cropImage(long imgAddr, long cropImgAddr, long pts);

    public static native void filterImage(long imgAddr, long filterImgAddr, int filterId);

    public static native void changeContrastAndBrightness(long imgAddr, long outputAddr, double alpha, int beta);
}
