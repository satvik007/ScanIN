package com.example.scanin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanin.DatabaseModule.DocumentAndImageInfo;
import com.example.scanin.DatabaseModule.ImageInfo;
import com.example.scanin.ImageDataModule.ContrastFilterTransformation1;
import com.example.scanin.ImageDataModule.FilterTransformation;
import com.example.scanin.ImageDataModule.ImageData;
import com.example.scanin.ImageDataModule.ImageEditUtil;
import com.example.scanin.StateMachineModule.MachineActions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static com.example.scanin.ImageDataModule.ImageData.rotateBitmap;
import static com.example.scanin.ImageDataModule.ImageEditUtil.convertArrayList2Map;
import static com.example.scanin.ImageDataModule.ImageEditUtil.convertMap2ArrayList;
import static com.example.scanin.ImageDataModule.ImageEditUtil.getDefaultPoints;
import static com.example.scanin.ImageDataModule.ImageEditUtil.rotateCropPoints;
import static com.example.scanin.ImageDataModule.ImageEditUtil.scalePoints;

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
    private FrameLayout holderImageCrop;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "EDIT_FRAG";
    private View rootView;
    private View mainView;
    private View cropView;
    private PolygonView polygonView;
    private ProgressBar progressBar;
    private ImageData currentImg;
    private ImageView cropImageView;
    private HorizontalScrollView filterContainer;
