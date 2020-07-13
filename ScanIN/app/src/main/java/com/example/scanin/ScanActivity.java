package com.example.scanin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.example.scanin.ImageDataModule.ImageData;
import com.example.scanin.StateMachineModule.MachineActions;
import com.example.scanin.StateMachineModule.MachineStates;
import com.example.scanin.StateMachineModule.StateMachine;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;

public class ScanActivity extends AppCompatActivity
        implements ImageGridFragment.ImageGridFragmentCallback, ImageEditFragment.ImageEditFragmentCallback {
    public ImageGridFragment imageGridFragment = null;
    public ImageEditFragment imageEditFragment = null;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Preview preview;
    private String documentName = null;
    ImageCapture imageCapture = null;
    private File outputDirectory;
    private Camera camera = null;
    public int CurrentMachineState = -1;
    PreviewView previewView;
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    FrameLayout fragment_cover;
    CameraSelector cameraSelector;
    String TAG = "Scan-Activity";

    //Schedulers for Image and Database
    private Scheduler preview_executor = Schedulers.newThread();
    private final CompositeDisposable disposable = new CompositeDisposable();

    private ArrayList<ImageData> imageData = new ArrayList<ImageData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        int state = getIntent().getIntExtra("STATE", -1);
        int action = getIntent().getIntExtra("ACTION", -1);

        imageGridFragment = new ImageGridFragment();
        imageEditFragment = new ImageEditFragment();
        cameraProviderFuture = ProcessCameraProvider.getInstance((Context)this);
        outputDirectory = getOutputDirectory();

        if(action == MachineActions.HOME_ADD_SCAN){
            CurrentMachineState = MachineStates.CAMERA;
            startCamera();
        }else if(action == MachineActions.HOME_OPEN_DOC){
            CurrentMachineState = MachineStates.EDIT_2;
            imageEditFragment.setCurrentMachineState(CurrentMachineState);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_edit, imageEditFragment)
                    .commit();
            startCamera();
        }else if(action == MachineActions.EDIT_PDF){

        }else{
            finish();
        }

        findViewById(R.id.grid_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setClickable(false);
                int nextState = StateMachine.getNextState(CurrentMachineState, MachineActions.CAMERA_EDIT_GRID);
                imageGridFragment.setCurrentMachineState(nextState);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_grid, imageGridFragment)
                        .commit();
            }
        });

        findViewById(R.id.camera_capture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setClickable(false);
                if(imageCapture == null){
                    return;
                }
                takePhoto();
            }
        });
    }

    //start camera
    private void startCamera(){
        cameraProviderFuture.addListener(() ->{
            try{
                Log.d("Camera-1", "bindPreview1");
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                bindPreview(cameraProvider);
            }catch (ExecutionException | InterruptedException e){

                Log.e("CameraFragment", "cameraProviderFuture.get() Failed", e);
            }
        }, ContextCompat.getMainExecutor((Context)this));
    }

    public void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
//        cameraProvider.unbindAll();
        Log.d("Camera-1", "bindPreview2");
        previewView = (PreviewView) findViewById(R.id.view_finder);
        preview = new Preview.Builder()
                .setTargetResolution(new Size(480, 360))
                .build();
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(previewView.getDisplay().getRotation()).build();

        cameraSelector = new CameraSelector.Builder()
                .build();
        preview.setSurfaceProvider(previewView.createSurfaceProvider());
        this.camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
        Log.d(TAG, "Camera bind done");
    }

