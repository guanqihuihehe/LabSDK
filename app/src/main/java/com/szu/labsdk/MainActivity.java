package com.szu.labsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.szu.bluetooth.BluetoothTestActivity;
import com.szu.file.FileTestActivity;

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
    }
}