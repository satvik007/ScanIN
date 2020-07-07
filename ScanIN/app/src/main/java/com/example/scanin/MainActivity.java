package com.example.scanin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.opencv.android.OpenCVLoader;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton btnTakePicture;
    private Button btnSavePicture;
    private ImageView imgPhoto;

    private static final int CAMERA_IMAGE_REQUEST_CODE = 1000;

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

        btnTakePicture.setOnClickListener(MainActivity.this);
        btnSavePicture.setOnClickListener(MainActivity.this);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {

            int permissionResult = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CAMERA);

            if (permissionResult == PackageManager.PERMISSION_GRANTED) {

                PackageManager packageManager = getPackageManager();
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_IMAGE_REQUEST_CODE);

                } else {

                    Toast.makeText(MainActivity.this,
                            "Your device doesn't have a camera!",
                            Toast.LENGTH_SHORT).show();

                }

            } else {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        1);     // request code , must be a unique value


            }


        } else if (view.getId() == R.id.saveBtn) {

        }
    }


    /**
    * A native method that is implemented by the 'native-lib' native library,
    * which is packaged with this application.
    */
    public native String stringFromJNI();
    public native String validate(long madAddrGr,long matAddrRgba);
}
