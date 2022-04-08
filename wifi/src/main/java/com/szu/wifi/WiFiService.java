package com.szu.wifi;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WiFiService implements IWiFiService{

    public static String TAG = "WiFiService";
    private static final int SOCKET_TIMEOUT = 3600;
    Context mContext;
    WifiP2pManager mWifiP2pManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    IntentFilter mIntentFilter;

    WifiP2pManager.PeerListListener mPeerListListener;

    WifiP2pManager.ConnectionInfoListener mConnectionInfoListener;
    WifiP2pManager.DeviceInfoListener mDeviceInfoListener;


    ProgressDialog mProgressDialog;
    WifiP2pInfo mInfo;
    WifiP2pDevice mSelfDevice;
    IWiFiListener mWiFiListener;

    ExecutorService mExecutorService = Executors.newCachedThreadPool();

    /**
     * 初始化 WiFi 直连功能
     * 初始化具体的结果会在onInitResult回调。是否成功会直接作为函数返回值返回。
     * */
    @Override
    public boolean initWiFiService(Context context, IWiFiListener wifiListener) {
        mContext = context;
        mWiFiListener = wifiListener;

        requestWiFiPermissions((Activity) mContext);

        // Device capability definition check
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e(TAG, "Wi-Fi Direct is not supported by this device.");
            wifiListener.onInitResult(false, "Wi-Fi Direct is not supported by this device.");
            return false;
        }
        // Hardware capability check
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e(TAG, "Cannot get Wi-Fi system service.");
            wifiListener.onInitResult(false, "Cannot get Wi-Fi system service.");
            return false;
        }
        if (!wifiManager.isP2pSupported()) {
            Log.e(TAG, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.");
            wifiListener.onInitResult(false, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.");
            return false;
        }
        mWifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        if (mWifiP2pManager == null) {
            Log.e(TAG, "Cannot get Wi-Fi Direct system service.");
            wifiListener.onInitResult(false, "Cannot get Wi-Fi Direct system service.");
            return false;
        }
        mChannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), null);
        if (mChannel == null) {
            Log.e(TAG, "Cannot initialize Wi-Fi Direct.");
            wifiListener.onInitResult(false, "Cannot initialize Wi-Fi Direct.");
            return false;
        }

        mPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                Collection<WifiP2pDevice> wifiP2pDeviceCollection = wifiP2pDeviceList.getDeviceList();
                ArrayList<WifiP2pDevice> wifiP2pDevices = new ArrayList<>(wifiP2pDeviceCollection);
                mWiFiListener.onRequestPeerList(wifiP2pDevices);
            }
        };

        mConnectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {

            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                if (wifiP2pInfo != null) {
                    mInfo = wifiP2pInfo;
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    mWiFiListener.onRequestConnectionInfo(wifiP2pInfo);
                } else {
                    mWiFiListener.onRequestConnectionInfo(null);
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mDeviceInfoListener = new WifiP2pManager.DeviceInfoListener() {
                @Override
                public void onDeviceInfoAvailable(@Nullable WifiP2pDevice wifiP2pDevice) {
                    mWiFiListener.onSelfDeviceResult(wifiP2pDevice);
                }
            };
        }

        wifiListener.onInitResult(true, "initSuccess");
        return true;
    }

    /**
     * 注册广播接收器，建议在onResume调用
     * */
    @Override
    public void registerWifiReceiver() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mReceiver = new WifiDirectBroadcastReceiver(mWifiP2pManager, mChannel, mPeerListListener, mConnectionInfoListener, mDeviceInfoListener);
        mContext.registerReceiver(mReceiver, mIntentFilter);
    }

    /**
     * 反注册广播接收器，建议在onPause调用
     * */
    @Override
    public void unRegisterWifiReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }

    /**
     * 创建 WiFi group。调用这个函数的设备在WiFi连接时会成为组长。如果不调用这个函数，WiFi连接时会在设备之间随机分配组长。
     * 创建的结果会在onCreateGroupResult回调。
     * */
    @Override
    public void createWifiGroup() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mWiFiListener.onCreateGroupResult(100);
                Log.e(TAG, "createGroup onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                mWiFiListener.onCreateGroupResult(reason);
                Log.e(TAG, "createGroup onFailure. Reason :" + reason);
            }
        });
    }

    /**
     * 搜索WiFi设备，搜索是否成功会在onDiscoverServiceResult回调，但搜索的WiFi设备列表会在onRequestPeerList回调。
     * */
    @Override
    public void discoverService() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mWiFiListener.onDiscoverServiceResult(100);
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onFailure(int reasonCode) {
                mWiFiListener.onDiscoverServiceResult(reasonCode);
                Log.d(TAG, "discoverPeers onFailure. Reason :" + reasonCode);
            }
        });
    }

    /**
     * 连接两个WiFi设备，传入的参数是目标WiFi设备
     * */
    @Override
    public void connectService(WifiP2pDevice wifiP2pDevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = wifiP2pDevice.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        connectService(mChannel, config);
    }

    /**
     * 连接两个WiFi设备，传入的参数是目标WiFi设备的channel和config。
     * 连接是否成功会在onConnectResult回调，但所连接的WiFi设备的详细信息会在onRequestConnectionInfo回调。
     * */
    @Override
    public void connectService(WifiP2pManager.Channel channel, WifiP2pConfig wifiP2pConfig) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mProgressDialog = ProgressDialog.show(mContext, "提示", "连接中");
        mWifiP2pManager.connect(mChannel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mWiFiListener.onConnectResult(100);
                Log.d(TAG, "connect onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                mWiFiListener.onConnectResult(reason);
                Log.d(TAG, "connect onFailure. Reason :" + reason);
            }
        });
    }

    /**
     * 断开WiFi group的连接。一般由group owner来调用即可。
     * 断开是否成功会在onDisConnectResult回调。
     * */
    @Override
    public void disConnect() {
        if (mWifiP2pManager != null) {
            mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    mWiFiListener.onDisConnectResult(100);
                    Log.e(TAG, "removeGroup onSuccess");
                }

                @Override
                public void onFailure(int reasonCode) {
                    mWiFiListener.onDisConnectResult(reasonCode);
                    Log.e(TAG, "removeGroup failed. Reason :" + reasonCode);
                }

            });
        }
    }

    /**
     * 调用这个接口可用获取发送文件方的 Socket。由文件发送方也就是非group owner进行调用
     * Socket 会在onClientResult回调。
     * */
    @Override
    public void sendFile(int port) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {

                String host = mInfo.groupOwnerAddress.getHostAddress();
                Socket socket = new Socket();

                try {
                    Log.d(TAG, "Opening client socket - ");
                    socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                    Log.d(TAG, "Client socket - " + socket.isConnected());
                    mWiFiListener.onClientResult(100, socket);

                } catch (IOException e) {
                    mWiFiListener.onClientResult(-1, null);
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException exception) {
                            // Give up
                            exception.printStackTrace();
                        }
                    }
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    /**
     * 调用这个接口可用获取发送接收方的ServerSocket。由文件接收也就是group owner进行调用
     * ServerSocket 会在onClientResult回调。
     * */
    @Override
    public void receiveFile(int port) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(port);

                    mWiFiListener.onServerResult(100, serverSocket);

                } catch (IOException e) {
                    mWiFiListener.onServerResult(-1, null);
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    public static class WifiDirectBroadcastReceiver extends BroadcastReceiver {

        private WifiP2pManager mWifiP2pManager;
        private WifiP2pManager.Channel mChannel;
        WifiP2pManager.PeerListListener mPeerListListener;
        WifiP2pManager.ConnectionInfoListener mConnectionInfoListener;
        WifiP2pManager.DeviceInfoListener mDeviceInfoListener;

        public WifiDirectBroadcastReceiver() {

        }

        public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                           WifiP2pManager.PeerListListener peerListListener,
                                           WifiP2pManager.ConnectionInfoListener connectionInfoListener,
                                           WifiP2pManager.DeviceInfoListener deviceInfoListener) {
            super();
            mWifiP2pManager = manager;
            mChannel = channel;
            mPeerListListener = peerListListener;
            mConnectionInfoListener = connectionInfoListener;
            mDeviceInfoListener = deviceInfoListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                Log.d(TAG, "onReceive: WIFI_P2P_STATE_CHANGED_ACTION ");
                //判断是否支持 wifi点对点传输。在初始化的时候通过isP2pSupported可用判断，因此这里就不再处理了。

            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                //查找到设备列表
                Log.d(TAG, "onReceive: WIFI_P2P_PEERS_CHANGED_ACTION ");
                if (mWifiP2pManager != null) {
                    mWifiP2pManager.requestPeers(mChannel, mPeerListListener);
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                //获取到连接状态改变的详细信息
                Log.d(TAG, "onReceive: WIFI_P2P_CONNECTION_CHANGED_ACTION");
                if (mWifiP2pManager == null) {
                    return;
                }
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    Log.d(TAG, "onReceive: isConnected");
                    mWifiP2pManager.requestConnectionInfo(mChannel, mConnectionInfoListener);
                } else {
                    // It's a disconnect
                    mConnectionInfoListener.onConnectionInfoAvailable(null);
                    Log.d(TAG, "onReceive: disconnect");
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
                //自身设备信息改变
                Log.d(TAG, "onReceive: WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                WifiP2pDevice wifiP2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                if (mDeviceInfoListener != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mDeviceInfoListener.onDeviceInfoAvailable(wifiP2pDevice);
                }
            }
        }
    }

    /*申请权限*/
    private static final int WRITE_EXTERNAL_STORAGE = 0;
    private static final int READ_EXTERNAL_STORAGE = 1;
    private static final int INTERNET = 2;
    private static final int ACCESS_FINE_LOCATION = 3;
    private static final int ACCESS_WIFI_STATE = 4;
    private static final int CHANGE_WIFI_STATE = 5;
    private static final int CHANGE_NETWORK_STATE = 6;
    private static final int ACCESS_NETWORK_STATE = 7;
    private static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };

    /*
     * 申请权限*/
    private static void requestWiFiPermissions(Activity activity) {
        int permission1 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    WRITE_EXTERNAL_STORAGE);
        }

        int permission2 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    READ_EXTERNAL_STORAGE);
        }

        int permission3 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.INTERNET);
        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    INTERNET);
        }

        int permission4 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission4 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    ACCESS_FINE_LOCATION);
        }

        int permission5 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_WIFI_STATE);
        if (permission5 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    ACCESS_WIFI_STATE);
        }

        int permission6 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CHANGE_WIFI_STATE);
        if (permission6 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    CHANGE_WIFI_STATE);
        }

        int permission7 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CHANGE_NETWORK_STATE);
        if (permission7 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    CHANGE_NETWORK_STATE);
        }

        int permission8 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_NETWORK_STATE);
        if (permission8 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    ACCESS_NETWORK_STATE);
        }
    }

}
