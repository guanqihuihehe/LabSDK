package com.szu.bluetooth.demo2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.szu.bluetooth.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 蓝牙搜索和蓝牙通信
 */
public class Demo2Activity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final int REQUEST_ENABLE = 1;

    private static final String TAG = Demo2Activity.class.getName();

    private ListView mBondedDevicesListView;
    private ListView mAvailableDevicesListView;


    private List<String> mBluetoothBondedDevices = new ArrayList<String>();

    private List<String> mBluetoothAvailableDevices = new ArrayList<String>();

    // ListView的字符串数组适配器
    private ArrayAdapter<String> mBondedArrayAdapter;
    private ArrayAdapter<String> mAvailableArrayAdapter;


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

    // 服务端利用线程不断接受客户端信息
    private AcceptThread thread;
    MyHandler mMyHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo2);
        mExecutorService = Executors.newCachedThreadPool();
        initBluetooth();
        initView();
        mMyHandler = new MyHandler(this);

        // 实例接收客户端传过来的数据线程
        thread = new AcceptThread();
        // 线程开始
        thread.start();
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()){
            //弹出对话框提示用户是后打开
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, REQUEST_ENABLE);
            //不做提示，直接打开，不建议用下面的方法，有的手机会有问题。
            // mBluetoothAdapter.enable();
        }

        //获取已经配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mBluetoothBondedDevices.add(device.getName() + "-"+ device.getAddress());
            }
        }

        //每搜索到一个设备就会发送一个该广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(receiver, filter);
        //当全部搜索完后发送该广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);
    }

    private void initView() {

        mBondedDevicesListView = (ListView) findViewById(R.id.bonded_devices_listview);
        mAvailableDevicesListView = (ListView) findViewById(R.id.available_devices_listview);

        mBondedArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mBluetoothBondedDevices);
        mAvailableArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mBluetoothAvailableDevices);

        mBondedDevicesListView.setAdapter(mBondedArrayAdapter);
        mAvailableDevicesListView.setAdapter(mAvailableArrayAdapter);

        mBondedDevicesListView.setOnItemClickListener(this);//Activity实现OnItemClickListener接口
        mAvailableDevicesListView.setOnItemClickListener(this);


        findViewById(R.id.search_bluetooth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果当前在搜索，就先取消搜索
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                //开启搜索
                mBluetoothAdapter.startDiscovery();
            }
        });

        findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText bluetoothMsg = findViewById(R.id.bluetooth_message);
                String msg = bluetoothMsg.getText().toString();
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mOutputStream != null) {
                            try {
                                mOutputStream.write(msg.getBytes("utf-8"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String s = mAvailableArrayAdapter.getItem(position);
        String address = s.substring(s.indexOf("-")+1).trim();//把地址解析出来

        //主动连接蓝牙服务端
        //判断当前是否正在搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        try {
            if (mDevice == null) {
                //获得远程设备
                mDevice = mBluetoothAdapter.getRemoteDevice(address);
            }
            if (clientSocket == null) {
                Log.e("address",address);
                //创建客户端蓝牙Socket
                Log.e("mDevice",mDevice.getName());
                clientSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        InputStream mInputSream = null;
                        boolean mIsRunning = false;
                        try {
                            if (clientSocket != null)
                                clientSocket.connect();
                            if (clientSocket != null) {
                                mOutputStream = clientSocket.getOutputStream();
                                mClientInputStream = clientSocket.getInputStream();
                            }
                            if (mOutputStream != null) {
                                //往服务端写信息
                                mOutputStream.write("蓝牙信息来了".getBytes("utf-8"));
                                mOutputStream.write("蓝牙信息2".getBytes("utf-8"));
                                mOutputStream.write("蓝牙信息3".getBytes("utf-8"));
                                // 吐司一下，告诉用户发送成功
                                Log.e("send", "发送信息成功，请查收");
                            }

                            while (mClientInputStream != null) {
                                // 创建一个128字节的缓冲
                                byte[] buffer = new byte[1024];
                                // 每次读取128字节，并保存其读取的角标
                                int count = mClientInputStream.read(buffer);
                                // 创建Message类，向handler发送数据
                                Message msg = new Message();
                                // 发送一个String的数据，让他向上转型为obj类型
                                msg.obj = new String(buffer, 0, count, "utf-8");
                                Log.e("Client", String.valueOf(msg));
                                // 发送数据
                                mMyHandler.sendMessage(msg);
                            }


                        } catch (IOException e) {
                            try {
                                if (mInputSream != null)
                                    mInputSream.close();
                                if (clientSocket != null) {
                                    clientSocket.close();
                                    clientSocket = null;
                                }
                            } catch (Exception e2) {
                                // TODO: handle exception
                            }
                        }
                    }
                }).start();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "蓝牙连接失败", Toast.LENGTH_LONG).show();
        }
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
                Toast.makeText(Demo2Activity.this,"已搜索完成",Toast.LENGTH_LONG).show();
            }
        }
    };

    // 创建handler，因为我们接收是采用线程来接收的，在线程中无法操作UI，所以需要handler
    private static class MyHandler extends Handler {
        //持有弱引用HandlerActivity,GC回收时会被回收掉.
        private final WeakReference<Demo2Activity> mActivity;

        public MyHandler(Demo2Activity activity){
            mActivity =new WeakReference<Demo2Activity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            Demo2Activity demo2Activity = mActivity.get();
            super.handleMessage(msg);
            // 通过msg传递过来的信息，吐司一下收到的信息
            Log.i("MyHandler", msg.obj.toString());// 接收其他设备传过来的消息
            Toast.makeText(demo2Activity, (String) msg.obj, Toast.LENGTH_LONG).show();
        }
    };


    // 服务端接收信息线程
    private class AcceptThread extends Thread {
        private BluetoothServerSocket serverSocket;// 服务端接口
        private BluetoothSocket socket;// 获取到客户端的接口
        private InputStream is;// 获取到输入流
        private OutputStream os;// 获取到输出流

        public AcceptThread() {
            try {
                // 通过UUID监听请求，然后获取到对应的服务端接口
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        public void run() {
            try {
                Log.e("AcceptThread","开始接收");
                // 接收其客户端的接口
                socket = serverSocket.accept();
                // 获取到输入流
                is = socket.getInputStream();
                // 获取到输出流
                os = socket.getOutputStream();

                // 无线循环来接收数据
                while (true) {
                    // 创建一个128字节的缓冲
                    byte[] buffer = new byte[128];
                    // 每次读取128字节，并保存其读取的角标
                    int count = is.read(buffer);
                    // 创建Message类，向handler发送数据
                    Message msg = new Message();
                    // 发送一个String的数据，让他向上转型为obj类型
                    msg.obj = new String(buffer, 0, count, "utf-8");
                    Log.e("AcceptThread","接收到数据了");
                    os.write("服务端接收到数据了".getBytes("utf-8"));
                    // 发送数据
                    mMyHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }
    }
}

