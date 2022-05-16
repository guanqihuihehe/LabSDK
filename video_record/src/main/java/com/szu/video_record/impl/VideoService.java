package com.szu.video_record.impl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.szu.video_record.api.IVideoListener;
import com.szu.video_record.api.IVideoService;
import com.szu.video_record.api.VideoRecorderConfig;

public class VideoService implements IVideoService {

    private static volatile VideoService mInstance = null;
    private Context mContext;
    private VideoRecordManager mVideoRecordManager = null;

    private VideoService(Context context) {
        mContext = context;
        requestPhotoPermissions((Activity) mContext);
    }

    public static VideoService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VideoService(context);
        }
        return mInstance;
    }

    /**
     * 初始化录制
     */
    @Override
    public boolean initVideoRecord(VideoRecorderConfig config, IVideoListener videoListener) {
        mVideoRecordManager = new VideoRecordManager();
        return mVideoRecordManager.initVideoRecord(config, videoListener);
    }

    /**
     * 开始录制
     */
    @Override
    public boolean startRecord() {
        return mVideoRecordManager.startRecord();
    }

    /**
     * 停止录制
     */
    @Override
    public void stopRecord() {
        mVideoRecordManager.stopRecord();
    }

    /**
     * 暂停录制
     *
     * @return
     */
    @Override
    public boolean pause() {
        return mVideoRecordManager.pause();
    }

    /**
     * 继续录制
     *
     * @return
     */
    @Override
    public boolean resume() {
        return mVideoRecordManager.resume();
    }

    /*申请权限*/
    private static final int WRITE_EXTERNAL_STORAGE = 0;
    private static final int RECORD_AUDIO = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
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
                Manifest.permission.RECORD_AUDIO);
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    RECORD_AUDIO);
        }
    }
}
