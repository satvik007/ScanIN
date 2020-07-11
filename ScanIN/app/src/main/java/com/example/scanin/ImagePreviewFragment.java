package com.example.scanin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImagePreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagePreviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Bitmap bitmap = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ImagePreviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImagePreviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImagePreviewFragment newInstance(String param1, String param2) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        ImageView imageView = rootView.findViewById(R.id.image_preview);
        if(bitmap != null) imageView.setImageBitmap(bitmap);

        return rootView;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}