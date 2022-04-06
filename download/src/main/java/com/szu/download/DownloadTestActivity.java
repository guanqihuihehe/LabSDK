package com.szu.download;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class DownloadTestActivity extends AppCompatActivity {

    public int mDownLoadID;
    public static String TAG = "DownloadTestActivity";
    IDownloadService mDownloadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_test);
        requestDownloadPermissions(this);
        mDownloadService = new DownloadService();
        initUI();

    }

    public void initUI() {
        findViewById(R.id.start_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://6e33bb7b8f9f4eb629aae64a54b18359.rdt.tfogc.com:49156/dldir1.qq.com/weixin/android/weixin8021android2120_arm64.apk?mkey=624cafd0347a2a8cb2c8b4fcefc4779b&arrive_key=357376809878&cip=218.17.40.164&proto=https";
                String fileName = "test.apk";
                String pathname = getExternalFilesDir(null).getAbsolutePath() + File.separator + fileName;

                mDownloadService.startDownload(DownloadTestActivity.this, fileName, url, pathname, 1, new IDownloadClientListener() {
                    @Override
                    public void onProgress(int progress) {

                    }

                    @Override
                    public void onStart(int downloadID) {
                        mDownLoadID = downloadID;
                        Log.e(TAG,"onStart:"+String.valueOf(downloadID));
                    }

                    @Override
                    public void onSuccess(int downloadID) {
                        Log.d(TAG,"onSuccess:"+String.valueOf(downloadID));
                    }

                    @Override
                    public void onFailed(int downloadID, int errorCode, String errorMsg) {
                        Log.d(TAG,"onFailed:"+errorMsg);
                        Toast.makeText(DownloadTestActivity.this, "errorCode:"+String.valueOf(errorCode)+"errorMsg:"+errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPaused(int result) {
                        Log.d(TAG,"onPaused:"+result);
                    }

                    @Override
                    public void onCanceled(int result) {
                        Log.d(TAG,"onCanceled:"+result);
                    }
                });
            }
        });
        findViewById(R.id.cancel_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDownloadService.cancelDownload(mDownLoadID);
            }
        });
        findViewById(R.id.pause_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDownloadService.pauseDownload(mDownLoadID);
            }
        });
        findViewById(R.id.resume_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDownloadService.resumeDownload(mDownLoadID);
            }
        });
    }

    /*申请读写文件权限*/
    private static final int WRITE_EXTERNAL_STORAGE = 0;
    private static final int READ_EXTERNAL_STORAGE = 1;
    private static final int INTERNET = 2;
    private static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    /*
     * 申请读写文件权限*/
    public static void requestDownloadPermissions(Activity activity) {
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
                Manifest.permission.INTERNET);
        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    INTERNET);
        }
    }
}