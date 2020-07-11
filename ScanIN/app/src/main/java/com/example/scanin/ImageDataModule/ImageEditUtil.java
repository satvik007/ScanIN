package com.example.scanin.ImageDataModule;

import java.util.Arrays;

public class ImageEditUtil {
    private static String[] filterList = {"black_and_white", "magic_filter"};

    static {
        System.loadLibrary("image-edit-util");
    }

    public static boolean isValidFilter(String filter_name){
        return Arrays.asList(ImageEditUtil.filterList).contains(filter_name);
    }

    public static native void getTestGray(long imgAddr, long grayImgAddr);
}
