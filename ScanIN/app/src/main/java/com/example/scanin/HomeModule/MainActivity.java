package com.example.scanin.HomeModule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.AppDatabase;
import com.example.scanin.DatabaseModule.Document;
import com.example.scanin.DatabaseModule.DocumentsAndFirstImage;
import com.example.scanin.DatabaseModule.Repository;
import com.example.scanin.ImageDataModule.ImageEditUtil;
import com.example.scanin.R;
import com.example.scanin.ScanActivity;
import com.example.scanin.StateMachineModule.MachineActions;
import com.example.scanin.StateMachineModule.MachineStates;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.InputStream;
import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, DocAdapterClickListener{

    private FloatingActionButton btnTakePicture;
    private ImageButton btnSavePicture;
    private ImageView capturePreview;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    public static final int CAMERA_ACTIVITY_REQUEST_CODE = 0;
    public static final int CAMERA_IMAGE_REQUEST_CODE = 1000;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 2000;

    private AppDatabase appDatabase;
    private CompositeDisposable disposable;
    private Repository repository;

    private Bitmap bitmap;
    private ArrayList<DocumentsAndFirstImage> documentsAndFirstImages;
    private RecyclerViewDocAdapter mAdapter;

    private static String TAG="MainActivity";
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        TextView textView=(TextView) findViewById(R.id.sample_text);
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        if (OpenCVLoader.initDebug()) {
//            textView.setText(textView.getText()+"\n OPENCV LOADED SUCCESSFULLY");
//        } else {
//            Log.d(TAG, "OPENCV DİD NOT LOAD");
//        }

        appDatabase = AppDatabase.getInstance(this);
        disposable = new CompositeDisposable();
        repository = new Repository(this.getApplication(), this);

        RecyclerView recyclerView = findViewById(R.id.recyclerview_doc);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewDocAdapter(documentsAndFirstImages, this);
        recyclerView.setAdapter(mAdapter);
        loadDocsInfo();


        btnTakePicture = (FloatingActionButton) findViewById(R.id.fab);
//        btnSavePicture = (ImageButton) findViewById(R.id.open_doc);
//        capturePreview = (ImageView) findViewById(R.id.capturePreview);

        btnTakePicture.setOnClickListener(MainActivity.this);
//        btnSavePicture.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            view.setClickable(false);
            startCameraActivity();
        }
//        else if(view.getId()==R.id.open_doc){
//            Intent intent = new Intent(this, ScanActivity.class);
//            intent.putExtra("STATE", MachineStates.HOME);
//            intent.putExtra("ACTION", MachineActions.HOME_OPEN_DOC);
//            startActivity(intent);
//        }
    }

    public void loadDocsInfo(){
        disposable.add(Single.create(s->{
            s.onSuccess(appDatabase.docAndFirstImageDao().loadDocumentAllImageInfo());
        }).subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s->{
                    documentsAndFirstImages = (ArrayList<DocumentsAndFirstImage>) s;
//                    if(documentsAndFirstImages.size() == 0) return;
//                    Log.d("DocInfo", String.valueOf(documentsAndFirstImages.size()));
//                    Log.d("DocInfo0S", String.valueOf(documentsAndFirstImages.get(0).getImageInfo().getPosition()));
//                    Log.d("DocInfo1S", String.valueOf(documentsAndFirstImages.get(1).getImageInfo().getPosition()));
//                    Log.d("DocInfo1", String.valueOf(documentsAndFirstImages.get(1).getImageInfo().getUri()));
                    mAdapter.setmDataset(documentsAndFirstImages);
                }, Throwable::printStackTrace));
    }
//
//    public void loadDocsInfo(){
//        disposable.add(Single.create(s->{
////            s.onSuccess(appDatabase.docAndFirstImageDao().loadDocumentAllImageInfo());
//            s.onSuccess(appDatabase.imageInfoDao().getAllImageInfo());
//        }).subscribeOn(Schedulers.single())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s->{
////                    documentsAndFirstImages = (ArrayList<DocumentsAndFirstImage>) s;
////                    if(documentsAndFirstImages.size() == 0) return;
////                    Log.d("DocInfo", String.valueOf(documentsAndFirstImages.size()));
////                    Log.d("DocInfo0S", String.valueOf(documentsAndFirstImages.get(0).getImageInfos()));
////                    Log.d("DocInfo0", String.valueOf(documentsAndFirstImages.get(0).getFirstImage().getUri()));
//////                    Log.d("DocInfo1", String.valueOf(documentsAndFirstImages.get(1).getImageInfo().getUri()));
////                    mAdapter.setmDataset(documentsAndFirstImages);
//                    ArrayList<ImageInfo> temp = (ArrayList<ImageInfo>) s;
//                    Log.d("DbRead", "Size: "+ String.valueOf(temp.size()));
//                    if(temp.size() == 0) return;
//                    Log.d("DbRead_0", "Size: "+ String.valueOf(temp.get(0).getUri()));
//                    Log.d("DbRead_0", "docID: "+ String.valueOf(temp.get(0).getImg_document_id()));
//                    Log.d("DbRead_0", "Position: "+ String.valueOf(temp.get(0).getPosition()));
//                    Log.d("DbRead_1", "Size: "+ String.valueOf(temp.get(1).getUri()));
//                    Log.d("DbRead_1", "docID: "+ String.valueOf(temp.get(1).getImg_document_id()));
//                    Log.d("DbRead_1", "Position: "+ String.valueOf(temp.get(1).getPosition()));
//
//                }, Throwable::printStackTrace));
//    }

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
    public void onClick(View view, int position) {
        findViewById(R.id.recyclerview_doc).setClickable(false);
        DocumentsAndFirstImage clickedItem = documentsAndFirstImages.get(position);
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("CURRENT_DOCUMENT_ID", clickedItem.getDocument().getDocumentId());
        intent.putExtra("STATE", MachineStates.HOME);
        intent.putExtra("ACTION", MachineActions.HOME_OPEN_DOC);
        startActivity(intent);
    }

    @Override
    public void onLongClick(View view, int position) {
        Document document = documentsAndFirstImages.get(position).getDocument();
//        documentsAndFirstImages.remove(position);
//        mAdapter.setmDataset(documentsAndFirstImages);
//        mAdapter.notifyDataSetChanged();
//        repository.deleteDocument(document);
        view.setBackgroundColor(Color.parseColor("#1C69E1"));
    }

    @Override
    protected void onDestroy() {
        Log.d("Main-Activity", "OnDestroyCalled");
        super.onDestroy();
        disposable.dispose();
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
        loadDocsInfo();
    }

    //    public native String stringFromJNI();
//    public native String validate(long madAddrGr,long matAddrRgba);
}
