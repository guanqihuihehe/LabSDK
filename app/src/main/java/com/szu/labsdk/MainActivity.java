package com.szu.labsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.szu.bluetooth.BluetoothTestActivity;
import com.szu.download.DownloadTestActivity;
import com.szu.file.FileTestActivity;
import com.szu.upload.UploadTestActivity;
import com.szu.wifi.WiFiTestActivity;

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
    }
}