package com.szu.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import java.util.List;

public interface IBluetoothService {

    /**
     * 初始化蓝牙功能
     * */
    void initBluetooth(Activity activity);

    /**
     * 获取已经配对的可连接设备，不管目前是否可连接
     * */
    List<BluetoothDevice> getBondedDevices();

    /**
     * 获取包括已配对设备在内的可连接设备
     * */
    List<BluetoothDevice> getAvailableDevices();

    /**
     * 获取未配对的可连接设备
     * */
    List<BluetoothDevice> getUnboundDevices();

    /**
     * 开始搜索可用蓝牙设备。
     * 已经在搜索的会终止搜索，并且清空之前的搜索结果，再开始新的搜索。
     * 回调搜索的状态码。
     * */
    void startDiscovery(IDiscoveryCallback discoveryCallback);

    /**
     * 客户端连接特定的蓝牙设备。
     * 回调客户端的输入流和输出流。
     * */
    void clientConnectBluetooth(BluetoothDevice bluetoothDevice, String clientUUID, IConnectCallback connectCallback);

    /**
     * 客户端连接特定的蓝牙设备。
     * 回调客户端的输入流和输出流
     * */
    void clientConnectBluetooth(String address, String clientUUID, IConnectCallback connectCallback);

    /**
     * 服务端监听蓝牙连接请求。
     * 回调服务端的输入流和输出流。
     * */
    void startBluetoothServer(String serverUUID, IConnectCallback acceptCallback);

}
