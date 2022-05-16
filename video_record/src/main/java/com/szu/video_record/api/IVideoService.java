package com.szu.video_record.api;

public interface IVideoService {
    /**
     * 初始化录制
     * @param config 视频录制的各项参数配置
     * @param videoListener 视频录制的回调
     */
    boolean initVideoRecord(VideoRecorderConfig config, IVideoListener videoListener);

    /**
     * 开始录制
     */
    boolean startRecord();

    /**
     * 停止录制
     */
    void stopRecord();

    /**
     * 暂停录制
     *
     * @return
     */
    boolean pause();

    /**
     * 继续录制
     *
     * @return
     */
    boolean resume();
}
