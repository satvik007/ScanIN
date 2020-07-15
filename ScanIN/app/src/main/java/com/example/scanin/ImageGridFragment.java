package com.example.scanin;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.DocumentAndImageInfo;
import com.example.scanin.StateMachineModule.MachineActions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageGridFragment extends Fragment implements RecyclerViewGridAdapter.GridAdapterOnClickHandler {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "GRID_FRAG";
    private DocumentAndImageInfo documentAndImageInfo;
    RecyclerViewGridAdapter mAdapter = null;
    int CurrentMachineState = -1;

    public ImageGridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageGridFragment newInstance(String param1, String param2) {
        ImageGridFragment fragment = new ImageGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ImageGridFragmentCallback imageGridFragmentCallback;

    public interface ImageGridFragmentCallback{
        void onCreateGridCallback();
        void onClickGridCallback(int action);
        void onClickGridCallback(int action, int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d(TAG, "onCreateCalled");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            imageGridFragmentCallback = (ImageGridFragmentCallback) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement imageGridFragmentCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image_grid, container, false);
        ((ScanActivity)getActivity()).CurrentMachineState = this.CurrentMachineState;
        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview_grid);
        recyclerView.setHasFixedSize(true);

        //use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        //set adapter
        mAdapter = new RecyclerViewGridAdapter(documentAndImageInfo, this);
        recyclerView.setAdapter(mAdapter);
        imageGridFragmentCallback.onCreateGridCallback();

//        rootView.findViewById(R.id.temp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                rootView.setClickable(false);
//                imageGridFragmentCallback.onClickGridCallback(MachineActions.GRID_ADD_SCAN);
//            }
//        });
//
//        rootView.findViewById(R.id.temp_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                rootView.setClickable(false);
//                imageGridFragmentCallback.onClickGridCallback(MachineActions.GRID_ON_CLICK);
//            }
//        });

        return rootView;
    }

    @Override
    public void onClick(int position) {
//        ScanActivity scanActivity = (ScanActivity)getActivity();
//        scanActivity.imageEditFragment.setImagePathList(documentAndImageInfo);
        imageGridFragmentCallback.onClickGridCallback(MachineActions.GRID_ON_CLICK, position);
    }

    @Override
    public void onDestroyView() {
        Log.d("Scan-Activity2", "imageFragmentDestroyed");
        super.onDestroyView();
    }

    public void setImagePathList(DocumentAndImageInfo documentAndImageInfo) {
        this.documentAndImageInfo = documentAndImageInfo;
        mAdapter.setmDataset(documentAndImageInfo);
    }

    public void setCurrentMachineState(int currentMachineState) {
        this.CurrentMachineState = currentMachineState;
    }
}