//    private LinearSnapHelper pagerSnapHelper;
    private PagerSnapHelper pagerSnapHelper;
    protected CompositeDisposable disposable = new CompositeDisposable();
    private Bitmap selectedImage;
    private DocumentAndImageInfo documentAndImageInfo;
    RecyclerViewEditAdapter mAdapter = null;
    int CurrentMachineState = -1;
    Integer adapterPosition=0;
    RecyclerView recyclerView;
    private int cropHeight;
    private int cropWidth;
    private int filterPreviewHeight = 100;
    private RecyclerView.LayoutManager layoutManager;
    boolean filterVisible = false;

    private void initializeCropping() {
        ViewTreeObserver vto = cropImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                // Remove after the first run so it doesn't fire forever
                cropImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                int height = cropImageView.getMeasuredHeight();
                int width = cropImageView.getMeasuredWidth();

                Log.d("Dimension", "MainCrop: " + width + " - " + height);

                Map<Integer, PointF> pointFs = convertArrayList2Map(currentImg.getCropPosition());
                if (pointFs == null) {
                    Log.d("ImageEditFragment", "pointFs is null");
                }
                try {
                    // if nothing in database
                    if (pointFs == null) {
                        ArrayList<Point> points = currentImg.getBestPoints();
                        currentImg.setCropPosition(points);
                        pointFs = convertArrayList2Map(points);
                    // if database has cropPoints in orig config. Convert to the current rotation config.
                    } else {
                        int rotValue = 0;
                        int rotationConfig = currentImg.getRotationConfig();
                        while (rotValue != rotationConfig) {
                            pointFs = rotateCropPoints(pointFs, currentImg.getWidth(), currentImg.getHeight(), rotValue);
                            rotValue = (rotValue + 1) % 4;
                        }
                    }
                    double scale = currentImg.getScale(width, height);
                    pointFs = scalePoints(pointFs, (float) scale);

                    polygonView.setVisibility(View.VISIBLE);

                    int padding = (int) getResources().getDimension(R.dimen.scanPadding);

                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width + 2 * padding, height + 2 * padding);
                    layoutParams.gravity = Gravity.CENTER;

                    polygonView.setLayoutParams(layoutParams);
                    polygonView.setPointColor(getResources().getColor(R.color.colorPrimary));
                    polygonView.setPoints(pointFs);
                    polygonView.invalidate();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });


    }

    private OnClickListener mainCrop = new OnClickListener() {
        @Override
        public void onClick(View view) {
            View currentView = pagerSnapHelper.findSnapView(layoutManager);
            if(currentView == null) return;
            adapterPosition = layoutManager.getPosition(currentView);
            ImageInfo imgInfo = documentAndImageInfo.getImages().get(adapterPosition);

            Log.d (getTag(), imgInfo.getUri().toString());

            mainView.setVisibility(View.GONE);
            cropView.setVisibility(View.VISIBLE);
            currentImg = new ImageData(imgInfo);

            try {
                currentImg.setOriginalBitmap(cropImageView.getContext());
                currentImg.setOriginalBitmap(rotateBitmap(currentImg.getOriginalBitmap(),
                        90.0f * imgInfo.getRotationConfig()));
                selectedImage = currentImg.getSmallOriginalImage(cropImageView.getContext());
                hideProgressBar();
                cropImageView.setImageBitmap(selectedImage);
            } catch (Exception e) {
                Log.e(getTag(), "IO ERROR in loading image in crop");
            }
            initializeCropping();
        }
    };

    private OnClickListener cropApply = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Map <Integer, PointF> points = polygonView.getPoints();
            int width = cropImageView.getMeasuredWidth();
            int height = cropImageView.getMeasuredHeight();
            double scale = currentImg.getScale(width, height);
            points = scalePoints(points, (float) (1.0 / scale));

            // Before storing the crop points must be converted for the original configuration.
            // This is done because rotating in edit mode (not crop edit) mode will now not
            // require rotating crop Points.
            int rotationConfig = currentImg.getRotationConfig();
            int origWidth = currentImg.getWidth();
            int origHeight = currentImg.getHeight();

            while (rotationConfig != 0) {
                points = rotateCropPoints(points, origWidth, origHeight, rotationConfig);
                rotationConfig = (rotationConfig + 1) % 4;
            }

            // The crop position scale is according to the image that was loaded in ImageData.
            documentAndImageInfo.getImages().get(adapterPosition).setCropPositionMap(points);
            documentAndImageInfo.getImages().get(adapterPosition).setRotationConfig(currentImg.getRotationConfig());
            mAdapter.notifyDataSetChanged();

            cropView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }
    };

    private OnClickListener cropBack = new OnClickListener() {
        @Override
        public void onClick(View view) {
            cropView.setVisibility(View.GONE);
            mainView.setVisibility(View.VISIBLE);
        }
    };

    private OnClickListener cropAutoDetect = new OnClickListener() {
        @Override
        public void onClick(View view) {
            showProgressBar();
            int height = cropImageView.getMeasuredHeight();
            int width = cropImageView.getMeasuredWidth();

            Log.i("Dimension", "Auto Detect: " + width+" - "+height);

            double scale = currentImg.getScale(width, height);
            ArrayList<Point> points = currentImg.getBestPoints();
            currentImg.setCropPosition(points);
            Map<Integer, PointF> pointFs = convertArrayList2Map(points);
            pointFs = scalePoints(pointFs, (float) scale);
            polygonView.setPoints(pointFs);
            polygonView.invalidate();
            hideProgressBar();
        }
    };

    private OnClickListener cropNoCrop = new OnClickListener() {
        @Override
        public void onClick(View view) {
            showProgressBar();
            int width = cropImageView.getMeasuredWidth();
            int height = cropImageView.getMeasuredHeight();

            Log.i("Dimension", "No Crop: " + width+" - "+height);

            Map <Integer, PointF> default_points = getDefaultPoints (width, height);
            polygonView.setPoints(default_points);
            polygonView.invalidate();
            hideProgressBar();
        }
    };

    private OnClickListener cropRotate = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Map <Integer, PointF> points = polygonView.getPoints();
            int width = cropImageView.getMeasuredWidth();
            int height = cropImageView.getMeasuredHeight();
            double scale = currentImg.getScale(width, height);
            points = scalePoints(points, (float) (1.0 / scale));
            ArrayList <Point> points_ar = convertMap2ArrayList(points);
            currentImg.setCropPosition(points_ar);
            currentImg.rotateBitmap();
            selectedImage = currentImg.getSmallOriginalImage(cropImageView.getContext());
            cropImageView.setImageBitmap(selectedImage);

            ViewTreeObserver vto = cropImageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    // Remove after the first run so it doesn't fire forever
                    cropImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int height = cropImageView.getMeasuredHeight();
                    int width = cropImageView.getMeasuredWidth();

                    Log.d("Dimension", "MainCrop: " + width + " - " + height);

                    double scale = currentImg.getScale(width, height);
                    try {
                        Map <Integer, PointF> pointFs = convertArrayList2Map(currentImg.getCropPosition());
                        pointFs = scalePoints(pointFs, (float) scale);

                        polygonView.setPoints(pointFs);
                        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width + 2 * padding, height + 2 * padding);
                        layoutParams.gravity = Gravity.CENTER;
                        polygonView.setLayoutParams(layoutParams);
                        polygonView.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            });
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

    @SuppressLint("CheckResult")
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
        cropImageView = (ImageView) rootView.findViewById(R.id.cropImageView);
        holderImageCrop = rootView.findViewById(R.id.holderImageCrop);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview_image);
        filterContainer = (HorizontalScrollView)rootView.findViewById(R.id.filter_scroll_view);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
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
        pagerSnapHelper = new PagerSnapHelper();
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

        HorizontalScrollView filter_scroll_view = rootView.findViewById(R.id.filter_scroll_view);
        ImageView original_filter_view = rootView.findViewById(R.id.original_filter);
        ImageView magic_filter_view = rootView.findViewById(R.id.magic_filter);
        ImageView sharpen_filter_view = rootView.findViewById(R.id.sharpen_filter);
        ImageView gray_filter_view = rootView.findViewById(R.id.gray_filter);
        ImageView dark_magic_filter_view = rootView.findViewById(R.id.dark_magic_filter);

        filterContainer.setOnFocusChangeListener((view, b) -> {
            Log.d(TAG, "HEre foucus"+String.valueOf(b));
            if(!b) filterContainer.setVisibility(View.INVISIBLE);
        });


        rootView.findViewById(R.id.filters).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (filterVisible) {
                    filterVisible = false;
                    filter_scroll_view.setVisibility(View.GONE);
                } else {
                    filter_scroll_view.setVisibility(View.VISIBLE);
                    View currentView = pagerSnapHelper.findSnapView(layoutManager);
                    if(currentView == null) return;

                    adapterPosition = layoutManager.getPosition(currentView);
                    Picasso.get().load(documentAndImageInfo.getImages().get(adapterPosition).getUri())
                            .transform(new FilterTransformation("original_filter"))
                            .fit()
                            .centerCrop()
                            .into(original_filter_view);
                    Picasso.get().load(documentAndImageInfo.getImages().get(adapterPosition).getUri())
                            .transform(new FilterTransformation("magic_filter"))
                            .fit()
                            .centerCrop()
                            .into(magic_filter_view);
                    Picasso.get().load(documentAndImageInfo.getImages().get(adapterPosition).getUri())
                            .transform(new FilterTransformation("sharpen_filter"))
                            .fit()
                            .centerCrop()
                            .into(sharpen_filter_view);
                    Picasso.get().load(documentAndImageInfo.getImages().get(adapterPosition).getUri())
                            .transform(new FilterTransformation("dark_magic_filter"))
                            .fit()
                            .centerCrop()
                            .into(dark_magic_filter_view);
                    Picasso.get().load(documentAndImageInfo.getImages().get(adapterPosition).getUri())
                            .transform(new FilterTransformation("gray_filter"))
                            .fit()
                            .centerCrop()
                            .into(gray_filter_view);
                    filterVisible = true;
                }
            }
        });

        original_filter_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                View currentView = pagerSnapHelper.findSnapView(layoutManager);
                if(currentView == null) return;
                adapterPosition = layoutManager.getPosition(currentView);
                documentAndImageInfo.getImages().get(adapterPosition).setFilterId(
                        ImageEditUtil.getFilterId("original_filter"));
                mAdapter.notifyDataSetChanged();
            }
        });

        magic_filter_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                View currentView = pagerSnapHelper.findSnapView(layoutManager);
                if(currentView == null) return;
                adapterPosition = layoutManager.getPosition(currentView);
                documentAndImageInfo.getImages().get(adapterPosition).setFilterId(
                        ImageEditUtil.getFilterId("magic_filter"));
                mAdapter.notifyDataSetChanged();
            }
        });

        sharpen_filter_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                View currentView = pagerSnapHelper.findSnapView(layoutManager);
                if(currentView == null) return;
                adapterPosition = layoutManager.getPosition(currentView);
                documentAndImageInfo.getImages().get(adapterPosition).setFilterId(
                        ImageEditUtil.getFilterId("sharpen_filter"));
                mAdapter.notifyDataSetChanged();
            }
        });

        gray_filter_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                View currentView = pagerSnapHelper.findSnapView(layoutManager);
                if(currentView == null) return;
                adapterPosition = layoutManager.getPosition(currentView);
                documentAndImageInfo.getImages().get(adapterPosition).setFilterId(
                        ImageEditUtil.getFilterId("gray_filter"));
                mAdapter.notifyDataSetChanged();
            }
        });

        dark_magic_filter_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                View currentView = pagerSnapHelper.findSnapView(layoutManager);
                if(currentView == null) return;
                adapterPosition = layoutManager.getPosition(currentView);
                documentAndImageInfo.getImages().get(adapterPosition).setFilterId(ImageEditUtil.getFilterId("dark_magic_filter"));
                mAdapter.notifyDataSetChanged();
            }
        });

        rootView.findViewById(R.id.rotate).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                View currentView = pagerSnapHelper.findSnapView(layoutManager);
                if(currentView == null) return;
                adapterPosition = layoutManager.getPosition(currentView);
                documentAndImageInfo.getImages().get(adapterPosition).incrementRotationConfig();
                mAdapter.notifyDataSetChanged();
            }
        });

        SeekBar brightnessBar = rootView.findViewById(R.id.brightness_bar);
        Observable.create(s->{
            brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    s.onNext(i);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        })
        .throttleLatest(100, TimeUnit.MILLISECONDS)
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(pos->{
            int i = (int) pos;
            View currentView = pagerSnapHelper.findSnapView(layoutManager);
            if(currentView == null) return;
            adapterPosition = layoutManager.getPosition(currentView);
            float beta = (float)(i+100)/100.0f;
            documentAndImageInfo.getImages().get(adapterPosition).setBeta(beta);
            ImageInfo imageInfo = documentAndImageInfo.getImages().get(adapterPosition);
            ImageView temp = currentView.findViewById(R.id.image_edit_item);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bmp1, Picasso.LoadedFrom from) {
//                Bitmap newBitmap = ImageData.changeContrastAndBrightness(bmp1, (float)beta, 0);
                    ContrastFilterTransformation1 t = new ContrastFilterTransformation1(Objects.requireNonNull(getActivity()), (float)beta);
//                    Bitmap bmp2 = bmp1.copy(bmp1.getConfig(), true);
                    Bitmap newBitmap = t.transform(bmp1);
                    temp.setImageBitmap(newBitmap);
                    Log.d("Brightnsd", String.valueOf(i));
                    Log.d("Brightns", String.valueOf(beta));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            temp.setTag(target);
            int size = (int) Math.ceil(Math.sqrt(RecyclerViewEditAdapter.MAX_WIDTH * RecyclerViewEditAdapter.MAX_HEIGHT));
            Picasso.get().load(imageInfo.getUri())
                    .transform(new FilterTransformation(ImageEditUtil.getFilterName(imageInfo.getFilterId())))
                    .resize(size, size)
                    .centerInside()
                    .into(target);
        });