//    private void takePhoto(){
//        if(imageCapture == null) return;
//        File photoFile = new File(
//                outputDirectory, new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg");
//        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
//        imageCapture.takePicture(
//                outputFileOptions, ContextCompat.getMainExecutor((Context)this), new ImageCapture.OnImageSavedCallback() {
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//
//                        Uri savedUri = Uri.fromFile(photoFile);
////                        currentFile = photoFile;
////                        Bitmap myBitmap = BitmapFactory.decodeFile(currentFile.getAbsolutePath());
////                        photo_preview.setImageBitmap(myBitmap);
//                        Bitmap bitmap = readImageFromFile(savedUri);
//                        String msg = String.format("Photo capture succeeded: %s", savedUri);
//                        Toast.makeText((Context)ScanActivity.this, msg, Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, msg);
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        Log.e(TAG, "Photo capture failed: ${exc.message}", exception);
//                    }
//                }
//        );
//    }

    private void takePhoto(){
        if(imageCapture == null) return;
        File photoFile = new File(
                outputDirectory, new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(
                outputFileOptions, Executors.newSingleThreadExecutor(), new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedUri = Uri.fromFile(photoFile);
//                        int nextState = StateMachine.getNextState(CurrentMachineState, MachineActions.CAMERA_CAPTURE_PHOTO);
//                        imageEditFragment.setCurrentMachineState(nextState);
//                        FragmentManager fragmentManager = getSupportFragmentManager();
//                        fragmentManager.beginTransaction()
//                                .add(R.id.fragment_edit, imageEditFragment)
//                                .commit();
                        readImageFromFile(savedUri);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exception);
                    }
                }
        );
    }

    @NotNull
    public File getOutputDirectory() {
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

    @Override
    public void onBackPressed() {
        Log.d("Scan-ActivityBack", String.valueOf(CurrentMachineState));
        int nextState = StateMachine.getNextState(CurrentMachineState, MachineActions.BACK);

        if(nextState == MachineStates.CAMERA){
            Log.d("Scan-ActivityBack", "Opening Camera");
            getSupportFragmentManager().beginTransaction()
                    .remove(imageEditFragment)
                    .commit();
            setCamera(nextState);
            return;
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public void onCreateGridCallback() {

    }

    @Override
    public void onCreateEditCallback() {
        Log.d("onCreateEdit", "Called");
        imageEditFragment.setImagePathList(imageData);
    }

    @Override
    public void onClickEditCallback(int action) {
        int nextState = StateMachine.getNextState(CurrentMachineState, action);
        if(nextState == MachineStates.CAMERA){
            Log.d("OpenCamera", "opened");
            getSupportFragmentManager().beginTransaction()
                    .remove(imageEditFragment)
                    .commit();
            setCamera(nextState);
        }
        else {
            imageEditFragment.setCurrentMachineState(nextState);
            imageGridFragment.setCurrentMachineState(nextState);
            getSupportFragmentManager().beginTransaction()
                    .remove(imageEditFragment)
                    .add(R.id.fragment_grid, imageGridFragment)
                    .commit();
        }
    }

    @Override
    public void onClickGridCallback(int action) {
        int nextState = StateMachine.getNextState(CurrentMachineState, action);
        if(nextState == MachineStates.CAMERA){
            getSupportFragmentManager().beginTransaction()
                    .remove(imageGridFragment)
                    .commit();
            setCamera(nextState);
        }
        else{
            imageEditFragment.setCurrentMachineState(nextState);
            getSupportFragmentManager().beginTransaction()
                    .remove(imageGridFragment)
                    .add(R.id.fragment_edit, imageEditFragment)
                    .commit();
        }
    }

    private void setCamera(int nextState){
        CurrentMachineState = nextState;
        findViewById(R.id.camera_capture_button).setClickable(true);
        findViewById(R.id.grid_button).setClickable(true);
    }

    public void readImageFromFile(Uri uri){
        disposable.add(Single.create(new SingleOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(SingleEmitter<Bitmap> e) throws Exception {
                File file = new File(Objects.requireNonNull(uri.getPath()));
                if(!file.exists()) e.onSuccess(null);
                e.onSuccess(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }
        }).subscribeOn(preview_executor)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableSingleObserver<Bitmap>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    imageData.add(new ImageData(bitmap));
                    Log.d("onCreateEdit", "success");
//                    imageEditFragment.setImagePathList(imageData);
                    int nextState = StateMachine.getNextState(CurrentMachineState, MachineActions.CAMERA_CAPTURE_PHOTO);
                    imageEditFragment.setCurrentMachineState(nextState);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.fragment_edit, imageEditFragment)
                            .commit();
                }

                @Override
                public void onError(Throwable e) {

                }
            }));
    }

    public void updateImageInFile(Uri uri, Bitmap bitmap){
        File file = new File(Objects.requireNonNull(uri.getPath()));
        if(!file.exists()) return;
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteImageFromFile(Uri uri, Bitmap bitmap){
        File file = new File(Objects.requireNonNull(uri.getPath()));
        if (file.exists()) {
            return file.delete();
        }
        else return true;
    }

    @Override
    protected void onPause() {
        if(CurrentMachineState == MachineStates.CAMERA) findViewById(R.id.fragment_camera).setVisibility(View.INVISIBLE);
//        findViewById(R.id.fragment_camera).setVisibility(View.INVISIBLE);
        super.onPause();
    }
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
//        findViewById(R.id.fragment_camera).setVisibility(View.VISIBLE);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.fragment_camera).setVisibility(View.VISIBLE);
            }
        }, 600);
        super.onResume();
    }
}