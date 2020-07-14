package com.example.scanin.DatabaseModule;

import android.net.Uri;

import androidx.room.TypeConverter;

public class UriConverter {
    @TypeConverter
    public static String fromTimestamp(Uri uri) {
        return uri == null ? null : uri.toString();
    }

    @TypeConverter
    public static Uri dateToTimestamp(String s) {
        return s == null ? null : Uri.parse(s);
    }
}
