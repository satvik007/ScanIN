package com.example.basicpdfrenderer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Renderer extends AppCompatActivity {

    private PdfRenderer renderer;
    private PdfRenderer.Page currentPage;
    private ImageView imgPdf;
    private Button btnNextPage;
    private Button btnPrevPage;
    private ParcelFileDescriptor parcelFileDescriptor;
    private DisplayMetrics displayMetrics;
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renderer);

        imgPdf = (ImageView) findViewById(R.id.imgPdf);
        btnNextPage = (Button) findViewById(R.id.btnNextPage);
        btnPrevPage = (Button) findViewById(R.id.btnPrevPage);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (renderer != null && currentPage != null) {
                    if (view == btnNextPage) {
                        // render next page
                        renderPage(currentPage.getIndex() + 1);
                    } else if (view == btnPrevPage) {
                        // render previous page
                        renderPage(currentPage.getIndex() - 1);
                    }
                }
            }
        };

        btnNextPage.setOnClickListener(clickListener);
        btnPrevPage.setOnClickListener(clickListener);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialize();
        renderPage(1);

    }

    private void renderPage(int pageIdx){
        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = renderer.openPage(pageIdx);

        // TODO : change this to fit screen width



//        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(),
//                currentPage.getHeight(),
//                Bitmap.Config.ARGB_8888);

        // height maintaining aspect ratio
        int adjustedHeight = (int) (((float)screenWidth) / currentPage.getWidth() * currentPage.getHeight());
        Bitmap bitmap = Bitmap.createBitmap(screenWidth,
                adjustedHeight,
                Bitmap.Config.ARGB_8888);

        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        imgPdf.setImageBitmap(bitmap);

        btnPrevPage.setEnabled(currentPage.getIndex() > 0);
        btnNextPage.setEnabled(currentPage.getIndex() + 1 < renderer.getPageCount());
    }

    private void initialize() {
        // TODO : rewrite this
        try {
            File temp = new File(getCacheDir(), "tempPdf.pdf");
            FileOutputStream fos = new FileOutputStream(temp);
            InputStream is = getAssets().open("sample.pdf");

            byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = is.read(buffer)) != -1)
                fos.write(buffer, 0, readBytes);

            fos.close();
            is.close();

            parcelFileDescriptor = ParcelFileDescriptor.open(temp, ParcelFileDescriptor.MODE_READ_ONLY);
            renderer = new PdfRenderer(parcelFileDescriptor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            if (currentPage != null)
                currentPage.close();
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            renderer.close();
        }
        super.onPause();

    }
}