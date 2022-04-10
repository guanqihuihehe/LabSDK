package com.szu.video_record;

public interface IVideoService {
    /**
     * 初始化录制
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
