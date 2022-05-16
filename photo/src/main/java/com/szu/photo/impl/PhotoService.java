package com.szu.photo.impl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.szu.photo.api.IPhotoListener;
import com.szu.photo.api.IPhotoService;

public class PhotoService implements IPhotoService {
    private static volatile PhotoService mInstance = null;

    private final int TAKE_PHOTO = 1;
    private final int CHOOSE_PHOTO = 2;

    private Context mContext;

    private PhotoService(Context context) {
        requestPhotoPermissions((Activity) context);
        mContext = context;
    }

    public static PhotoService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PhotoService(context);
        }
        return mInstance;
    }

    @Override
    public void setListener(IPhotoListener photoListener) {
        PhotoActivity.setListener(photoListener);
    }

    @Override
    //三星手机在使用bitmap展示照片时，可能会出现照片被旋转的问题。
    public void takePhoto() {
        String fileName = "photo_image_"+System.currentTimeMillis()+".jpg";
        takePhoto(fileName);
    }

    @Override
    public void takePhoto(String fileName) {
        Intent intent = new Intent(mContext, PhotoActivity.class);
        intent.putExtra("action", TAKE_PHOTO);
        intent.putExtra("fileName", fileName);
        mContext.startActivity(intent);
    }

    @Override
    public void choosePhoto() {
        Intent intent = new Intent(mContext, PhotoActivity.class);
        intent.putExtra("action", CHOOSE_PHOTO);
        mContext.startActivity(intent);
    }

    /*申请权限*/
    private static final int WRITE_EXTERNAL_STORAGE = 0;
    private static final int READ_EXTERNAL_STORAGE = 1;
    private static final int CAMERA = 2;
    private static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    /*
     * 申请权限*/
    private static void requestPhotoPermissions(Activity activity) {
        int permission1 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    WRITE_EXTERNAL_STORAGE);
        }

        int permission2 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    READ_EXTERNAL_STORAGE);
        }

        int permission3 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);
        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    CAMERA);
        }
    }
}
