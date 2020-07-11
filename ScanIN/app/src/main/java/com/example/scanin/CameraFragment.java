package com.example.scanin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    private ImageButton camera_capture_button;
    private ImageButton flash_button;
    private ImageButton grid_button;
    private ImageView photo_preview;
    private Camera camera = null;
    private Bitmap[] bitmaps = null;
    Preview preview = null;
    ImageCapture imageCapture = null;
    File currentFile = null;
    public static int IMAGE_SAVED_CALLBACK_CODE = 10;
    public static int GRID_CLICKED_CALLBACK_CODE = 12;
//    private View rootView = null;

    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private static final String TAG = "CameraXBasic";
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    OnImageClickListener onImageClickListener;

    public interface OnImageClickListener{
        void cameraFragmentCallback(int CALLBACK_CODE);
        void cameraFragmentCallback(int CALLBACK_CODE, ImageProxy[] bitmaps);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            onImageClickListener = (OnImageClickListener)context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement OnImageClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.d("CameraFragment", "OnCreateViewCalled1");
//        if(rootView != null) return rootView;
//        Log.d("CameraFragment", "OnCreateViewCalled2");
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        camera_capture_button = rootView.findViewById(R.id.camera_capture_button);
        flash_button = rootView.findViewById(R.id.flash_button);
        grid_button = rootView.findViewById(R.id.grid_button);

        if(imageCapture == null || imageCapture !=null){
            if(allPermissionsGranted()){
                startCamera(rootView);
            }
            else{
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
            outputDirectory = getOutputDirectory();
        }

        return rootView;
    }

    //start camera
    private void startCamera(View rootView){
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance((Context)getActivity());
        cameraProviderFuture.addListener(() ->{
            try{
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                bindPreview(cameraProvider, rootView);
            }catch (ExecutionException | InterruptedException e){
                Log.e(TAG, "cameraProviderFuture.get() Failed", e);
            }
        }, ContextCompat.getMainExecutor((Context)getActivity()));
    }

    //bind preview view to camera
    @SuppressLint("RestrictedApi")
    public void bindPreview(@NonNull ProcessCameraProvider cameraProvider, View rootView){
        PreviewView previewView =(PreviewView)rootView.findViewById(R.id.viewFinder);
        if(imageCapture == null){
            preview = new Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .build();
            imageCapture = new ImageCapture.Builder()
                    .setTargetRotation(previewView.getDisplay().getRotation()).build();
        }
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        preview.setSurfaceProvider(previewView.createSurfaceProvider());
        if(CameraFragment.this.camera == null){
            CameraFragment.this.camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);
        }

        flash_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageCapture == null){
                    return;
                }
                if(imageCapture.getFlashMode() == ImageCapture.FLASH_MODE_OFF) {
                    imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
                    flash_button.setImageResource(R.drawable.ic_flashon);
                }
                else{
                    imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                    flash_button.setImageResource(R.drawable.ic_flashoff);
                }
            }
        });

        camera_capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageCapture == null){
                    return;
                }
                takePhoto(imageCapture);
            }
        });

        grid_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageClickListener.cameraFragmentCallback(GRID_CLICKED_CALLBACK_CODE);
            }
        });

    }

    //take photo using camera
    private void takePhoto(ImageCapture imageCapture){
        if(imageCapture == null) return;
        File photoFile = new File(
                outputDirectory, new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                .Builder(photoFile).build();

//        imageCapture.takePicture(
//                outputFileOptions,
//                ContextCompat.getMainExecutor((Context)getActivity()),
//                new ImageCapture.OnImageSavedCallback() {
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        Uri savedUri = Uri.fromFile(photoFile);
//
//                        String msg = String.format("Photo capture succeeded: %s", savedUri);
//                        Toast.makeText((Context)getActivity(), msg, Toast.LENGTH_SHORT).show();
//                        Log.d(TAG, msg);
//                        Uri[] filenames = {savedUri};
//                        onImageClickListener.cameraFragmentCallback(IMAGE_SAVED_CALLBACK_CODE, filenames);
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        Log.e(TAG, "Photo capture failed: ${exc.message}", exception);
//                    }
//                }
        imageCapture.takePicture(Executors.newSingleThreadExecutor(),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        ImageProxy[] bitmaps = {image};
                        onImageClickListener.cameraFragmentCallback(IMAGE_SAVED_CALLBACK_CODE, bitmaps);
                        String msg = String.format("Photo capture succeeded");
                        getActivity().runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                Toast.makeText((Context)getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                        super.onCaptureSuccess(image);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        super.onError(exception);
                    }
                }
        );
    }

    //Check if all required permissions are granted or not
    private boolean allPermissionsGranted(){
        boolean allGranted = true;
        for (String requiredPermission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(), requiredPermission) != 0) {
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
//                this.startCamera();
                return;
            } else {

                Toast.makeText((Context) getActivity(), (CharSequence) "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent();
//                setResult(RESULT_CANCELED, intent);
//                this.finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @NotNull
    public File getOutputDirectory(){
        File[] mediaDir = getActivity().getExternalMediaDirs();
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
            File tempDir =getActivity().getFilesDir();
            Intrinsics.checkExpressionValueIsNotNull(tempDir, "fileDir");
            return tempDir;
        }
    }
}