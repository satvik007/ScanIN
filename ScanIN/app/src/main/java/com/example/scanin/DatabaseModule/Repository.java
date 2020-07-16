package com.example.scanin.DatabaseModule;

import android.content.Context;

import com.example.scanin.ScanActivity;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Repository {

    public interface RepositoryCallback{
        void updateFullAdapter(DocumentAndImageInfo documentAndImageInfo);
        void addInAdapter(ImageInfo imageInfo);
    }

    private DocumentAndImageDao documentAndImageDao;
    private ImageInfoDao imageInfoDao;
    private DocumentDao documentDao;
    private RepositoryCallback repositoryCallback;
    private Scheduler insert_thread = Schedulers.single();
    private Scheduler update_image_thread = Schedulers.single();

    public Repository(ScanActivity application, Context context){
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        documentAndImageDao = appDatabase.documentAndImageDao();
        imageInfoDao = appDatabase.imageInfoDao();
        documentDao = appDatabase.documentDao();
        repositoryCallback = (RepositoryCallback)context;
    }

    public void getDocumentImageInfo(long document_id, CompositeDisposable disposable){
        disposable.add(Single.create(s->{
            DocumentAndImageInfo documentAndImageInfo = documentAndImageDao.loadDocumentAllImageInfo(document_id);
            s.onSuccess(documentAndImageInfo);
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(s->{
            repositoryCallback.updateFullAdapter((DocumentAndImageInfo) s);
        }));
    }

    public void insertImage(ImageInfo imageInfo, CompositeDisposable disposable){
        disposable.add(Single.create(s->{
            long image_id = imageInfoDao.insertImageInfo(imageInfo);
            imageInfo.setImage_id(image_id);
            s.onSuccess(imageInfo);
        }).subscribeOn(insert_thread)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(s->{
            repositoryCallback.addInAdapter((ImageInfo) s);
        }));
    }

    public void insertDocument(Document document, ImageInfo imageInfo, CompositeDisposable disposable){
        disposable.add(Single.create(s->{
            long document_id = documentDao.insertDocument(document);
            document.setDocumentId(document_id);
            imageInfo.setImg_document_id(document_id);
            long img_id = imageInfoDao.insertImageInfo(imageInfo);
            s.onSuccess(new DocumentAndImageInfo(document, imageInfo));
        }).subscribeOn(insert_thread)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(s->{
            repositoryCallback.updateFullAdapter((DocumentAndImageInfo) s);
        }));
    }

    public void updateImage(ImageInfo imageInfo){
        Completable.create(s->{
            imageInfoDao.insertImageInfo(imageInfo);
        }).subscribeOn(update_image_thread)
          .subscribe();
    }

    public void deleteDocument(Document document){
        Completable.create(s->{
            documentDao.deleteDocument(document);
            s.onComplete();
        }).subscribeOn(Schedulers.io())
        .subscribe();
    }

    public void deleteImage(ImageInfo imageInfo){
        Completable.create(s->{
            imageInfoDao.deleteImageInfo(imageInfo);
            s.onComplete();
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }
}
