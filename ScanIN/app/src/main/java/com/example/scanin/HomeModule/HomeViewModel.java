package com.example.scanin.HomeModule;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.scanin.DatabaseModule.DocumentPreview;
import com.example.scanin.DatabaseModule.Repository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<List<DocumentPreview>> mDocPreview;

    public HomeViewModel(Application application){
        super(application);
        mRepository = new Repository(application);
        mDocPreview = mRepository.getDocsPreview();
    }

    LiveData<List<DocumentPreview>> getmDocPreview(){
        return mDocPreview;
    }
}
