package com.example.scanin.HomeModule;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.scanin.DatabaseModule.Document;
import com.example.scanin.DatabaseModule.DocumentPreview;
import com.example.scanin.DatabaseModule.Repository;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class HomeViewModel extends AndroidViewModel {
    private Repository mRepository;
    private LiveData<List<DocumentPreview>> mDocPreview;
    private CompositeDisposable disposable;

    public HomeViewModel(Application application){
        super(application);
        mRepository = new Repository(application);
        mDocPreview = mRepository.getDocsPreview();
        disposable = new CompositeDisposable();
    }

    LiveData<List<DocumentPreview>> getmDocPreview(){
        return mDocPreview;
    }

    void deleteDoc(DocumentPreview documentPreview){
        mRepository.deleteDocument(documentPreview.getDocument());
    }

    void updateDoc(Document document){
        mRepository.updateDocument(document, disposable);
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
