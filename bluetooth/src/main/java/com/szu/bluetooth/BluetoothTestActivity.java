package com.szu.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.szu.bluetooth.demo2.Demo2Activity;


public class BluetoothTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initView();
        doRequestPermissions(this);
    }


    private void initView(){
        findViewById(R.id.btn2).setOnClickListener(v -> {
            startActivity(new Intent(BluetoothTestActivity.this, Demo2Activity.class));
        });
    }

    private static final int ACCESS_FINE_LOCATION = 1;
    private static final int ACCESS_COARSE_LOCATION = 2;
    private static final int BLUETOOTH = 3;
    private static final int BLUETOOTH_ADMIN = 4;
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
    };

    /*
     * 申请权限*/
    public static void doRequestPermissions(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    ACCESS_COARSE_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    BLUETOOTH);
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    BLUETOOTH_ADMIN);
        }
    }
}
