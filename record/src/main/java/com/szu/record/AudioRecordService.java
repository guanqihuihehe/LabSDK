package com.szu.record;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class AudioRecordService extends Service implements IAudioRecordService {

    private static final String TAG = AudioRecordService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 1;//如果id为0，将导致不能设置为前台service

    private final MyBinder myBinder = new MyBinder();//与外界交互


    private final static String ACTION_NAME = "action_type";

    private final static int ACTION_INVALID = 0;
    private final static int ACTION_START_RECORD = 1;
    private final static int ACTION_STOP_RECORD = 2;
    private final static int ACTION_RESTART_RECORD = 3;
    private final static int ACTION_PAUSE_RECORD = 4;
    private final static int ACTION_INIT_RECORD = 5;
    private final static int ACTION_INIT_RECORD_DEFAULT = 6;
    private final static int ACTION_CANCEL_RECORD = 7;

    private final static String PARAM_PATH = "path";
    private final static String PARAM_NAME = "name";
    private final static String PARAM_SOURCE = "source";
    private final static String PARAM_SAMPLE_RATE = "samplerate";
    private final static String PARAM_CHANNEL = "channel";
    private final static String PARAM_ENCODING = "encoding";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        int notificationId = 123;
        String CHANNEL_ONE_ID = "record";
        String CHANNEL_ONE_NAME = "Channel Record";
        NotificationChannel notificationChannel = null;
        //初始化notificationChannel是解决Bad notification for startForeground问题的关键1
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_MIN);
            notificationChannel.setShowBadge(false);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);//用这个方法注册这个notification到系统中
        }

        //使用这个NotificationCompat代替Notification是解决Bad notification for startForeground问题的关键2
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ONE_ID)
                .setSmallIcon((R.drawable.ic_launcher_foreground))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("正在录音")
                .setContentText("")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(false)
                .setChannelId(CHANNEL_ONE_ID)//这个setChannelId是解决Bad notification for startForeground问题的关键3
                .build();
        //使用startForeground,如果id为0，那么notification将不会显示
        startForeground(notificationId, notification);

        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey(ACTION_NAME)) {
            switch (bundle.getInt(ACTION_NAME, ACTION_INVALID)) {
                case ACTION_START_RECORD:
                    doStartRecording();
                    break;
                case ACTION_STOP_RECORD:
                    doStopRecording();
                    break;
                case ACTION_RESTART_RECORD:
                    doRestartRecording();
                    break;
                case ACTION_PAUSE_RECORD:
                    doPauseRecording();
                    break;
                case ACTION_INIT_RECORD:
                    doInitRecorder(bundle.getString(PARAM_NAME), bundle.getString(PARAM_PATH), bundle.getInt(PARAM_SOURCE), bundle.getInt(PARAM_SAMPLE_RATE), bundle.getInt(PARAM_CHANNEL), bundle.getInt(PARAM_ENCODING));
                    break;
                case ACTION_INIT_RECORD_DEFAULT:
                    doInitDefaultRecorder(bundle.getString(PARAM_NAME), bundle.getString(PARAM_PATH));
                    break;
                case ACTION_CANCEL_RECORD:
                    doCancelRecording();
                    break;
                default:
                    break;
            }
            return START_STICKY;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    //在Activity获取Service实例
    public class MyBinder extends Binder {
        public AudioRecordService getService(){
            return AudioRecordService.this;
        }
    }

    private void doInitRecorder(String fileName, String rootPath, int audioSource, int sampleRateInHz, int channelConfig, int audioEncoding) {
        Log.v(TAG, "doInitRecording path"+rootPath+"/"+fileName);
        AudioRecordManager.getInstance().createAudio(fileName, rootPath, audioSource, sampleRateInHz, channelConfig, audioEncoding);
    }

    private void doInitDefaultRecorder(String fileName, String rootPath) {
        Log.v(TAG, "doInitRecording path"+rootPath+"/"+fileName);
        AudioRecordManager.getInstance().createDefaultAudio(fileName, rootPath);
    }

    private void doStartRecording() {
        Log.v(TAG, "doStartRecording");
        AudioRecordManager.getInstance().startRecord();
    }

    private void doRestartRecording() {
        Log.v(TAG, "doResumeRecording");
        AudioRecordManager.getInstance().restartRecord();
    }

    private void doPauseRecording() {
        Log.v(TAG, "doResumeRecording");
        AudioRecordManager.getInstance().pauseRecord();
    }

    private void doStopRecording() {
        Log.v(TAG, "doResumeRecording");
        AudioRecordManager.getInstance().stopRecord();
        stopSelf();
    }

    private void doCancelRecording() {
        Log.v(TAG, "doResumeRecording");
        AudioRecordManager.getInstance().cancelRecord();
        stopSelf();
    }

    @Override
    public void initRecorder(Context context, String fileName, String rootPath, int audioSource, int sampleRateInHz, int channelConfig, int audioEncoding) {
        Intent intent = new Intent(context, AudioRecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_INIT_RECORD);
        intent.putExtra(PARAM_PATH, rootPath);
        intent.putExtra(PARAM_NAME, fileName);
        intent.putExtra(PARAM_SOURCE, audioSource);
        intent.putExtra(PARAM_SAMPLE_RATE, sampleRateInHz);
        intent.putExtra(PARAM_CHANNEL, channelConfig);
        intent.putExtra(PARAM_ENCODING, audioEncoding);
        verifyAudioPermissions((Activity) context);
        context.startService(intent);
    }

    @Override
    public void initRecorder(Context context, String fileName, String rootPath) {
        Intent intent = new Intent(context, AudioRecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_INIT_RECORD_DEFAULT);
        intent.putExtra(PARAM_PATH, rootPath);
        intent.putExtra(PARAM_NAME, fileName);
        verifyAudioPermissions((Activity) context);
        context.startService(intent);
    }

    @Override
    public void startRecording(Context context) {
        Intent intent = new Intent(context, AudioRecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_START_RECORD);
        context.startService(intent);
    }

    @Override
    public void stopRecording(Context context) {
        Intent intent = new Intent(context, AudioRecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_STOP_RECORD);
        context.startService(intent);
    }

    @Override
    public void restartRecording(Context context) {
        Intent intent = new Intent(context, AudioRecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_RESTART_RECORD);
        context.startService(intent);
    }

    @Override
    public void pauseRecording(Context context) {
        Intent intent = new Intent(context, AudioRecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_PAUSE_RECORD);
        context.startService(intent);
    }

    @Override
    public void cancelRecording(Context context) {
        Intent intent = new Intent(context, AudioRecordService.class);
        intent.putExtra(ACTION_NAME, ACTION_CANCEL_RECORD);
        context.startService(intent);
    }

    /*申请录音权限、写入文件权限*/
    private static final int GET_RECODE_AUDIO = 0;
    private static final int GET_WRITE_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSION_AUDIO = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /*
     * 申请录音权限*/
    public static void verifyAudioPermissions(Activity activity) {
        int permissionRecord = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO);
        int permissionWrite = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionRecord != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
                    GET_RECODE_AUDIO);
        }
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
                    GET_WRITE_EXTERNAL_STORAGE);
        }
    }

}
