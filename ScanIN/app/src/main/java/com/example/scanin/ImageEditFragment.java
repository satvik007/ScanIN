package com.example.scanin;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.DocumentAndImageInfo;
import com.example.scanin.ImageDataModule.ImageData;
import com.example.scanin.StateMachineModule.MachineActions;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageEditFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "EDIT_FRAG";
    private View rootView;
    private View mainView;
    private View cropView;
    private PolygonView polygonView;
    private ProgressBar progressBar;
    private int imgPosition;
    private ImageData currentImg;
    private ImageView cropImageView;
//    private LinearSnapHelper pagerSnapHelper;
    private PagerSnapHelper pagerSnapHelper;

    private DocumentAndImageInfo documentAndImageInfo;
    RecyclerViewEditAdapter mAdapter = null;
    int CurrentMachineState = -1;
    Integer adapterPosition=0;
    RecyclerView recyclerView;

    private OnClickListener mainCrop = new OnClickListener() {
        @Override
        public void onClick(View view) {
//            View currentView = pagerSnapHelper.findSnapView(Objects.requireNonNull(recyclerView.getLayoutManager()));
//            if(currentView == null) return;
//            Integer pos = recyclerView.getLayoutManager().getPosition(currentView);
//            if(pos == null) return;
//            Uri uri = documentAndImageInfo.getImages().get(pos).getUri();

            mainView.setVisibility(View.GONE);
            cropView.setVisibility(View.VISIBLE);
//            showProgressBar();
//
//            currentImg = new ImageData(uri);
//            try {
//                hideProgressBar();
//                Bitmap bmp = currentImg.getSmallOriginalImage(cropImageView.getContext());
//                cropImageView.setImageBitmap(bmp);
//            } catch (Exception e) {
//                Log.d(getTag(), "IO ERROR in loading image in crop");
//            }
        }
    };

    private OnClickListener cropApply = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO
//            Map<Integer, PointF> points = polygonView.getPoints();
            cropView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }
    };

    private OnClickListener cropBack = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO
            cropView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }
    };

    private OnClickListener cropAutoDetect = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO

        }
    };

    private OnClickListener cropNoCrop = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO

        }
    };

    private OnClickListener cropRotate = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // TODO

        }
    };

    private void setViewInteract(View view, boolean canDo) {
        view.setEnabled(canDo);
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setViewInteract(((ViewGroup) view).getChildAt(i), canDo);
            }
        }
    }

    protected void showProgressBar() {
        //setViewInteract(rootView, false);
        progressBar.setVisibility(View.VISIBLE);
    };

    protected void hideProgressBar() {
        //setViewInteract(rootView, false);
        progressBar.setVisibility(View.GONE);
    };

    public ImageEditFragment() {
        // Required empty public constructor
    }

    ImageEditFragment.ImageEditFragmentCallback imageEditFragmentCallback;

    public interface ImageEditFragmentCallback{
        void onCreateEditCallback();
        void onClickEditCallback(int action);
        void editDeleteImageCallback(int position);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            imageEditFragmentCallback = (ImageEditFragment.ImageEditFragmentCallback) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement imageEditFragmentCallback");
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageEditFragment newInstance(String param1, String param2) {
        ImageEditFragment fragment = new ImageEditFragment();
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
        Log.d(TAG, "onCreateCalled");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image_edit, container, false);
        ((ScanActivity)getActivity()).CurrentMachineState = this.CurrentMachineState;

        cropView = rootView.findViewById(R.id.rlContainer);
        mainView = rootView.findViewById(R.id.edit_main);
        cropView.setVisibility(View.GONE);
        polygonView = rootView.findViewById(R.id.polygonView);
        progressBar = rootView.findViewById(R.id.progressBar);
        cropImageView = rootView.findViewById(R.id.cropImageView);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview_image);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        SpeedyLinearLayoutManager layoutManager = new SpeedyLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewEditAdapter(null, (ScanActivity) getActivity());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == RecyclerView.SCROLL_STATE_IDLE){
//                    Integer position = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
//                    setCurrentAdapterPosition(position);
//                }
//            }
//        });

//        pagerSnapHelper = new LinearSnapHelper();
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);

        rootView.findViewById(R.id.edit_add_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setClickable(false);
                imageEditFragmentCallback.onClickEditCallback(MachineActions.EDIT_ADD_MORE);
            }
        });

        rootView.findViewById(R.id.reorder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setClickable(false);
                imageEditFragmentCallback.onClickEditCallback(MachineActions.REORDER);
            }
        });

        rootView.findViewById(R.id.discard).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mAdapter.getItemCount() >= 0){
                    View currentView = pagerSnapHelper.findSnapView(layoutManager);
                    if(currentView == null) return;
                    adapterPosition = layoutManager.getPosition(currentView);
                    imageEditFragmentCallback.editDeleteImageCallback(adapterPosition);
                }
            }
        });

        ImageButton btnMainCrop = rootView.findViewById(R.id.crop);
        Button btnCropApply = rootView.findViewById(R.id.crop_apply);
        Button btnCropBack = rootView.findViewById(R.id.crop_back);
        Button btnCropAutoDetect = rootView.findViewById(R.id.crop_auto_detect);
        Button btnCropNoCrop = rootView.findViewById(R.id.crop_no_crop);
        Button btnCropRotate = rootView.findViewById(R.id.crop_rotate);

        btnMainCrop.setOnClickListener(mainCrop);
        btnCropApply.setOnClickListener(cropApply);
        btnCropBack.setOnClickListener(cropBack);
        btnCropAutoDetect.setOnClickListener(cropAutoDetect);
        btnCropNoCrop.setOnClickListener(cropNoCrop);
        btnCropRotate.setOnClickListener(cropRotate);

        imageEditFragmentCallback.onCreateEditCallback();
//        Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition((int)imageData.size() - 1);
//        recyclerView.scrollToPosition((int)imageData.size() - 1);
//        recyclerView.post(() -> {
//            View view = layoutManager.findViewByPosition((int)imageData.size() - 1);
//            if (view == null) {
//                Log.e("Snapping:", "Cant find target View for initial Snap");
//                return;
//            }
//
//            int[] snapDistance = pagerSnapHelper.calculateDistanceToFinalSnap(layoutManager, view);
//            assert snapDistance != null;
//            if (snapDistance[0] != 0 || snapDistance[1] != 0) {
//                recyclerView.scrollBy(snapDistance[0], snapDistance[1]);
//            }
//         });
        return rootView;
    }

    public void setImagePathList(DocumentAndImageInfo documentAndImageInfo) {
        this.documentAndImageInfo = documentAndImageInfo;
        mAdapter.setmDataset(documentAndImageInfo);
    }

    public void setCurrentAdapterPosition(Integer position){
        adapterPosition = position;
    }

    public void setCurrentMachineState(int currentMachineState) {
        this.CurrentMachineState = currentMachineState;
    }

    @Override
    public void onDestroyView() {
        mAdapter.notifyDataSetChanged();
        super.onDestroyView();
    }
}