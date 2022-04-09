package com.szu.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SensorService implements ISensorService{

    private static final String TAG ="SensorService";
    private final SensorManager mSensorManager;
    private static volatile SensorService mInstance = null;

    private SensorService(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public static SensorService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SensorService(context);
        }
        return mInstance;
    }



    /**
     * 配对某种类型的sensor是否可用
     * */
    @Override
    public boolean isSensorAvailable(int sensorType) {
        if (mSensorManager.getDefaultSensor(sensorType) != null){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取该设备支持的所有sensor
     * */
    @Override
    public List<Sensor> getAllSensors() {
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        return deviceSensors;
    }

    /**
     * 获取特定类型的sensor列表
     * */
    @Override
    public List<Sensor> getSensorList(int sensorType) {
        List<Sensor> sensors = new ArrayList<>();
        if (isSensorAvailable(sensorType)) {
            sensors = mSensorManager.getSensorList(sensorType);
        } else {
            Log.e(TAG, "该类型sensor不可用");
        }
        return sensors;
    }

    /**
     * 获取特定类型、特定供应商、特定版本号的sensor列表
     * */
    @Override
    public List<Sensor> getSensorList(int sensorType, String vendor, int version) {

        List<Sensor> targetSensors = new ArrayList<>();
        if (isSensorAvailable(sensorType)) {
            List<Sensor> sensors = mSensorManager.getSensorList(sensorType);
            for(int i=0; i<sensors.size(); i++) {
                if ((sensors.get(i).getVendor().contains(vendor)) && (sensors.get(i).getVersion() == version)){
                    targetSensors.add(sensors.get(i));
                }
            }
        } else {
            Log.e(TAG, "该类型sensor不可用");
        }
        return targetSensors;
    }

    /**
     * 开启某种传感器的数据监听
     * @return true代表开启监听成功，false代表失败
     *
     * @param sensorType 传感器类型
     *
     * @param delayTime 传感器每次上报的时间间隔
     * SensorManager.SENSOR_DELAY_FASTEST 0ms,几乎会立即上报数据
     * SensorManager.SENSOR_DELAY_GAME 20ms上报一次数据，实时性较高的游戏都使用该级别
     * SensorManager.SENSOR_DELAY_UI 60ms上报一次数据，适合用户界面UI变化速率
     * SensorManager.SENSOR_DELAY_NORMAL 200ms上报一次数据
     *
     * @param sensorEventListener 传感器回调事件，调用方主要监听onSensorChanged方法
     * */
    @Override
    public boolean startSensor(int sensorType, int delayTime, SensorEventListener sensorEventListener) {
        if (!isSensorAvailable(sensorType)) {
            Log.e(TAG, "在本设备不支持该类型的sensor");
            return false;
        }
        Sensor sensor = mSensorManager.getDefaultSensor(sensorType);
        return startSensor(sensor, delayTime, sensorEventListener);
    }

    /**
     * 开启某种传感器的数据监听
     * @return true代表开启监听成功，false代表失败
     *
     * @param sensor 传感器
     *
     * @param delayTime 传感器每次上报的时间间隔
     * SensorManager.SENSOR_DELAY_FASTEST 0ms,几乎会立即上报数据
     * SensorManager.SENSOR_DELAY_GAME 20ms上报一次数据，实时性较高的游戏都使用该级别
     * SensorManager.SENSOR_DELAY_UI 60ms上报一次数据，适合用户界面UI变化速率
     * SensorManager.SENSOR_DELAY_NORMAL 200ms上报一次数据
     *
     * @param sensorEventListener 传感器回调事件，调用方主要监听onSensorChanged方法
     * */
    @Override
    public boolean startSensor(Sensor sensor, int delayTime, SensorEventListener sensorEventListener) {
        if (sensorEventListener != null) {
            mSensorManager.registerListener(sensorEventListener, sensor, delayTime);
            return true;
        } else {
            Log.e(TAG, "sensorEventListener == null");
            return false;
        }
    }

    /**
     * 开启某种传感器的数据监听
     * @param sensorEventListener 传感器回调事件，这里传入的事件要和startSensor接口传入的一致
     * 建议在退出activity时调用，或者不监听时调用
     * */
    @Override
    public void stopSensor(SensorEventListener sensorEventListener) {
        if (sensorEventListener != null) {
            mSensorManager.unregisterListener(sensorEventListener);
        }
    }
}
