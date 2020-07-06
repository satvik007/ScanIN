package com.example.scanin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;


public class MainActivity extends AppCompatActivity {

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
    }

    /**
    * A native method that is implemented by the 'native-lib' native library,
    * which is packaged with this application.
    */
    public native String stringFromJNI();
    public native String validate(long madAddrGr,long matAddrRgba);
}

//
//public class MainActivity extends AppCompatActivity {
//
//    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
//    }
//
//    /**
//     * A native method that is implemented by the 'native-lib' native library,
//     * which is packaged with this application.
//     */
//    public native String stringFromJNI();
//}
