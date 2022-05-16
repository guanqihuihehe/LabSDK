package com.szu.download.impl;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import com.szu.download.api.IDownloadClientListener;
import com.szu.download.api.IDownloadService;

import java.util.HashMap;
import java.util.Map;

public class DownloadService extends Service implements IDownloadService {

    private final static String TAG="DownloadService";
    private static DownloadService mInstance;
    private static Object mLock = new Object();

    public static HashMap<Integer,DownloadTask> downloadTaskHashMap;

    public static HashMap<String, IDownloadClientListener> clientListenerHashMap = new HashMap<String,IDownloadClientListener>();

    public DownloadService() {
    }

    public BroadcastReceiver notificationBroadcastReceiver =new BroadcastReceiver () {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int downloadID = intent.getIntExtra("download_id", -1);
            if (downloadID != -1) {
                if (action.equals("notification_clicked")) {
                    //处理点击事件
                    doPauseDownload(downloadID);
                    Log.e(TAG,"进入广播："+downloadID);
                }
                if (action.equals("notification_cancelled")) {
                    //处理滑动清除和点击删除事件
//                    Toast.makeText(context, "取消下载", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    class DownloadListener implements DownloadListenerInterface {

        private final int downloadType;
        private final int downloadID;
        private final String downloadName;
        private final String destPath;
        private final String downloadUrl;
        private IDownloadClientListener mClientListener;

        DownloadListener(int downloadID,String downloadName,int downloadType,String downloadUrl,String destPath, IDownloadClientListener clientListener) {
            this.downloadID = downloadID;
            this.downloadName=downloadName;
            this.downloadType=downloadType;
            this.destPath=destPath;
            this.downloadUrl = downloadUrl;
            this.mClientListener = clientListener;
            getNotificationManager().notify(downloadID, getNotification("正在下载..."+downloadName, 0,downloadID));
            if (mClientListener == null) {
                Log.e(TAG,"mClientListener == null");
            } else {
                mClientListener.onStart(downloadID);
            }
            mClientListener.onStart(downloadID);
        }
        @Override
        public void onProgress(int progress) {
            Log.e(TAG,"更新进度："+downloadID);
            mClientListener.onProgress(progress);
            getNotificationManager().notify(downloadID, getNotification("正在下载..."+downloadName, progress,downloadID));
        }

        @Override
        public void onSuccess() {
            // 下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(downloadID, getNotification("成功下载:"+downloadName, -1,downloadID));
            Toast.makeText(DownloadService.this, "成功下载:"+downloadName, Toast.LENGTH_SHORT).show();
            Toast.makeText(DownloadService.this,"文件保存路径:"+destPath,Toast.LENGTH_LONG).show();
            if(downloadType==1){
                // 最后通知图库更新
                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + destPath)));
            }
            stopSelf(downloadID);
            downloadTaskHashMap.remove(downloadID);
            clientListenerHashMap.remove(downloadUrl);
            mClientListener.onSuccess(downloadID);
        }

        @Override
        public void onFailed() {
            // 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(downloadID, getNotification("下载失败"+downloadName, -1,downloadID));
            Toast.makeText(DownloadService.this, "下载失败"+downloadName, Toast.LENGTH_SHORT).show();
            stopSelf(downloadID);
            downloadTaskHashMap.remove(downloadID);
            clientListenerHashMap.remove(downloadUrl);
            mClientListener.onFailed(downloadID, -1, "下载失败");
        }

        @Override
        public void onPaused() {
            Log.e(TAG,"暂停下载："+downloadID);
            getNotificationManager().notify(downloadID, getNotification("暂停下载"+downloadName, -1,downloadID));
            mClientListener.onPaused(0);
        }

