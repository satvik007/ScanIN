package com.example.scanin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.opencv.android.OpenCVLoader;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton btnTakePicture;
    private Button btnSavePicture;
    private ImageView capturePreview;

    private static final int CAMERA_IMAGE_REQUEST_CODE = 1000;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2000;

    private Bitmap bitmap;

    private static String TAG="MainActivity";
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView=(TextView) findViewById(R.id.sample_text);
        textView.setText(stringFromJNI());

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        if (OpenCVLoader.initDebug()) {
            textView.setText(textView.getText()+"\n OPENCV LOADED SUCCESSFULLY");
            textView.setText(textView.getText()+"\n"+validate(500,500));

        } else {
            Log.d(TAG, "OPENCV DİD NOT LOAD");

        }

        btnTakePicture = (FloatingActionButton) findViewById(R.id.fab);
        btnSavePicture = (Button) findViewById(R.id.saveBtn);
        capturePreview = (ImageView) findViewById(R.id.capturePreview);

        btnTakePicture.setOnClickListener(MainActivity.this);
        btnSavePicture.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            startCameraActivity();
        }
    }

    public void startCameraActivity(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public native String stringFromJNI();
    public native String validate(long madAddrGr,long matAddrRgba);
}
