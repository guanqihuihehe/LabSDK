package com.szu.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SensorTestActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static String TAG ="SensorTestActivity";

    private SensorService mSensorService;

    private ListView mAvailableSensorsListView;
    private ArrayAdapter<String> mAvailableSensorsAdapter;
    private List<Sensor> mAvailableSensors = new ArrayList<Sensor>();
    private List<String> mAvailableSensorTypes = new ArrayList<String>();

    private TextView mAccelerometerSensorTextView;
    private TextView mMagneticSensorTextView;
    private TextView mGyroscopeSensorTextView;
    private TextView mOrientationSensorTextView;

    private float[] mAccelerometerReading = new float[3];
    private float[] mMagneticFieldReading = new float[3];

    private MySensorEventListener accelerometerListener;
    private MySensorEventListener magneticListener;
    private MySensorEventListener gyroscopeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_test);
        mSensorService = SensorService.getInstance(this);
        initUI();
    }

    public void initUI() {
        mAccelerometerSensorTextView = findViewById(R.id.accelerometer_sensor);
        mMagneticSensorTextView = findViewById(R.id.magnetic_sensor);
        mGyroscopeSensorTextView = findViewById(R.id.gyroscope_sensor);
        mOrientationSensorTextView = findViewById(R.id.orientation_sensor);

        mAvailableSensorsListView = (ListView) findViewById(R.id.available_sensors_listview);
        mAvailableSensorsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mAvailableSensorTypes);
        mAvailableSensorsListView.setAdapter(mAvailableSensorsAdapter);
        mAvailableSensorsListView.setOnItemClickListener(this);

        findViewById(R.id.get_sensors).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Sensor> deviceSensors = mSensorService.getAllSensors();
                for (int i = 0; i<deviceSensors.size(); i++) {
                    Sensor sensor = deviceSensors.get(i);
                    String type = sensor.getStringType();
                    mAvailableSensorTypes.add((i+1)+": "+type);
                    mAvailableSensors.add(sensor);
                }
                mAvailableSensorsAdapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.listen_accelerometer_sensor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accelerometerListener = new MySensorEventListener();
                mSensorService.startSensor(Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL, accelerometerListener);
            }
        });

        findViewById(R.id.listen_magnetic_sensor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                magneticListener = new MySensorEventListener();
                mSensorService.startSensor(Sensor.TYPE_MAGNETIC_FIELD, SensorManager.SENSOR_DELAY_NORMAL, magneticListener);
            }
        });

        findViewById(R.id.listen_gyroscope_sensor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gyroscopeListener = new MySensorEventListener();
                mSensorService.startSensor(Sensor.TYPE_GYROSCOPE, SensorManager.SENSOR_DELAY_NORMAL, gyroscopeListener);
            }
        });

        findViewById(R.id.calculate_orientation_sensor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateOrientation();
            }
        });

        findViewById(R.id.stop_listen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorService.stopSensor(accelerometerListener);
                mSensorService.stopSensor(magneticListener);
                mSensorService.stopSensor(gyroscopeListener);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Sensor sensor = mAvailableSensors.get(position);
        mSensorService.startSensor(sensor, SensorManager.SENSOR_DELAY_NORMAL, new MySensorEventListener());
    }

    /*
    This orientation sensor was deprecated in Android 2.2 (API level 8), and this sensor type was deprecated in Android 4.4W (API level 20).
    The sensor framework provides alternate methods for acquiring device orientation.
     */
    //举个例子，怎样利用加速度计、磁力计、陀螺仪的数据计算手机的倾角，详细可以查看Android官方文档关于这些传感器的数据的介绍https://developer.android.google.cn/guide/topics/sensors/sensors_motion
    private void calculateOrientation() {
        final float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerReading, mMagneticFieldReading);

        final float[] orientationAngles = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        Log.d(TAG, "orientation data[x:" + orientationAngles[0] + ", y:" + orientationAngles[1] + ", z:" + orientationAngles[2] + "]");
        mOrientationSensorTextView.setText("[x:" + orientationAngles[0] + ", y:" + orientationAngles[1] + ", z:" + orientationAngles[2] + "]");
    }

    //举个例子，怎样使用加速度计、磁力计、陀螺仪的数据，详细可以查看Android官方文档关于这些传感器的数据的介绍https://developer.android.google.cn/guide/topics/sensors/sensors_motion
    private class MySensorEventListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mAccelerometerReading = event.values;
                Log.d(TAG, "accelerometer data[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
                mAccelerometerSensorTextView.setText("[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mMagneticFieldReading = event.values;
                Log.d(TAG, "magnetic data[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
                mMagneticSensorTextView.setText("[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
            } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                Log.d(TAG, "gyroscope data[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
                mGyroscopeSensorTextView.setText("[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
            } else {
                Log.d(TAG,"其他传感器的数据处理");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "onAccuracyChanged:" + sensor.getType() + "->" + accuracy);
        }

    }
}
