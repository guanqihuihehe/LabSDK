package com.szu.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.szu.bluetooth.demo2.Demo2Activity;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class BluetoothService {

    private static final int REQUEST_ENABLE = 1;

    private Activity mActivity;
    // 获取到蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    // UUID，蓝牙建立链接需要的
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//特定的UUID

    // 获取到选中设备的客户端串口，全局变量，否则连接在方法执行完就结束了
    private BluetoothSocket clientSocket;
    // 选中发送数据的蓝牙设备，全局变量，否则连接在方法执行完就结束了
    private BluetoothDevice mDevice;
    // 获取到向设备写的输出流，全局变量，否则连接在方法执行完就结束了
    private OutputStream mOutputStream;//输出流
    private InputStream mClientInputStream;//输出流

    //线程池
    private ExecutorService mExecutorService;

    // 为其链接创建一个名称
    private final String NAME = "Bluetooth_Socket";

    private List<String> mBluetoothBondedDevices = new ArrayList<String>();

    private List<String> mBluetoothAvailableDevices = new ArrayList<String>();


    public void initBluetooth(Activity activity) {
        mActivity = activity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()){
            //弹出对话框提示用户是后打开
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enabler, REQUEST_ENABLE);
            //不做提示，直接打开，不建议用下面的方法，有的手机会有问题。
            // mBluetoothAdapter.enable();
        }

    }

    public List<String> getBondedDevices() {
        //获取已经配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mBluetoothBondedDevices.add(device.getName() + "-"+ device.getAddress());
            }
        }
        return mBluetoothBondedDevices;
    }

    public void registerBluetoothReceiver() {
        //每搜索到一个设备就会发送一个该广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mActivity.registerReceiver(receiver, filter);
        //当全部搜索完后发送该广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mActivity.registerReceiver(receiver, filter);
    }

    /**
     * 定义广播接收器
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                String tempDeviceInfo = device.getName() + "-" + device.getAddress();
                if (!mBluetoothAvailableDevices.contains(tempDeviceInfo)) {
                    mBluetoothAvailableDevices.add(tempDeviceInfo);
                }
                mAvailableArrayAdapter.notifyDataSetChanged();//更新适配器
//                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //已搜素完成
                Toast.makeText(mActivity,"已搜索完成",Toast.LENGTH_LONG).show();
            }
        }
    };
}
