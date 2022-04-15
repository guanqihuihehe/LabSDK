package com.szu.video_play;

import android.content.Context;
import android.widget.VideoView;

public interface IVideoPlayService {
    /**
     * 启动播放
     * @param path 播放视频的路径
     * @param videoView VideoView，作为视频播放的界面
     * @param context 当前的context
     * */
    void playVideo(String path, VideoView videoView, Context context);
}
