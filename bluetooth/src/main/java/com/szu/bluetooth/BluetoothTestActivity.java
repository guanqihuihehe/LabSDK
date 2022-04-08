package com.szu.bluetooth;

import android.bluetooth.BluetoothDevice;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 蓝牙搜索和蓝牙通信
 */
public class BluetoothTestActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = BluetoothTestActivity.class.getName();

    private ListView mBondedDevicesListView;
    private ListView mAvailableDevicesListView;

    private List<String> mBluetoothBondedDevices = new ArrayList<String>();

    private List<String> mBluetoothAvailableDevices = new ArrayList<String>();

    // ListView的字符串数组适配器
    private ArrayAdapter<String> mBondedArrayAdapter;
    private ArrayAdapter<String> mAvailableArrayAdapter;

    // UUID，蓝牙建立链接需要的
    private final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//特定的UUID

    // 获取到向设备写的输出流，全局变量，否则连接在方法执行完就结束了
    private OutputStream mClientOutputStream1 = null;//客户端输出流
    private InputStream mClientInputStream1 = null;//客户端输入流
    private OutputStream mClientOutputStream2 = null;//客户端输出流
    private InputStream mClientInputStream2 = null;//客户端输入流
    private OutputStream mServerOutputStream;//服务端输出流
    private InputStream mServerInputStream;//服务端输入流

    //线程池
    private ExecutorService mExecutorService;

    MyHandler mMyHandler;

    private IBluetoothService mBluetoothService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        mBluetoothService = new BluetoothService();

        mExecutorService = Executors.newCachedThreadPool();
        initBluetooth();
        initView();
        mMyHandler = new MyHandler(this);
    }

    private void initBluetooth() {

        mBluetoothService.initBluetooth(this);

        //获取已经配对的蓝牙设备
        List<BluetoothDevice> bondedDevices = mBluetoothService.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                mBluetoothBondedDevices.add(device.getName() + "-"+ device.getAddress());
            }
        }
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

        findViewById(R.id.start_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothService.startBluetoothServer(null, new IConnectCallback() {
                    @Override
                    public void onSuccess(InputStream inputStream, OutputStream outputStream) {
                        mServerInputStream = inputStream;
                        mServerOutputStream = outputStream;
                        mExecutorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // 无线循环来接收数据
                                    while (true) {
                                        // 创建一个1024字节的缓冲
                                        byte[] buffer = new byte[1024];
                                        // 每次读取128字节，并保存其读取的角标
                                        int count = mServerInputStream.read(buffer);
                                        // 创建Message类，向handler发送数据
                                        Message msg = new Message();
                                        // 发送一个String的数据，让他向上转型为obj类型
                                        msg.obj = new String(buffer, 0, count, "utf-8");
                                        Log.e("AcceptThread","接收到数据了");
                                        mServerOutputStream.write("服务端接收到数据了".getBytes("utf-8"));
                                        // 发送数据
                                        mMyHandler.sendMessage(msg);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMsg) {
                        Toast.makeText(BluetoothTestActivity.this,"已搜索完成",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.search_bluetooth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBluetoothAvailableDevices.clear();
                mBluetoothService.startDiscovery(new IDiscoveryCallback() {
                    @Override
                    public void onResult(int actionCode) {
                        if (actionCode == IDiscoveryCallback.ACTION_DISCOVERY_FOUND) {
                            List<BluetoothDevice> availableDevices = mBluetoothService.getAvailableDevices();
                            for (BluetoothDevice device : availableDevices) {
                                String tempDeviceInfo = device.getName() + "-" + device.getAddress();
                                if (!mBluetoothAvailableDevices.contains(tempDeviceInfo)) {
                                    mBluetoothAvailableDevices.add(tempDeviceInfo);
                                }
                            }
                            mAvailableArrayAdapter.notifyDataSetChanged();//更新适配器
                        }
                        if (actionCode == IDiscoveryCallback.ACTION_DISCOVERY_FINISH) {
                            //已搜素完成
                            Toast.makeText(BluetoothTestActivity.this,"已搜索完成",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        findViewById(R.id.send_message1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText bluetoothMsg = findViewById(R.id.bluetooth_message1);
                String msg = bluetoothMsg.getText().toString();
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mClientOutputStream1 != null) {
                            try {
                                mClientOutputStream1.write(msg.getBytes("utf-8"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

        findViewById(R.id.send_message2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText bluetoothMsg = findViewById(R.id.bluetooth_message2);
                String msg = bluetoothMsg.getText().toString();
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mClientOutputStream2 != null) {
                            try {
                                mClientOutputStream2.write(msg.getBytes("utf-8"));
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

        mBluetoothService.clientConnectBluetooth(address, null,new IConnectCallback() {
            @Override
            public void onSuccess(InputStream inputStream, OutputStream outputStream) {
                try {
                    if (outputStream != null) {
                        if (mClientOutputStream1 == null) {
                            mClientOutputStream1 = outputStream;
                            //往服务端写信息
                            mClientOutputStream1.write("蓝牙信息来了".getBytes("utf-8"));
                            mClientOutputStream1.write("蓝牙信息2".getBytes("utf-8"));
                            mClientOutputStream1.write("蓝牙信息3".getBytes("utf-8"));
                        } else {
                            mClientOutputStream2 = outputStream;
                            //往服务端写信息
                            mClientOutputStream2.write("蓝牙信息来了".getBytes("utf-8"));
                            mClientOutputStream2.write("蓝牙信息2".getBytes("utf-8"));
                            mClientOutputStream2.write("蓝牙信息3".getBytes("utf-8"));
                        }
                        Log.e("send", "发送信息成功，请查收");
                    }

                    if (inputStream != null) {
                        if (mClientInputStream1 == null) {
                            mClientInputStream1 = inputStream;
                            while (mClientInputStream1 != null) {
                                // 创建一个1024字节的缓冲
                                byte[] buffer = new byte[1024];
                                // 每次读取128字节，并保存其读取的角标
                                int count = mClientInputStream1.read(buffer);
                                // 创建Message类，向handler发送数据
                                Message msg = new Message();
                                // 发送一个String的数据，让他向上转型为obj类型
                                msg.obj = new String(buffer, 0, count, "utf-8");
                                Log.e("Client", String.valueOf(msg));
                                // 发送数据
                                mMyHandler.sendMessage(msg);
                            }
                        } else {
                            mClientInputStream2 = inputStream;
                            while (mClientInputStream2 != null) {
                                // 创建一个1024字节的缓冲
                                byte[] buffer = new byte[1024];
                                // 每次读取128字节，并保存其读取的角标
                                int count = mClientInputStream2.read(buffer);
                                // 创建Message类，向handler发送数据
                                Message msg = new Message();
                                // 发送一个String的数据，让他向上转型为obj类型
                                msg.obj = new String(buffer, 0, count, "utf-8");
                                Log.e("Client", String.valueOf(msg));
                                // 发送数据
                                mMyHandler.sendMessage(msg);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMsg) {
                Log.e(TAG,"errorCode:"+errorCode+"\n"+"errorMsg:"+errorMsg);
            }
        });
    }

    // 创建handler，因为我们接收是采用线程来接收的，在线程中无法操作UI，所以需要handler
    private static class MyHandler extends Handler {
        //持有弱引用HandlerActivity,GC回收时会被回收掉.
        private final WeakReference<BluetoothTestActivity> mActivity;

        public MyHandler(BluetoothTestActivity activity){
            mActivity =new WeakReference<BluetoothTestActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            BluetoothTestActivity bluetoothTestActivity = mActivity.get();
            super.handleMessage(msg);
            // 通过msg传递过来的信息，吐司一下收到的信息
            Log.i("MyHandler", msg.obj.toString());// 接收其他设备传过来的消息
            Toast.makeText(bluetoothTestActivity, (String) msg.obj, Toast.LENGTH_SHORT).show();
        }
    }
}

