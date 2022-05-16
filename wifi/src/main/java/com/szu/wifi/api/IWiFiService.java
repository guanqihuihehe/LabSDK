package com.szu.wifi.api;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

public interface IWiFiService {

    /**
     * 初始化 WiFi 直连功能
     * 初始化具体的结果会在onInitResult回调。是否成功会直接作为函数返回值返回。
     * */
    boolean initWiFiService(Context context, IWiFiListener wifiListener);

    /**
     * 注册广播接收器，建议在onResume调用
     * */
    void registerWifiReceiver();

    /**
     * 反注册广播接收器，建议在onPause调用
     * */
    void unRegisterWifiReceiver();

    /**
     * 创建 WiFi group。调用这个函数的设备在WiFi连接时会成为组长。如果不调用这个函数，WiFi连接时会在设备之间随机分配组长。
     * 创建的结果会在onCreateGroupResult回调。
     * */
    void createWifiGroup();

    /**
     * 搜索WiFi设备，搜索是否成功会在onDiscoverServiceResult回调，但搜索的WiFi设备列表会在onRequestPeerList回调。
     * */
    void discoverService();

    /**
     * 连接两个WiFi设备，传入的参数是目标WiFi设备
     * */
    void connectService(WifiP2pDevice wifiP2pDevice);

    /**
     * 连接两个WiFi设备，传入的参数是目标WiFi设备的channel和config。
     * 连接是否成功会在onConnectResult回调，但所连接的WiFi设备的详细信息会在onRequestConnectionInfo回调。
     * */
    void connectService(WifiP2pManager.Channel channel, WifiP2pConfig wifiP2pConfig);

    /**
     * 断开WiFi group的连接。一般由group owner来调用即可。
     * 断开是否成功会在onDisConnectResult回调。
     * */
    void disConnect();

    /**
     * 调用这个接口可用获取发送文件方的 Socket。由文件发送方也就是非group owner进行调用
     * Socket 会在onClientResult回调。
     * */
    void sendFile(int port);

    /**
     * 调用这个接口可用获取发送接收方的ServerSocket。由文件接收方也就是group owner进行调用
     * ServerSocket 会在onServerResult回调。
     * */
    void receiveFile(int port);
}
