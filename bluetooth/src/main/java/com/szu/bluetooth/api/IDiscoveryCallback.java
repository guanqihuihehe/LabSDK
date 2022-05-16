package com.szu.bluetooth.api;

public interface IDiscoveryCallback {
    int ACTION_DISCOVERY_START = 0;//开始查找蓝牙设备
    int ACTION_DISCOVERY_FOUND = 1;//找到一个蓝牙设备
    int ACTION_DISCOVERY_FINISH = 2;//寻找蓝牙设备结束

    /**调用查找蓝牙设备接口后时回调，actionCode含义如上*/
    void onResult(int actionCode);
}
