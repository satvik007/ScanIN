package com.example.scanin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.SystemClock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class SaveFile {

    public static File saveImage(Activity myActivity, Bitmap bitmap) throws IOException {

        String externalStorageState = Environment.getExternalStorageState();
        File myFile = null;

        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {

            File savedImageDirectory = myActivity.getExternalFilesDir("ScanIN");

            Date currentDate = new Date();
            long elapsedTime = SystemClock.elapsedRealtime();
            String uniqueImageName = "/" + currentDate + "_" + elapsedTime + ".png";

            myFile = new File(savedImageDirectory + uniqueImageName);
            long freeSpace = savedImageDirectory.getFreeSpace();
            long requiredSpace = bitmap.getByteCount();

            if (requiredSpace * 1.8 < freeSpace) {
                // enough space to store img in external storage

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(myFile);
                    boolean isImageSaveSuccessful = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                    if (isImageSaveSuccessful) {
                        return myFile;
                    } else {
                        throw new IOException("Something went wrong while saving the image.");
                    }

                } catch (Exception e) {
                    throw new IOException("Couldn't store the image.");
                }

            } else {
                throw new IOException("Not enough space to save image.");
            }
        } else {
            throw new IOException("This device doesn't have an external storage.");
        }
    }

}
