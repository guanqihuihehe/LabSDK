package com.szu.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;

import java.util.List;

public interface ISensorService {

    /**
     * 配对某种类型的sensor是否可用
     * */
    boolean isSensorAvailable(int sensorType);

    /**
     * 获取该设备支持的所有sensor
     * */
    List<Sensor> getAllSensors();

    /**
     * 获取特定类型的sensor列表
     * */
    List<Sensor> getSensorList(int sensorType);

    /**
     * 获取特定类型、特定供应商、特定版本号的sensor列表
     * */
    List<Sensor> getSensorList(int sensorType, String vendor, int version);

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
    boolean startSensor(int sensorType, int delayTime, SensorEventListener sensorEventListener);

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
    boolean startSensor(Sensor sensor, int delayTime, SensorEventListener sensorEventListener);

    /**
     * 开启某种传感器的数据监听
     * @param sensorEventListener 传感器回调事件，这里传入的事件要和startSensor接口传入的一致
     * 建议在退出activity时调用，或者不监听时调用
     * */
    void stopSensor(SensorEventListener sensorEventListener);
}
