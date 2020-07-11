package com.example.scanin;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.scanin.HomeModule.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class SaveFile {

    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = MainActivity.WRITE_EXTERNAL_STORAGE_REQUEST_CODE;

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


    public static void savePDF(Activity myActivity, PdfDocument document) throws IOException {

        String externalStorageState = Environment.getExternalStorageState();
        FileOutputStream myFile = null;

        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {

            File savedPDFDirectory = myActivity.getExternalFilesDir("ScanIN");

            Date currentDate = new Date();
            long elapsedTime = SystemClock.elapsedRealtime();
            String uniquePDFName = "/ScanIN_" + currentDate + "_" + elapsedTime + ".pdf";

            myFile = new FileOutputStream(savedPDFDirectory + uniquePDFName);

            document.writeTo(myFile);

            Log.i("PDF", myFile.toString());

        } else {
            throw new IOException("This device doesn't have an storage.");
        }
    }

    public static void createPDFfromListOfBitmap(Activity myActivity,
                                                 ArrayList<Bitmap> listBmp) throws IOException{


        PdfDocument document = new PdfDocument();

        int pageNumber = 1;
        for (Bitmap bmp : listBmp) {

            // Create a page of the same size as the image

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bmp.getWidth(),
                    bmp.getHeight(), pageNumber).create();

            PdfDocument.Page page = document.startPage(pageInfo);

            // Draw the bitmap onto the page
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(bmp, 0f, 0f, null);
            document.finishPage(page);

            pageNumber += 1;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(myActivity ,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                // write PDF file
                SaveFile.savePDF(myActivity, document);

                Toast.makeText(myActivity,
                        "The pdf is saved successfully to external storage.",
                        Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(myActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
        document.close();
    }

}
