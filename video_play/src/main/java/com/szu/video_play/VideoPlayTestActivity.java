package com.szu.video_play;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play_test);
        VideoView videoView = (VideoView)findViewById(R.id.video_view);

        //加载指定的视频文件
        String path = getExternalFilesDir("VideoRecord").getPath()+"/1649596709662_test.mp4";
        videoView.setVideoPath(path);

        //创建MediaController对象
        MediaController mediaController = new MediaController(this);

        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);

        //让VideoView获取焦点
        videoView.requestFocus();
    }
}