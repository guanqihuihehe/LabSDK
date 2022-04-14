package com.szu.photo_demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.szu.photo.IPhotoListener;
import com.szu.photo.IPhotoService;
import com.szu.photo.PhotoService;

public class PhotoTestActivity extends AppCompatActivity {

    private static String TAG = "PhotoTestActivity";

    private ImageView mPhotoImageView, mChosenPicImageView;
    private IPhotoService mPhotoService;
    Bitmap mPhotoBitmap, mChosenPhotoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_test);
        mPhotoImageView = (ImageView) findViewById(R.id.photo);
        mChosenPicImageView = findViewById(R.id.chosen_photo);

        mPhotoService = PhotoService.getInstance(this);

        mPhotoService.setListener(new IPhotoListener() {
            @Override
            public void onTakePhotoResult(int resultCode, String photoPath) {
                if (resultCode == RESULT_OK) {
                    Log.e(TAG,photoPath);
                    mPhotoBitmap = BitmapFactory.decodeFile(photoPath);
                    mPhotoImageView.setImageBitmap(mPhotoBitmap);
                }
            }

            @Override
            public void onChoosePhotoResult(int resultCode, String photoPath) {
                if (resultCode == RESULT_OK) {
                    Log.e(TAG,photoPath);
                    mChosenPhotoBitmap = BitmapFactory.decodeFile(photoPath);
                    mChosenPicImageView.setImageBitmap(mChosenPhotoBitmap);
                }
            }
        });

        findViewById(R.id.take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoService.takePhoto();
            }
        });

        findViewById(R.id.choose_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoService.choosePhoto();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPhotoBitmap != null) {
            mPhotoBitmap.recycle();
        }
        if (mChosenPhotoBitmap != null) {
            mChosenPhotoBitmap.recycle();
        }
    }
}