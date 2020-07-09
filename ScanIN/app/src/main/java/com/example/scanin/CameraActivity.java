package com.example.scanin;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;


public class CameraActivity extends AppCompatActivity {
    private Preview preview = null;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture = null;
    private ImageAnalysis imageAnalyzer = null;
    private Camera camera = null;
    private ImageButton camera_capture_button;
    private Button flash_button;
    private ImageView photo_preview;
    private Bitmap[] bitmaps = null;
    File currentFile = null;

    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private static final String TAG = "CameraXBasic";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //get required views
        camera_capture_button = findViewById(R.id.camera_capture_button);
        flash_button = findViewById(R.id.flash_button);

        //Check for permissions
        if(allPermissionsGranted()){
            startCamera();
        }
        else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        outputDirectory = getOutputDirectory();

        camera_capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        flash_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageCapture == null){
                    return;
                }
                if(imageCapture.getFlashMode() == ImageCapture.FLASH_MODE_OFF) imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
                else imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
            }
        });
    }

    //start camera
    private void startCamera(){
        cameraProviderFuture = ProcessCameraProvider.getInstance((Context)this);
        cameraProviderFuture.addListener(() ->{
            try{
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                bindPreview(cameraProvider);
            }catch (ExecutionException | InterruptedException e){
                Log.e(TAG, "cameraProviderFuture.get() Failed", e);
            }
        }, ContextCompat.getMainExecutor((Context)this));
    }

    //bind preview view to camera
    public void bindPreview(@NonNull ProcessCameraProvider cameraProvider){
        preview = new Preview.Builder()
                .build();
        PreviewView previewView =(PreviewView)CameraActivity.this.findViewById(R.id.viewFinder);
        imageCapture = new ImageCapture.Builder().setTargetRotation(previewView.getDisplay().getRotation()).build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        preview.setSurfaceProvider(previewView.createSurfaceProvider());
        CameraActivity.this.camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);
    }

    //take photo using camera
    private void takePhoto(){
        if(imageCapture == null) return;
        File photoFile = new File(
                outputDirectory, new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(
                outputFileOptions, ContextCompat.getMainExecutor((Context)this), new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                        Uri savedUri = Uri.fromFile(photoFile);
//                        currentFile = photoFile;
//                        Bitmap myBitmap = BitmapFactory.decodeFile(currentFile.getAbsolutePath());
//                        photo_preview.setImageBitmap(myBitmap);
                        String msg = String.format("Photo capture succeeded: %s", savedUri);
                        Toast.makeText((Context)CameraActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exception);
                    }
                }
        );
    }

    //Check if all required permissions are granted or not
    private boolean allPermissionsGranted(){
        boolean allGranted = true;
        for (String requiredPermission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this.getBaseContext(), requiredPermission) != 0) {
                allGranted = false;
                break;
            }
        }
        return allGranted;
    }

    //Users response when asked for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (this.allPermissionsGranted()) {
                this.startCamera();
            } else {
                Toast.makeText((Context) this, (CharSequence) "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @NotNull
    public File getOutputDirectory(){
        File[] mediaDir = getExternalMediaDirs();
        Intrinsics.checkExpressionValueIsNotNull(mediaDir, "externalMediaDirs");
        File fileDir = (File) ArraysKt.firstOrNull(mediaDir);
        if(fileDir != null){
            File tempDir =fileDir;
            File tempDir1 = (new File(tempDir, String.valueOf(R.string.app_name)));
            tempDir1.mkdirs();
            fileDir = tempDir1;
        }

        if(fileDir != null && fileDir.exists()){
            return fileDir;
        }
        else{
            File tempDir =this.getFilesDir();
            Intrinsics.checkExpressionValueIsNotNull(tempDir, "fileDir");
            return tempDir;
        }
    }
}