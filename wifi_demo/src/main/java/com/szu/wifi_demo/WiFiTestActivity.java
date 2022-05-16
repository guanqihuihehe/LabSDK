package com.szu.wifi_demo;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.szu.wifi.api.IWiFiListener;
import com.szu.wifi.api.IWiFiService;
import com.szu.wifi.impl.WiFiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WiFiTestActivity extends AppCompatActivity {
    private static final String TAG = "WiFiTestActivity";
    private static final int CHOOSE_FILE_RESULT_CODE = 1001;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    IWiFiService mWiFiService;
    Socket mSocket = null;
    ServerSocket mServerSocket = null;

    ExecutorService mExecutorService = Executors.newCachedThreadPool();

    static int mClientResult = 0;
    static int mServerResult = 0;

    int clientPort=8988;
    int serverPort=8988;

    MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_test);

        myHandler = new MyHandler(this);

        mWiFiService = new WiFiService();
        mWiFiService.initWiFiService(this, new IWiFiListener() {
            @Override
            public void onInitResult(boolean result, String msg) {
                Toast.makeText(WiFiTestActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelfDeviceResult(WifiP2pDevice wifiP2pDevice) {

            }

            @Override
            public void onConnectResult(int reason) {

            }

            @Override
            public void onRequestConnectionInfo(WifiP2pInfo wifiP2pInfo) {
                if (wifiP2pInfo != null) {

                    TextView view = (TextView) WiFiTestActivity.this.findViewById(R.id.group_owner);
                    String text1 = getResources().getString(R.string.group_owner_text)
                            + ((wifiP2pInfo.isGroupOwner) ? "yes" : "no");
                    view.setText(text1);

                    view = (TextView) WiFiTestActivity.this.findViewById(R.id.device_info);
                    String text2 = "Group Owner IP - " + wifiP2pInfo.groupOwnerAddress.getHostAddress();
                    view.setText(text2);

                    if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                        ((TextView) WiFiTestActivity.this.findViewById(R.id.status_text)).setText(getResources()
                                .getString(R.string.service_text));

                    } else if (wifiP2pInfo.groupFormed) {
                        ((TextView) WiFiTestActivity.this.findViewById(R.id.status_text)).setText(getResources()
                                .getString(R.string.client_text));
                    }
                }
            }

            @Override
            public void onRequestPeerList(ArrayList<WifiP2pDevice> wifiP2pDeviceList) {
                mAdapter = new WifiAdapter(wifiP2pDeviceList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCreateGroupResult(int reason) {

            }

            @Override
            public void onDiscoverServiceResult(int reason) {

            }

            @Override
            public void onDisConnectResult(int reason) {

            }

            @Override
            public void onClientResult(int result, Socket socket) {
                mClientResult = result;
                mSocket = socket;
                if (socket == null) {
                    Log.e(TAG,"接收到socket=null");
                } else {
                    Log.e(TAG,"接收到socket!null");
                }
            }

            @Override
            public void onServerResult(int result, ServerSocket serverSocket) {
                mServerResult = result;
                mServerSocket = serverSocket;
                if (serverSocket == null) {
                    Log.e(TAG,"接收到serverSocket=null");
                } else {
                    Log.e(TAG,"接收到serverSocket!null");
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        findViewById(R.id.btn_create_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWiFiService.createWifiGroup();
            }
        });

        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWiFiService.discoverService();
            }
        });

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientPort = 8988;
                Log.d(TAG, "onClick: btnSend....");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
            }
        });
        findViewById(R.id.btn_send2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientPort = 8989;
                Log.d(TAG, "onClick: btnSend....");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
            }
        });

        findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnDisconnect...");
                mWiFiService.disConnect();
            }
        });

        findViewById(R.id.btn_receive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveFile(serverPort, WiFiTestActivity.this.getExternalFilesDir(null).getAbsolutePath() + "/"
                        + "WiFiTransfer" + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");
            }
        });

        findViewById(R.id.btn_receive2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveFile(8989, WiFiTestActivity.this.getExternalFilesDir(null).getAbsolutePath() + "/"
                        + "WiFiTransfer" + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        mWiFiService.registerWifiReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWiFiService.unRegisterWifiReceiver();
    }



    class WifiAdapter extends RecyclerView.Adapter<WifiHolder> {
        List<WifiP2pDevice> mWifiP2pDevices;

        public WifiAdapter(ArrayList<WifiP2pDevice> wifiP2pDevices) {
            mWifiP2pDevices = wifiP2pDevices;
            Log.d(TAG, "WifiAdapter: " + mWifiP2pDevices.size());
        }

        @Override
        public WifiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WifiHolder(
                    LayoutInflater.from(WiFiTestActivity.this).
                            inflate(R.layout.item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(WifiHolder holder, @SuppressLint("RecyclerView") int position) {
            String showContent = mWifiP2pDevices.get(position).deviceName+ " "+mWifiP2pDevices.get(position).deviceAddress;
            holder.tv.setText(showContent);
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WifiP2pDevice currentDevice = mWifiP2pDevices.get(position);
                    mWiFiService.connectService(currentDevice);
                }
            });

        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "WifiAdapter: " + mWifiP2pDevices.size());
            return mWifiP2pDevices.size();
        }
    }

    static class WifiHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public WifiHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            TextView statusText = (TextView) findViewById(R.id.status_text);
            String text = "Sending: " + uri;
            statusText.setText(text);
            Log.d(TAG, "Intent----------- " + uri);
            sendFile(clientPort, uri.toString());
        }
    }

    private void sendFile(int port, String srcPath) {
        mWiFiService.sendFile(port);
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = WiFiTestActivity.this.getContentResolver();
                InputStream inputStream = null;
                try {
                    while (true) {
                        if (mClientResult != 0) {
                            mClientResult = 0;
                            break;
                        }
                    }
                    inputStream = contentResolver.openInputStream(Uri.parse(srcPath));
                    OutputStream outputStream = null;
                    if (mSocket != null) {
                        outputStream = mSocket.getOutputStream();
                        copyFile(inputStream, outputStream);
                        outputStream.close();
                        mSocket.close();
                        mSocket = null;
                    }
                    inputStream.close();

                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                }
            }
        });

    }

    public void receiveFile(int port, String savePath) {
        mWiFiService.receiveFile(port);
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (mServerResult != 0) {
                            mServerResult = 0;
                            Log.e(TAG,"卡在mServerResult了");
                            break;
                        }
                    }
                    File file = new File(savePath);

                    File dirs = new File(file.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    file.createNewFile();

                    //使用handler来设置UI
                    Message msg = new Message();
                    msg.obj = file.getAbsolutePath();
                    myHandler.sendMessage(msg);

                    OutputStream outputStream = new FileOutputStream(file);

                    Socket socket;
                    InputStream inputStream;
                    if (mServerSocket != null) {
                        Log.e(TAG,"mServerSocket阻塞中");
                        socket = mServerSocket.accept();
                        Log.e(TAG,"mServerSocket结束阻塞");
                        inputStream = socket.getInputStream();
                        Log.e(TAG,"getInputStream成功");
                        if (inputStream == null) {
                            Log.e(TAG,"inputStream == null");
                        }
                        copyFile(inputStream, outputStream);
                        inputStream.close();
                        socket.close();
                        mServerSocket.close();
                        mServerSocket = null;
                        Log.e(TAG,"接收文件完成");
                    }
                    outputStream.close();

                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

    public static boolean copyFile(InputStream inputStream, OutputStream outputStream) {
        byte[] buf = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                Log.e(TAG, "卡住了");
                outputStream.write(buf, 0, len);
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            return false;
        }
        return true;
    }

    // 创建handler，因为我们接收是采用线程来接收的，在线程中无法操作UI，所以需要handler
    private static class MyHandler extends Handler {
        //持有弱引用HandlerActivity,GC回收时会被回收掉.
        private final WeakReference<WiFiTestActivity> mActivity;

        public MyHandler(WiFiTestActivity activity){
            mActivity =new WeakReference<WiFiTestActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            WiFiTestActivity wifiTestActivity = mActivity.get();
            super.handleMessage(msg);
            String text = "File copied - " + msg.obj.toString();
            Log.i("MyHandler", text);// 接收其他设备传过来的消息
            TextView textView = wifiTestActivity.findViewById(R.id.status_text);
            textView.setText(text);
        }
    }



}
