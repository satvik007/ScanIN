package com.example.scanin.HomeModule;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.DocumentPreview;
import com.example.scanin.R;
import com.example.scanin.ScanActivity;
import com.example.scanin.StateMachineModule.MachineActions;
import com.example.scanin.StateMachineModule.MachineStates;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, DocAdapterClickListener{

    private FloatingActionButton btnTakePicture;
    private ImageButton btnSavePicture;
    private ImageView capturePreview;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    public static final int CAMERA_ACTIVITY_REQUEST_CODE = 0;
    public static final int CAMERA_IMAGE_REQUEST_CODE = 1000;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2000;

    private static String TAG="MainActivity";
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);

        RecyclerView recyclerView = findViewById(R.id.recyclerview_doc);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewDocAdapter mAdapter = new RecyclerViewDocAdapter(this);
        recyclerView.setAdapter(mAdapter);

        HomeViewModel homeViewModel;
        homeViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                            .get(HomeViewModel.class);
        homeViewModel.getmDocPreview().observe(this, new Observer<List<DocumentPreview>>() {
            @Override
            public void onChanged(List<DocumentPreview> documentPreviews) {
                mAdapter.setmDataset((ArrayList<DocumentPreview>) documentPreviews);
            }
        });

        btnTakePicture = (FloatingActionButton) findViewById(R.id.fab);
        btnTakePicture.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            view.setClickable(false);
            startCameraActivity();
        }
    }

    public void startCameraActivity(){
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("STATE", MachineStates.HOME);
        intent.putExtra("ACTION", MachineActions.HOME_ADD_SCAN);
        startActivity(intent);
    }

    @Override
    public void onClick(View view, DocumentPreview clickedItem) {
        findViewById(R.id.recyclerview_doc).setClickable(false);
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("CURRENT_DOCUMENT_ID", clickedItem.getDocument().getDocumentId());
        intent.putExtra("STATE", MachineStates.HOME);
        intent.putExtra("ACTION", MachineActions.HOME_OPEN_DOC);
        startActivity(intent);
    }

    @Override
    public void onLongClick(View view, int position) {
//        Document document = documentsAndFirstImages.get(position).getDocument();
        view.setBackgroundColor(Color.parseColor("#1C69E1"));
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
            if (resultCode == RESULT_OK) {
                return;
            }
        }
    }

    @Override
    protected void onRestart() {
        Log.d("Main-Activity", "OnRestartCalled");
        super.onRestart();
    }
}
