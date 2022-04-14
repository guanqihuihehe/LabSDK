package com.szu.audio_play_demo;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.szu.audio_play.AudioPlayService;
import com.szu.audio_play.IAudioPlayService;

import java.io.File;


public class AudioPlayTestActivity extends AppCompatActivity {
    String mPlayFileName = "";
    String mRootPath = "";
    String defaultPlayFile = "";

    EditText mPlayNameEditText;
    IAudioPlayService mAudioPlayService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play_test);
        mRootPath = getPathFromStorage();
        mAudioPlayService = new AudioPlayService();
        initUI();
    }

    public void initUI() {

        mPlayNameEditText = findViewById(R.id.play_name);
        TextView rootPathTextView = findViewById(R.id.root_path);

        rootPathTextView.setText(mRootPath);

        findViewById(R.id.play).setOnClickListener(v -> {
            setFileName();
            doAudioPlayerPlay();
        });
        findViewById(R.id.stop_play).setOnClickListener(v -> {
            doStopPlay();
        });
        findViewById(R.id.pause_play).setOnClickListener(v -> {
            mAudioPlayService.pausePlay();
        });
        findViewById(R.id.restart_play).setOnClickListener(v -> {
            mAudioPlayService.restartPlay();
        });
        findViewById(R.id.ear_phone_play).setOnClickListener(v -> {
            setFileName();
            String playPath = mRootPath + "/" + mPlayFileName;
            Toast.makeText(AudioPlayTestActivity.this, "正在播放："+playPath, Toast.LENGTH_SHORT).show();
            mAudioPlayService.initPlayer(playPath,1);
            mAudioPlayService.startEarPhonePlay(AudioPlayTestActivity.this);
        });



        findViewById(R.id.audio_track_play).setOnClickListener(v -> {
            setFileName();
            String playPath = mRootPath + "/" + mPlayFileName;
            Toast.makeText(AudioPlayTestActivity.this, "正在播放："+playPath, Toast.LENGTH_SHORT).show();

            mAudioPlayService.initPlayer(playPath,2);
            mAudioPlayService.startPlay();
        });

        findViewById(R.id.audio_track_stop).setOnClickListener(v -> {
            mAudioPlayService.stopPlay();
        });

    }

    public void setFileName() {

        String tempPlayName = mPlayNameEditText.getText().toString();
        if (!tempPlayName.equals("")) {
            mPlayFileName = tempPlayName;
        } else {
            mPlayFileName = defaultPlayFile;
        }
    }

    public void doAudioPlayerPlay () {
        String playPath = mRootPath + "/" + mPlayFileName;
        Toast.makeText(AudioPlayTestActivity.this, "正在播放："+playPath, Toast.LENGTH_SHORT).show();
        mAudioPlayService.initPlayer(playPath,1);
        mAudioPlayService.startPlay();
    }

    public void doStopPlay() {
        mAudioPlayService.stopPlay();
    }

    /**
     * 获取当前根目录的绝对路径
     */
    public String getPathFromStorage() {
        File file = this.getExternalFilesDir("Audio");
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取当前raw文件目录的绝对路径
     */
    public String getPathFromRaw() {
        return null;
    }


}