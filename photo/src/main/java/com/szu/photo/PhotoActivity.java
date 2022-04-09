package com.szu.photo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    private static volatile PhotoActivity mInstance = null;
    private static String TAG = "PhotoActivity";
    private final int TAKE_PHOTO = 1;
    private final int CHOOSE_PHOTO = 2;
    private String mPhotoPath;
    private Uri mPhotoUri;
    private static IPhotoListener mPhotoListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int action = intent.getIntExtra("action", 1);
        if (action == TAKE_PHOTO) {
            String fileName = intent.getStringExtra("fileName");
            takePhoto(fileName);
        } else if(action == CHOOSE_PHOTO){
            choosePicFromAlbum();
        } else {
            Toast.makeText(this,"action不支持",Toast.LENGTH_SHORT).show();
        }
    }

    public static void setListener(IPhotoListener photoListener) {
        mPhotoListener = photoListener;
    }

    private void takePhoto(String picName) {
        // 创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(), picName);
        mPhotoPath =outputImage.toString();
        Log.d(TAG,"拍照的文件路径："+ mPhotoPath);
        try {
            if(!outputImage.exists()){
                outputImage.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT < 24) {
            mPhotoUri = Uri.fromFile(outputImage);
        } else {
            mPhotoUri = FileProvider.getUriForFile(this, "com.szu.photo.fileprovider", outputImage);
        }

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        startActivityForResult(intent, TAKE_PHOTO);// 打开相机
    }

    private void choosePicFromAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG,"拍摄的图片的uri："+mPhotoUri);
                    mPhotoListener.onTakePhotoResult(RESULT_OK, mPhotoPath);
                } else {
                    mPhotoListener.onTakePhotoResult(resultCode, null);
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                } else {
                    mPhotoListener.onChoosePhotoResult(resultCode, null);
                }
                break;
            default:
                break;
        }
        finish();
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                //其实是以4.4之前的方式储存的uri
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        mPhotoListener.onChoosePhotoResult(RESULT_OK, imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        mPhotoListener.onChoosePhotoResult(RESULT_OK, imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        Log.d(TAG,"内存的图片的路径："+path);
        return path;
    }
}