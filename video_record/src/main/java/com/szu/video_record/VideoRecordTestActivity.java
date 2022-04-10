package com.szu.video_record;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import java.io.File;

public class VideoRecordTestActivity extends AppCompatActivity {

    public static String TAG = "VideoRecordActivity";
    TextureView tvShowVideo;
    IVideoService mVideoService;
    VideoRecorderConfig mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);
        tvShowVideo = findViewById(R.id.tv_show_video);
        initVideoRecord();

        findViewById(R.id.start_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initVideoRecord();
                mVideoService.initVideoRecord(mConfig, new IVideoListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Log.e(TAG, "录制失败:" + errorCode + " errorMag:" + errorMsg);
                    }
                });
                boolean start = mVideoService.startRecord();
                Log.d(TAG, "开始录制" + start);
            }
        });

        findViewById(R.id.stop_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVideoService.stopRecord();
                Log.d(TAG, "停止录制");
            }
        });

        findViewById(R.id.pause_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean pause = mVideoService.pause();
                Log.e(TAG, "暂停录制" + pause);
            }
        });

        findViewById(R.id.resume_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean resume = mVideoService.resume();
                Log.e(TAG, "重新开始录制" + resume);
            }
        });
    }

    public void initVideoRecord() {
        mConfig = new VideoRecorderConfig();
        String path = this.getExternalFilesDir(null).getAbsolutePath() + File.separator + "VideoRecord" + File.separator + System.currentTimeMillis() +"_test.mp4";
        mConfig.setCamera(Camera.open());
        mConfig.setCameraRotation(90);
        mConfig.setVideoWidth(640);
        mConfig.setVideoHeight(480);
        mConfig.setPath(path);
        mConfig.setSurfaceTexture(tvShowVideo.getSurfaceTexture());
        mConfig.setCameraId(0);
        mVideoService = VideoService.getInstance(this);
    }
}