//        Observable.create(s->{
//            brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                    s.onNext(i);
//                }
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {}
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {}
//            });
//        })
//        .throttleLatest(100, TimeUnit.MILLISECONDS)
//        .subscribeOn(AndroidSchedulers.mainThread())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(pos->{
//            int i = (int) pos;
//            View currentView = pagerSnapHelper.findSnapView(layoutManager);
//            if(currentView == null) return;
//            adapterPosition = layoutManager.getPosition(currentView);
//            float beta = (float)(i)/500.0f;
//            documentAndImageInfo.getImages().get(adapterPosition).setBeta(beta);
//            ImageInfo imageInfo = documentAndImageInfo.getImages().get(adapterPosition);
//            ImageView temp = currentView.findViewById(R.id.image_edit_item);
//            Target target = new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bmp1, Picasso.LoadedFrom from) {
////                Bitmap newBitmap = ImageData.changeContrastAndBrightness(bitmap, 1.5, imageInfo.getBeta());
//                    BrightnessFilterTransformation1 t = new BrightnessFilterTransformation1(Objects.requireNonNull(getActivity()), (float)beta);
////                    Bitmap bmp2 = bmp1.copy(bmp1.getConfig(), true);
//                    Bitmap newBitmap = t.transform(bmp1);
//                    temp.setImageBitmap(newBitmap);
//                    Log.d("Brightnsd", String.valueOf(i));
//                    Log.d("Brightns", String.valueOf(beta));
//                }
//
//                @Override
//                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//            };
//            temp.setTag(target);
//            int size = (int) Math.ceil(Math.sqrt(RecyclerViewEditAdapter.MAX_WIDTH * RecyclerViewEditAdapter.MAX_HEIGHT));
//            Picasso.get().load(imageInfo.getUri())
//                .transform(new FilterTransformation(ImageEditUtil.getFilterName(imageInfo.getFilterId())))
//                .resize(size, size)
//                .centerInside()
//                .into(target);
//        });

//        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                View currentView = pagerSnapHelper.findSnapView(layoutManager);
//                if(currentView == null) return;
//                adapterPosition = layoutManager.getPosition(currentView);
//                double beta = (double)(i)/100.0f;
//                Log.d("Brightnsd", String.valueOf(i));
//                Log.d("Brightns", String.valueOf(beta));
//                documentAndImageInfo.getImages().get(adapterPosition).setBeta(beta);
//                mAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

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
        disposable.clear();
    }
}