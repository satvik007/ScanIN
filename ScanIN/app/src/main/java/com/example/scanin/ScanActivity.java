package com.example.scanin;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class ScanActivity extends AppCompatActivity implements CameraFragment.OnImageClickListener, ImageGridFragment.ImageGridFragmentCallback{
    private ImageGridFragment imageGridFragment = null;
    private CameraFragment cameraFragment = null;
    public ImageEditFragment imageEditFragment = null;
    private ArrayList<ImageData> imageData = new ArrayList<ImageData>();
//    public List<String> imageFiles = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        CameraFragment cameraFragment = new CameraFragment();
        imageGridFragment = new ImageGridFragment();
        imageEditFragment = new ImageEditFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_camera, cameraFragment)
                .commit();
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

    @Override
    public void cameraFragmentCallback(int CALLBACK_CODE, Uri[] file_uris) {
        for(Uri uri : file_uris){
            imageData.add(new ImageData(uri));
        }
    }

    @Override
    public void onCreateGridCallback() {
        Log.d("ScanActivity: ", "createGridCallback");
        imageGridFragment.setImagePathList(imageData);
    }
}