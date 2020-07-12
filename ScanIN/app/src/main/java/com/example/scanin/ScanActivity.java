package com.example.scanin;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.FragmentManager;

import com.example.scanin.ImageDataModule.ImageData;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity implements CameraFragment.OnImageClickListener,
        ImageGridFragment.ImageGridFragmentCallback, ImagePreviewFragment.ImagePreviewFragmentCallback{
    private ImageGridFragment imageGridFragment = null;
    private CameraFragment cameraFragment = null;
    public ImageEditFragment imageEditFragment = null;
    private ImagePreviewFragment imagePreviewFragment = null;

    private ArrayList<ImageData> imageData = new ArrayList<ImageData>();
    //    public List<String> imageFiles = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

    }

    @Override
    public void cameraFragmentCallback(int CALLBACK_CODE) {
        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.fragment_camera, imageGridFragment)
//                .addToBackStack(null)
//                .commit();

        fragmentManager.beginTransaction()
                .add(R.id.fragment_camera, imageGridFragment)
                .addToBackStack(null)
                .commit();
    }

    private void createImagePreviewFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_camera, imagePreviewFragment)
                .commit();
    }

    @Override
    public void cameraFragmentCallback(int CALLBACK_CODE, ImageProxy[] bitmaps) {
        for(ImageProxy bitmap:bitmaps){
            @SuppressLint("UnsafeExperimentalUsageError") Image image = bitmap.getImage();
            Bitmap temp = ImageProxyToBitmap(image);
            ImageData currentItem = new ImageData(temp);
            imageData.add(currentItem);
            imagePreviewFragment.setBitmap(currentItem.getOriginalBitmap());
            createImagePreviewFragment();
        }
    }

    @Override
    public void onCreateGridCallback() {
        Log.d("ScanActivity: ", "createGridCallback");
        imageGridFragment.setImagePathList(imageData);
    }

    @Override
    public void onRemovePreviewCallback(int callback_code) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .remove(imagePreviewFragment)
                .commit();
        if(callback_code == ImagePreviewFragment.CONTINUE_CAPTURE_CALLBACK){
            return;
        }else if(callback_code == ImagePreviewFragment.RETRY_CAPTURE_CALLBACK){
            imageData.remove(imageData.size() - 1);
        }
    }

    public Bitmap ImageProxyToBitmap(Image image){
        Image.Plane planes = image.getPlanes()[0];
        ByteBuffer buffer = planes.getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}