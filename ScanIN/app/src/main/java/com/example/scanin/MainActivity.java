package com.example.scanin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
                        CAMERA_IMAGE_REQUEST_CODE);     // request code , must be a unique value
            }


        } else if (view.getId() == R.id.saveBtn) {

            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                try {

                    SaveFile.saveImage(MainActivity.this, bitmap);

                    Toast.makeText(MainActivity.this,
                            "The image is saved successfully to external storage.",
                            Toast.LENGTH_SHORT).show();


                } catch (Exception e) {

                    e.printStackTrace();

                }


            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(MainActivity.this,
                "OnActivityResult is called",
                Toast.LENGTH_SHORT);

        if (requestCode == CAMERA_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle bundle = data.getExtras();

            bitmap = (Bitmap) bundle.get("data");

            capturePreview.setImageBitmap(bitmap);

        }

    }


    /**
    * A native method that is implemented by the 'native-lib' native library,
    * which is packaged with this application.
    */
    public native String stringFromJNI();
    public native String validate(long madAddrGr,long matAddrRgba);
}
