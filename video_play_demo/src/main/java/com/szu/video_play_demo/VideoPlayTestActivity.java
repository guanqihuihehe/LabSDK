package com.szu.video_play_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.szu.video_play.api.IVideoPlayService;
import com.szu.video_play.impl.VideoPlayService;

public class VideoPlayTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play_test);
        VideoView videoView = (VideoView)findViewById(R.id.video_view);

        EditText editText = findViewById(R.id.video_name);


        IVideoPlayService videoPlayService = new VideoPlayService();

        findViewById(R.id.start_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputName = editText.getText().toString();
                //加载指定的视频文件
                String name;
                if (!inputName.equals("")) {
                    name = inputName;
                } else {
                    name = "1650032798192_test.mp4";
                }
                String path = getExternalFilesDir("VideoRecord").getPath() + "/" +name;
                Toast.makeText(VideoPlayTestActivity.this, path, Toast.LENGTH_SHORT).show();
                videoPlayService.playVideo(path, videoView, VideoPlayTestActivity.this);
            }
        });
    }
}