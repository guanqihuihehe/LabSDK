package com.szu.video_play;

import android.content.Context;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayService implements IVideoPlayService{
    @Override
    public void playVideo(String path, VideoView videoView, Context context) {
        videoView.setVideoPath(path);

        //创建MediaController对象
        MediaController mediaController = new MediaController(context);

        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);

        //让VideoView获取焦点
        videoView.requestFocus();
    }
}
