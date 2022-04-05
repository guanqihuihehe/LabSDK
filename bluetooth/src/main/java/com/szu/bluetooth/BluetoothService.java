package com.szu.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("MissingPermission")
public class BluetoothService implements IBluetoothService {

    private static final int REQUEST_ENABLE = 1;
    private static String TAG = "BluetoothService";

    private Activity mActivity;

    // 获取到蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;

    // UUID，蓝牙建立链接需要的
    private final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//特定的UUID

    private IDiscoveryCallback mDiscoveryCallback;//搜索蓝牙的监听callback

    private boolean isRegister = false;

    //线程池
    private ExecutorService mExecutorService;

    private final List<BluetoothDevice> mBluetoothBondedDevices = new ArrayList<>();

    private final List<BluetoothDevice> mBluetoothAvailableDevices = new ArrayList<>();

    private final List<BluetoothDevice> mBluetoothUnboundDevices = new ArrayList<>();

    @Override
    public void initBluetooth(Activity activity) {
        mActivity = activity;
        mExecutorService = Executors.newCachedThreadPool();

        doRequestPermissions(mActivity);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()){
            //弹出对话框提示用户是后打开
            Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enable, REQUEST_ENABLE);
            //不做提示，直接打开，不建议用下面的方法，有的手机会有问题。
            // mBluetoothAdapter.enable();
        }

        //设置蓝牙一直可见，但收到设备限制，有一些设备就算设置了一直可见也只能120s限制
        Intent discoverableIntent  = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,0);
        mActivity.startActivity(discoverableIntent);



    }

    /**
     * 获取已经配对的可连接设备，不管目前是否可连接
     * */
    @Override
    public List<BluetoothDevice> getBondedDevices() {
        //获取已经配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            mBluetoothBondedDevices.addAll(pairedDevices);
        }
        return mBluetoothBondedDevices;
    }

    /**
     * 获取包括已配对设备在内的可连接设备
     * */
    @Override
    public List<BluetoothDevice> getAvailableDevices() {
        return mBluetoothAvailableDevices;
    }

    /**
     * 获取未配对的可连接设备
     * */
    @Override
    public List<BluetoothDevice> getUnboundDevices() {
        return mBluetoothUnboundDevices;
    }

    /**
     * 开始搜索可用蓝牙设备。
     * 已经在搜索的会终止搜索，并且清空之前的搜索结果，再开始新的搜索。
     * */
    @Override
    public void startDiscovery(IDiscoveryCallback discoveryCallback) {
        mDiscoveryCallback = discoveryCallback;
        //如果当前在搜索，就先取消搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothUnboundDevices.clear();
        mBluetoothAvailableDevices.clear();

        registerBluetoothReceiver();

        //开启搜索
        mBluetoothAdapter.startDiscovery();
    }

    private void registerBluetoothReceiver() {
        if (isRegister) {
            mActivity.unregisterReceiver(receiver);
            isRegister = false;
        }
        //每搜索到一个设备就会发送一个该广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mActivity.registerReceiver(receiver, filter);
        //当全部搜索完后发送该广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mActivity.registerReceiver(receiver, filter);
        isRegister = true;

    }

    /**
     * 定义广播接收器
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.e(TAG, "开始搜索");
                mDiscoveryCallback.onResult(IDiscoveryCallback.ACTION_DISCOVERY_START);

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (!mBluetoothUnboundDevices.contains(device)) {
                        mBluetoothUnboundDevices.add(device);
                    }
                }
                if (!mBluetoothAvailableDevices.contains(device)) {
                    mBluetoothAvailableDevices.add(device);
                    mDiscoveryCallback.onResult(IDiscoveryCallback.ACTION_DISCOVERY_FOUND);
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mDiscoveryCallback.onResult(IDiscoveryCallback.ACTION_DISCOVERY_FINISH);
                //已搜素完成
                Toast.makeText(mActivity,"已搜索完成",Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 客户端连接特定的蓝牙设备。
     * 回调客户端的输入流和输出流。
     * */
    @Override
    public void clientConnectBluetooth(BluetoothDevice bluetoothDevice, String clientUUID, IConnectCallback connectCallback) {
        String address = bluetoothDevice.getAddress();
        clientConnectBluetooth(address, clientUUID, connectCallback);
    }

    /**
     * 客户端连接特定的蓝牙设备。
     * 回调客户端的输入流和输出流
     * */
    @Override
    public void clientConnectBluetooth(String address, String clientUUID, IConnectCallback connectCallback) {

        //判断当前是否正在搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        try {
            BluetoothDevice clientDevice = mBluetoothAdapter.getRemoteDevice(address);
            Log.e("address",address);
            //创建客户端蓝牙Socket
            Log.e("mDevice",clientDevice.getName());
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    BluetoothSocket clientSocket = null;
                    try {
                        UUID uuid;
                        if (clientUUID == null) {
                            uuid = DEFAULT_UUID;
                        } else {
                            uuid = UUID.fromString(clientUUID);
                        }
                        clientSocket = clientDevice.createRfcommSocketToServiceRecord(uuid);
                        if (clientSocket != null)
                            clientSocket.connect();
                        if (clientSocket != null) {
                            OutputStream clientOutputStream = clientSocket.getOutputStream();
                            InputStream clientInputStream = clientSocket.getInputStream();
                            connectCallback.onSuccess(clientInputStream, clientOutputStream);
                        }
                    } catch (IOException e) {
                        try {
                            if (clientSocket != null) {
                                clientSocket.close();
                                clientSocket = null;
                            }
                            e.printStackTrace();
                            connectCallback.onFailed(-1, e.getMessage());
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            connectCallback.onFailed(-1, e2.getMessage());
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "蓝牙连接失败");
            connectCallback.onFailed(-1, e.getMessage());
        }
    }

    /**
     * 服务端监听蓝牙连接请求。
     * 回调服务端的输入流和输出流。
     * */
    @Override
    public void startBluetoothServer(String serverUUID, IConnectCallback acceptCallback) {
        BluetoothServerSocket serverSocket = null;
        try {
            // 通过UUID监听请求，然后获取到对应的服务端接口
            // 为其链接创建一个名称
            String NAME = "Bluetooth_Socket";
            UUID uuid;
            if (serverUUID == null) {
                uuid = DEFAULT_UUID;
            } else {
                uuid = UUID.fromString(serverUUID);
            }
            serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, uuid);
        } catch (Exception e) {
            acceptCallback.onFailed(-1, e.getMessage());
        }
        BluetoothServerSocket finalServerSocket = serverSocket;
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("AcceptThread","开始接收");
                    // 接收其客户端的接口
                    BluetoothSocket socket = finalServerSocket.accept();
                    // 获取到输入流
                    InputStream serverInputStream = socket.getInputStream();
                    // 获取到输出流
                    OutputStream serverOutputStream = socket.getOutputStream();
                    acceptCallback.onSuccess(serverInputStream, serverOutputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                    acceptCallback.onFailed(-1, e.getMessage());
                }
            }
        });
    }

    private static final int ACCESS_FINE_LOCATION = 0;
    private static final int ACCESS_COARSE_LOCATION = 1;
    private static final int BLUETOOTH = 2;
    private static final int BLUETOOTH_ADMIN = 3;
    private static final int BLUETOOTH_SCAN = 4;
    private static final int BLUETOOTH_ADVERTISE = 5;
    private static final int BLUETOOTH_CONNECT = 6;
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
    };

    /**
     * 申请权限
     * */
    private static void doRequestPermissions(Activity activity) {
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS,
                        BLUETOOTH_SCAN);
            }
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS,
                        BLUETOOTH_ADVERTISE);
            }
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS,
                        BLUETOOTH_CONNECT);
            }
        }

    }

}
