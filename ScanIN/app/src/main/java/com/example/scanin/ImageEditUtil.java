package com.example.scanin;

import java.util.Arrays;

public class ImageEditUtil {
    private static String[] filterList = {"black_and_white", "magic_filter"};

    public static boolean isValidFilter(String filter_name){
        return Arrays.asList(ImageEditUtil.filterList).contains(filter_name);
    }
}
