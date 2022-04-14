package com.szu.labsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.szu.audio_play_demo.AudioPlayTestActivity;
import com.szu.audio_record_demo.AudioRecordTestActivity;
import com.szu.bluetooth_demo.BluetoothTestActivity;
import com.szu.download_demo.DownloadTestActivity;
import com.szu.file_demo.FileTestActivity;
import com.szu.photo_demo.PhotoTestActivity;
import com.szu.python_demo.PythonTestActivity;
import com.szu.sensor_demo.SensorTestActivity;
import com.szu.upload_demo.UploadTestActivity;
import com.szu.video_play.VideoPlayTestActivity;
import com.szu.video_record_demo.VideoRecordTestActivity;
import com.szu.wifi_demo.WiFiTestActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    public void initUI() {
        findViewById(R.id.file_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FileTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.bluetooth_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BluetoothTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.download_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DownloadTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.upload_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UploadTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.wifi_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WiFiTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.photo_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PhotoTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.audio_play_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AudioPlayTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.audio_record_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AudioRecordTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.audio_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AudioPlayTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.sensor_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SensorTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.video_record_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VideoRecordTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.video_play_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VideoPlayTestActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.python_test).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PythonTestActivity.class);
            startActivity(intent);
        });
    }
}