package com.szu.audio_play;

import static android.content.Context.AUDIO_SERVICE;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MediaPlayerManager {
    public MediaPlayer mMediaPlayer;
    public AudioManager mAudioManager;

    public String mFileName;
    public String mFilePath;
    public int mMode = AudioManager.MODE_NORMAL;
    public int mStreamType = AudioManager.STREAM_MUSIC;//如果需要在双麦克风的设备上只用主麦克风播放，这里设置为STREAM_NOTIFICATION
    public boolean mIsLooping = false;

    public void initAudioPlayer(String filePath) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(mStreamType);
        mMediaPlayer.setLooping(mIsLooping);//控制是否循环播放
        mFilePath = filePath;
        try {
            mMediaPlayer.setDataSource(mFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initAudioPlayer(String filePath, int streamType, boolean isLooping) {
        mStreamType = streamType;
//        mMode = mode;
        mIsLooping = isLooping;
        initAudioPlayer(filePath);
    }



    public void startEarPhonePlay(Activity activity) {
        mAudioManager = (AudioManager) activity.getSystemService(AUDIO_SERVICE);
        mAudioManager.setSpeakerphoneOn(false);
        mMode = AudioManager.MODE_IN_COMMUNICATION;
        mStreamType = AudioManager.STREAM_VOICE_CALL;
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        initAudioPlayer(mFilePath);
//        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
//                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FX_KEY_CLICK);
        doPlay();
    }


    public void startPlay() {
        doPlay();
    }

    public void pausePlay() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void stopPlay() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void restartPlay() {
        mMediaPlayer.start();
    }

    private void doPlay() {
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }
}
