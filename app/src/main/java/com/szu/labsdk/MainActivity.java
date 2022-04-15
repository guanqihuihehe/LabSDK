package com.szu.labsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    public void initUI() {
        findViewById(R.id.file_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.file_demo.FileTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.bluetooth_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.bluetooth_demo.BluetoothTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.download_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.download_demo.DownloadTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.upload_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.upload_demo.UploadTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.wifi_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.wifi_demo.WiFiTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.photo_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.photo_demo.PhotoTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.audio_play_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.audio_play_demo.AudioPlayTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.audio_record_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.audio_record_demo.AudioRecordTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

//        findViewById(R.id.audio_test).setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, AudioTestActivity.class);
//            startActivity(intent);
//        });

        findViewById(R.id.sensor_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.sensor_demo.SensorTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.video_record_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.video_record_demo.VideoRecordTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.video_play_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.video_play_demo.VideoPlayTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.python_test).setOnClickListener(v -> {
            try {
                startActivity(new Intent(MainActivity.this, Class.forName("com.szu.labsdk.pythontest.PythonTestActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}