package com.example.scanin;

import android.content.Context;
import android.os.Bundle;
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
import androidx.camera.core.ImageProxy;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ScanActivity extends AppCompatActivity
        implements ImageGridFragment.ImageGridFragmentCallback, ImageEditFragment.ImageEditFragmentCallback {
    public ImageGridFragment imageGridFragment = null;
    public ImageEditFragment imageEditFragment = null;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Preview preview;
    private String documentName = null;
    ImageCapture imageCapture = null;
    private Camera camera = null;
    public int CurrentMachineState = -1;
    PreviewView previewView;
    FrameLayout fragment_cover;
    CameraSelector cameraSelector;
    String TAG = "Scan-Activity";

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
                takePhoto(imageCapture);
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

    private void takePhoto(ImageCapture imageCapture) {
        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        int nextState = StateMachine.getNextState(CurrentMachineState, MachineActions.CAMERA_CAPTURE_PHOTO);
                        imageEditFragment.setCurrentMachineState(nextState);
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.fragment_edit, imageEditFragment)
                                .commit();
                        super.onCaptureSuccess(image);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                }
        );
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

    @Override
    public void onCreateGridCallback() {

    }

    @Override
    public void onCreateEditCallback() {

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

    private void setCamera(int nextState){
        CurrentMachineState = nextState;
        findViewById(R.id.camera_capture_button).setClickable(true);
        findViewById(R.id.grid_button).setClickable(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}