        @Override
        public void onCanceled() {
            stopForeground(true);
            Toast.makeText(DownloadService.this, "取消下载", Toast.LENGTH_SHORT).show();
            stopSelf(downloadID);
            downloadTaskHashMap.remove(downloadID);
            clientListenerHashMap.remove(downloadUrl);
            mClientListener.onCanceled(0);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadTaskHashMap=new HashMap<>();
        IntentFilter filter = new IntentFilter();
        filter.addAction("notification_clicked");
        registerReceiver(notificationBroadcastReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getStringExtra("action");
        if(action.equals("init")) {
            if (clientListenerHashMap == null) {
                Log.e(TAG, "clientListenerHashMap = null");
            } else {
                Log.e(TAG, "clientListenerHashMap != null");
            }
        }
        if (action.equals("start")) {
            int downloadID =startId;
            String destPath=intent.getStringExtra("destPath");
            int downloadType=intent.getIntExtra("downloadType",-1);
            String downloadUrl=intent.getStringExtra("downloadUrl");
            String downloadName = intent.getStringExtra("fileName");
            if(downloadTaskHashMap.containsKey(downloadID)){
                downloadTaskHashMap.get(downloadID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,downloadUrl,destPath);
            }
            else {
                IDownloadClientListener clientListener = clientListenerHashMap.get(downloadUrl);
                DownloadListener listener=new DownloadListener(downloadID,downloadName,downloadType,downloadUrl,destPath,clientListener);
                DownloadTask downloadTask = new DownloadTask(listener);
                downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,downloadUrl,destPath);
                downloadTaskHashMap.put(downloadID,downloadTask);
                startForeground(downloadID, getNotification("正在下载..."+ downloadName, 0,downloadID));
                Toast.makeText(DownloadService.this, "正在下载..."+ downloadName, Toast.LENGTH_SHORT).show();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(notificationBroadcastReceiver);
        for(Map.Entry<Integer,DownloadTask> entry:downloadTaskHashMap.entrySet()){
            entry.getValue().cancelDownload();
        }
        super.onDestroy();
    }

    @Override
    public void startDownload(Context context, String fileName, String url, String path, int type, IDownloadClientListener clientListener) {
        requestDownloadPermissions((Activity) context);
        Intent initIntent = new Intent(context, DownloadService.class);
        initIntent.putExtra("action","init");
        context.startService(initIntent);

        if (clientListenerHashMap.containsKey(url)) {
            clientListener.onFailed(-1, -1, "已有相同URL的下载任务");
        } else {
            clientListenerHashMap.put(url, clientListener);
            Intent intent = new Intent(context, DownloadService.class);
            intent.putExtra("action","start");
            intent.putExtra("fileName",fileName);
            intent.putExtra("downloadUrl",url);
            intent.putExtra("destPath",path);
            intent.putExtra("downloadType",type);
            context.startService(intent);
        }
    }

    @Override
    public void pauseDownload(int downloadID) {
        doPauseDownload(downloadID);
    }

    @Override
    public void resumeDownload(int downloadID) {
        doPauseDownload(downloadID);
    }

    @Override
    public void cancelDownload(int downloadID) {
        doCancelDownload(downloadID);
    }

    private void doPauseDownload(int downloadID) {
        Log.e(TAG,"doPauseDownload："+downloadID);
        if(downloadTaskHashMap.containsKey(downloadID)){
            DownloadTask originTask = downloadTaskHashMap.get(downloadID);
            assert originTask != null;
            originTask.pauseDownload();
            if(!originTask.isPaused()){
                Log.e(TAG,"重启："+downloadID);
                DownloadTask downloadTask = new DownloadTask(originTask.getListener());

                downloadTaskHashMap.remove(downloadID);
                downloadTaskHashMap.put(downloadID,downloadTask);
                downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,originTask.currentUrl,originTask.currentDestPath);
            }
            else {
                Log.e(TAG,"暂停："+downloadID);
            }

        }
    }

    private void doCancelDownload(int downloadID) {
        if(downloadTaskHashMap.containsKey(downloadID)){
            DownloadTask originTask = downloadTaskHashMap.get(downloadID);
            assert originTask != null;
            originTask.cancelDownload();
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress,int downloadID) {
        String CHANNEL_ONE_ID = String.valueOf(downloadID);
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);//用这个方法注册这个notification到系统中
        }
//        Intent intent = new Intent(this, ShowMyDownloadActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        //点击广播监听
        Intent intentClick = new Intent();
        intentClick.setAction("notification_clicked");
        intentClick.putExtra("download_id", downloadID);
        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_UPDATE_CURRENT);
        //cancle广播监听
        Intent intentCancel = new Intent();
        intentCancel.setAction("notification_cancelled");
        intentCancel.putExtra("download_id", downloadID);
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(com.szu.base.R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), com.szu.base.R.mipmap.ic_launcher));
//        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        builder.setContentIntent(pendingIntentClick);
        builder.setDeleteIntent(pendingIntentCancel);
        builder.setChannelId(CHANNEL_ONE_ID);
        builder.setAutoCancel(true);
        if (progress >= 0) {
            // 当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }

        return builder.build();
    }

    /*申请权限*/
    private static final int WRITE_EXTERNAL_STORAGE = 0;
    private static final int READ_EXTERNAL_STORAGE = 1;
    private static final int INTERNET = 2;
    private static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    /*
     * 申请权限*/
    public static void requestDownloadPermissions(Activity activity) {
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
    }
}
