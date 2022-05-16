package com.szu.audio_play.api;

import android.app.Activity;

public interface IAudioPlayService {

    /**
     * 初始化播放器
     * @param filePath 播放的音乐文件路径
     * @param playerType 播放器类型，1代表使用MediaPlayer播放，2代表使用AudioTrack播放。pcm文件必须传入2，非pcm文件建议选择1。
     * @param sampleRateInHz 采样率
     * @param channelConfig 声道，传入2代表单声道，3代表立体声 //TODO 优化为1是单声道，2是双声道
     * @param audioEncoding 指定音频量化位数 ,在AudioFormat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM
     * @param playMode 播放模式
     * @param streamType 音频流类型
     * @param isLooping 是否循环播放
     * */
    void initPlayer(String filePath, int playerType, int sampleRateInHz, int channelConfig, int audioEncoding, int playMode, int streamType, boolean isLooping);

    /**
     * 初始化播放器
     * @param filePath 播放的音乐文件路径
     * @param playerType 播放器类型，1代表使用MediaPlayer播放，2代表使用AudioTrack播放。pcm文件必须传入2，非pcm文件建议选择1。
     * */
    void initPlayer(String filePath, int playerType);

    /**
     * 使用听筒开始播放
     * @param activity 当前activity
     * */
    void startEarPhonePlay(Activity activity);

    /**
     * 开始播放
     * */
    void startPlay();

    /**
     * 暂停播放
     * */
    void pausePlay();

    /**
     * 停止播放
     * */
    void stopPlay();

    /**
     * 恢复播放
     * */
    void restartPlay();
}
