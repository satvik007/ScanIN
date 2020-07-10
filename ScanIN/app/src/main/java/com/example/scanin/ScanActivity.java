package com.example.scanin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class ScanActivity extends AppCompatActivity implements CameraFragment.OnImageClickListener{
    private ImageGridFragment imageGridFragment = null;
//    public List<String> imageFiles = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        CameraFragment cameraFragment = new CameraFragment();
        imageGridFragment = new ImageGridFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_camera, cameraFragment)
                .commit();
    }

    @Override
    public void startGridFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_camera, imageGridFragment)
                .addToBackStack(null)
                .commit();
    }
}