package com.szu.audio_play;
import android.app.Activity;
import android.util.Log;

public class AudioPlayService implements IAudioPlayService {
    private static final String TAG = AudioPlayService.class.getSimpleName();
    private MediaPlayerManager mMediaPlayerManager;
    private AudioTrackManager mAudioTrackManager;
    private String mPath = null;
    private int mPlayerType = 1;//1表示使用MediaPlayer进行播放，2表示使用AudioTrack播放

    @Override
    public void initPlayer(int playerType, String filePath, int sampleRateInHz, int channelConfig, int audioEncoding, int playMode, int streamType, boolean isLooping) {
        mPath = filePath;
        mPlayerType = playerType;
        if (mPlayerType == 1) {
            mMediaPlayerManager = new MediaPlayerManager();
            mMediaPlayerManager.initAudioPlayer(mPath, streamType, isLooping);
        } else if (mPlayerType == 2){
            mAudioTrackManager = new AudioTrackManager();
            mAudioTrackManager.setConfig(mPath, sampleRateInHz, channelConfig, audioEncoding, playMode, streamType, isLooping);
            mAudioTrackManager.initAudioTrack();
        } else {
            Log.e(TAG, "选择播放器的类型不正确");
        }
    }

    @Override
    public void initPlayer(String filePath, int playerType) {
        mPath = filePath;
        mPlayerType = playerType;
        if (mPlayerType == 1) {
            mMediaPlayerManager = new MediaPlayerManager();
            mMediaPlayerManager.initAudioPlayer(mPath);
        } else if (mPlayerType == 2){
            mAudioTrackManager = new AudioTrackManager();
            mAudioTrackManager.setConfig(mPath);
            mAudioTrackManager.initAudioTrack();
        } else {
            Log.e(TAG, "选择播放器的类型不正确");
        }
    }

    @Override
    public void startEarPhonePlay(Activity activity) {
        if (mPlayerType == 1) {
            mMediaPlayerManager.startEarPhonePlay(activity);
        } else if (mPlayerType == 2){
            Log.e(TAG, "audioTrack暂未支持听筒播放");
        }
    }

    @Override
    public void startPlay() {
        if (mPlayerType == 1) {
            mMediaPlayerManager.startPlay();
        } else if (mPlayerType == 2){
            mAudioTrackManager.startPlay();
        }

    }

    @Override
    public void pausePlay() {
        if (mPlayerType == 1) {
            mMediaPlayerManager.pausePlay();
        } else if (mPlayerType == 2){
            Log.e(TAG, "audioTrack暂未支持暂停播放");
        }
    }

    @Override
    public void stopPlay() {
        mMediaPlayerManager.stopPlay();
        if (mPlayerType == 1) {
            mMediaPlayerManager.stopPlay();
        } else if (mPlayerType == 2){
            mAudioTrackManager.stopPlay();
        }
    }

    @Override
    public void restartPlay() {
        if (mPlayerType == 1) {
            mMediaPlayerManager.restartPlay();
        } else if (mPlayerType == 2){
            Log.e(TAG, "audioTrack暂未支持暂停与恢复播放");
        }
    }
}
