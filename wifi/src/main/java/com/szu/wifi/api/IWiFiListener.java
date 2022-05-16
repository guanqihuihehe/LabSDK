package com.szu.wifi.api;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public interface IWiFiListener {

    /**
     * 回调初始化的结果，
     * 会在初始化之后回调
     * @param result true代表成功，false代表失败
     * @param msg 成功或者失败的具体原因
     * */
    void onInitResult(boolean result, String msg);

    /**
     * 回调自身设备的WiFi信息
     * @param wifiP2pDevice 自身设备的WiFi设备信息
     * */
    void onSelfDeviceResult(WifiP2pDevice wifiP2pDevice);

    /**
     * 回调wifi设备连接的结果
     * 会在调用连接WiFi的connectService接口后回调
     * @param reason 结果码，100代表成功
     * */
    void onConnectResult(int reason);

    /**
     * 回调与本设备连接的那个设备的WiFi设备信息
     * 会在调用连接WiFi的connectService接口后回调
     * @param wifiP2pInfo 与本设备连接的那个设备的WiFi设备信息
     * */
    void onRequestConnectionInfo(WifiP2pInfo wifiP2pInfo);

    /**
     * 回调当前可连接的WiFi设备列表
     * 会在调用搜索WiFi设备接口discoverService之后回调
     * @param wifiP2pDeviceList 当前可连接的WiFi设备列表
     * */
    void onRequestPeerList(ArrayList<WifiP2pDevice> wifiP2pDeviceList);

    /**
     * 回调搜索WiFi设备的结果
     * 会在调用搜索WiFi设备接口discoverService之后回调
     * @param reason 结果码，100代表成功
     * */
    void onDiscoverServiceResult(int reason);

    /**
     * 回调创建WiFi group的结果
     * 会在调用创建group的接口createWifiGroup后回调
     * @param reason 结果码，100代表成功
     * */
    void onCreateGroupResult(int reason);

    /**
     * 回调断开与另一个WiFi设备的连接的结果
     * 会在调用断开连接的接口disConnect后回调
     * @param reason 结果码，100代表成功
     * */
    void onDisConnectResult(int reason);

    /**
     * 回调发送方的Socket
     * 在调用sendFile接口后回调
     * @param result 结果码，100代表成功
     * @param socket 发送方的socket
     * */
    void onClientResult(int result, Socket socket);

    /**
     * 回调接收方的ServerSocket
     * 在调用receiveFile接口后回调
     * @param result 结果码，100代表成功
     * @param serverSocket 接收方的ServerSocket
     * */
    void onServerResult(int result, ServerSocket serverSocket);
}
