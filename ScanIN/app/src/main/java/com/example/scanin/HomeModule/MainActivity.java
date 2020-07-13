package com.example.scanin.HomeModule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scanin.ImageDataModule.ImageEditUtil;
import com.example.scanin.R;
import com.example.scanin.ScanActivity;
import com.example.scanin.StateMachineModule.MachineActions;
import com.example.scanin.StateMachineModule.MachineStates;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.InputStream;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton btnTakePicture;
    private ImageButton btnSavePicture;
    private ImageView capturePreview;

    public static final int CAMERA_ACTIVITY_REQUEST_CODE = 0;
    public static final int CAMERA_IMAGE_REQUEST_CODE = 1000;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2000;

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
//        textView.setText(stringFromJNI());

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());

        if (OpenCVLoader.initDebug()) {
            textView.setText(textView.getText()+"\n OPENCV LOADED SUCCESSFULLY");
//            textView.setText(textView.getText()+"\n"+validate(500,500));

        } else {
            Log.d(TAG, "OPENCV DİD NOT LOAD");

        }

        btnTakePicture = (FloatingActionButton) findViewById(R.id.fab);
        btnSavePicture = (ImageButton) findViewById(R.id.open_doc);
        capturePreview = (ImageView) findViewById(R.id.capturePreview);

        btnTakePicture.setOnClickListener(MainActivity.this);
        btnSavePicture.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            view.setClickable(false);
            startCameraActivity();
        }
        else if(view.getId()==R.id.open_doc){
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra("STATE", MachineStates.HOME);
            intent.putExtra("ACTION", MachineActions.HOME_OPEN_DOC);
            startActivity(intent);
        }
    }

    public void startCameraActivity(){
//        Intent intent = new Intent(this, CameraActivity.class);
////        startActivity(intent);
//
//        startActivityForResult(intent, CAMERA_ACTIVITY_REQUEST_CODE);
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("STATE", MachineStates.HOME);
        intent.putExtra("ACTION", MachineActions.HOME_ADD_SCAN);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Log.d("Main-Activity", "OnDestroyCalled");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("Main-Activity", "OnPauseCalled");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("Main-Activity", "OnStopCalled");
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.d("Main-Activity", "OnStartCalled");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("Main-Activity", "OnResumeCalled");
        findViewById(R.id.fab).setClickable(true);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_ACTIVITY_REQUEST_CODE) {
            // if camera activity result was returned
            if (resultCode == RESULT_OK) {
                Uri savedImageUri = Uri.parse(data.getStringExtra("imageUri"));
                Log.i("Uri", "" + savedImageUri);

                InputStream inputStream = null;

                try{
                    assert savedImageUri != null;
                    inputStream = getContentResolver().openInputStream(savedImageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Bitmap bmp = BitmapFactory.decodeStream(inputStream);

                Log.d("BMP width", "" + bmp.getWidth());
                Log.d("BMP height", "" + bmp.getHeight());

                Mat imgToProcess = new Mat();
                Utils.bitmapToMat(bmp, imgToProcess);

                // this will convert it to gray -- testing native call
                Mat grayMat = new Mat();     // get cv::Mat from nativeObjectAddr
                ImageEditUtil.getTestGray(imgToProcess.getNativeObjAddr(), grayMat.getNativeObjAddr());

//                Mat grayMat = new Mat();
//                Imgproc.cvtColor(imgToProcess, grayMat, Imgproc.COLOR_BGR2GRAY);

                Log.d("grayMat width", "" + grayMat.cols());
                Log.d("grayMat height", "" + grayMat.rows());

                Bitmap bmpOut = Bitmap.createBitmap(grayMat.cols(),
                        grayMat.rows(), Bitmap.Config.ARGB_8888);

                Utils.matToBitmap(grayMat, bmpOut);

                Log.d("BMPout width", "" + bmpOut.getWidth());
                Log.d("BMPout height", "" + bmpOut.getHeight());

                capturePreview.setImageBitmap(bmpOut);

            }
        }
    }

    @Override
    protected void onRestart() {
        Log.d("Main-Activity", "OnRestartCalled");
        super.onRestart();
    }

    //    public native String stringFromJNI();
//    public native String validate(long madAddrGr,long matAddrRgba);
}
