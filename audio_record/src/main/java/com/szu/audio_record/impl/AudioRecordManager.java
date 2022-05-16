package com.szu.audio_record.impl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioRecordManager {

    public static String TAG = "AudioRecorder";

    //音频输入源
    private int AUDIO_INPUT_SOURCE = MediaRecorder.AudioSource.DEFAULT;//这个上下两个麦克风都录音
//    private int AUDIO_INPUT_SOURCE = MediaRecorder.AudioSource.CAMCORDER;//这个上下两个麦克风都录音
//    private int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;//这个只录下面的麦克风
//    private int AUDIO_INPUT = MediaRecorder.AudioSource.VOICE_RECOGNITION;//这个也只录下面
//    private int AUDIO_INPUT = MediaRecorder.AudioSource.VOICE_COMMUNICATION;//这个只录上面的麦克风


    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private int AUDIO_SAMPLE_RATE = 48000;
    //声道 单声道
    private int AUDIO_CHANNEL = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    //编码
    private int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;//8bit的录制就把这个换成ENCODING_PCM_8BIT即可，逻辑代码不需要改动。
    // 缓冲区字节大小
    private int mBufferSizeInBytes = 0;

    //录音对象
    private AudioRecord mAudioRecord;

    //录音状态
    private Status mStatus = Status.STATUS_NO_READY;

    //文件名
    private String mFileName;

    //储存的根目录
    private String mRootPath;

    //pcm文件路径
    private String mPcmPath;

    //wav文件路径
    private String mWavPath;

    //线程池
    private ExecutorService mExecutorService;

    //录音监听
//    private RecordStreamListener listener;


    //音频写入本地文件的输出流
    DataOutputStream mDataOutputStream;

    private volatile static AudioRecordManager mInstance = null;

    public AudioRecordManager() {
    }

    /**
     * 获取单例引用
     *
     * @return
     */
    public static AudioRecordManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioRecordManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioRecordManager();
                }
            }
        }
        return mInstance;
    }

    //申请录音权限、写入文件权限
    private static final int GET_RECODE_AUDIO = 1;
    private static final int GET_WRITE_EXTERNAL_STORAGE = 2;
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

    /**
     * 创建录音对象
     */
    public void createAudio(String fileName, String rootPath, int audioSource, int sampleRateInHz, int channelConfig, int audioEncoding) {

//        //申请权限（这里不能调用verifyAudioPermissions函数就很奇怪）
//        int permissionRecord = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.RECORD_AUDIO);
//        int permissionWrite = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permissionRecord != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
//                    GET_RECODE_AUDIO);
//        }
//        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
//                    GET_WRITE_EXTERNAL_STORAGE);
//        }

        // 获得缓冲区字节大小
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioEncoding);

        mFileName = fileName;
        mRootPath = rootPath;
        mExecutorService = Executors.newCachedThreadPool();
        AUDIO_INPUT_SOURCE = audioSource;
        AUDIO_SAMPLE_RATE = sampleRateInHz;
        AUDIO_CHANNEL = channelConfig;
        AUDIO_ENCODING = audioEncoding;


        mAudioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioEncoding, mBufferSizeInBytes);
        mStatus = Status.STATUS_READY;

    }

    /**
     * 创建默认的录音对象
     *
     * @param fileName 文件名
     */
    public void createDefaultAudio(String fileName, String rootPath) {

//        //申请权限（这里不能调用verifyAudioPermissions函数就很奇怪）
//        int permissionRecord = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.RECORD_AUDIO);
//        int permissionWrite = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permissionRecord != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
//                    GET_RECODE_AUDIO);
//        }
//        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
//                    GET_WRITE_EXTERNAL_STORAGE);
//        }

        mExecutorService = Executors.newCachedThreadPool();
        mFileName = fileName;
        mRootPath = rootPath;
        // 获得缓冲区字节大小
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                AUDIO_CHANNEL, AUDIO_ENCODING);

        mAudioRecord = new AudioRecord(AUDIO_INPUT_SOURCE, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, mBufferSizeInBytes);

        mStatus = Status.STATUS_READY;
    }


    /**
     * 开始录音
     *
     */
    public void startRecord() {

        if (mStatus == Status.STATUS_NO_READY || mAudioRecord == null) {
            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
        }
        if (mStatus == Status.STATUS_START) {
            throw new IllegalStateException("正在录音");
        }
        Log.d("AudioRecorder", "===startRecord===" + mAudioRecord.getState());

        //将录音状态设置成正在录音状态
        mStatus = Status.STATUS_START;
        mAudioRecord.startRecording();

        //使用线程池管理线程
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                writeDataTOFile();
            }
        });
    }

    /**
     * 暂停录音
     */
    public void pauseRecord() {
        Log.d("AudioRecorder", "===pauseRecord===");
        if (mStatus != Status.STATUS_START) {
            throw new IllegalStateException("没有在录音");
        } else {
            mAudioRecord.stop();
            mStatus = Status.STATUS_PAUSE;
        }
    }

    public void restartRecord() {
        if (mStatus == Status.STATUS_NO_READY || mAudioRecord == null) {
            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
        }
        if (mStatus == Status.STATUS_START) {
            throw new IllegalStateException("正在录音");
        }
        Log.d("AudioRecorder", "===startRecord===" + mAudioRecord.getState());

        mStatus = Status.STATUS_START;
        //使用线程池管理线程
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                writeDataTOFile();
            }
        });


    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        Log.d("AudioRecorder", "===stopRecord===");
        if (mStatus == Status.STATUS_NO_READY || mStatus == Status.STATUS_READY) {
//            throw new IllegalStateException("录音尚未开始");
        } else {
            mAudioRecord.stop();
            mStatus = Status.STATUS_STOP;
            release();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        Log.d("AudioRecorder", "===release===");

        if (mAudioRecord != null) {
            mAudioRecord.release();
            mAudioRecord = null;
        }
        mFileName = "";
        mPcmPath = "";
        mWavPath = "";

        mStatus = Status.STATUS_NO_READY;
    }

    /**
     * 取消录音
     */
    public void cancelRecord() {
        if (mStatus == Status.STATUS_START || mStatus == Status.STATUS_PAUSE) {
            mStatus = Status.STATUS_NO_READY;
            if (mAudioRecord != null) {
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }

            //删除文件
            File pcmFile = new File(mPcmPath);
            File wavFile = new File(mWavPath);
            boolean deleteResult;
            if (pcmFile.exists()) {
                deleteResult = pcmFile.delete();
            }
            if (wavFile.exists()) {
                deleteResult = wavFile.delete();
            }
        }
    }


    /**
     * 将音频信息写入文件
     *
     */
    private void writeDataTOFile() {

        //新建文件以及创建输出流
        try {
            mPcmPath = mRootPath+"/"+mFileName;
            mWavPath = mRootPath+"/"+mFileName+".wav";
            Log.d(TAG,"pcm:"+mPcmPath);
            Log.d(TAG,"wav:"+mWavPath);
            File pcmFile = new File(mPcmPath);
            File wavFile = new File(mWavPath);
            if (!pcmFile.exists()) {
                pcmFile.createNewFile();
            }
            if (!wavFile.exists()) {
                wavFile.createNewFile();
            }

            Log.d(TAG,pcmFile.getAbsolutePath());

            mDataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(pcmFile,true)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // new一个byte数组用来存一些字节数据，大小为缓冲区大小
            byte[] byteData = new byte[mBufferSizeInBytes];
            mAudioRecord.startRecording();
            while (mStatus == Status.STATUS_START && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                int bufferReadResult = mAudioRecord.read(byteData, 0, byteData.length);
                for (int i = 0; i < bufferReadResult; i++) {
                    mDataOutputStream.write(byteData[i]);
                }
            }
            mDataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //pcm文件转为wav文件
        int channel = 1, bitNUm = 16;
        if (AUDIO_CHANNEL == AudioFormat.CHANNEL_CONFIGURATION_MONO || AUDIO_CHANNEL == AudioFormat.CHANNEL_IN_MONO) {
            channel = 1;
        } else if (AUDIO_CHANNEL == AudioFormat.CHANNEL_CONFIGURATION_STEREO || AUDIO_CHANNEL == AudioFormat.CHANNEL_IN_STEREO) {
            channel = 2;
        }
        if (AUDIO_ENCODING == AudioFormat.ENCODING_PCM_16BIT) {
            bitNUm = 16;
        } else if (AUDIO_ENCODING == AudioFormat.ENCODING_PCM_8BIT) {
            bitNUm = 8 ;
        } else if (AUDIO_ENCODING == AudioFormat.ENCODING_PCM_FLOAT) {
            bitNUm = 32;
        }
        WavUtils.convertPcm2Wav(mPcmPath, mWavPath, AUDIO_SAMPLE_RATE, channel, bitNUm);
    }

    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }

    /**
     * 获取录音对象的状态
     *
     * @return
     */
    public Status getStatus() {
        return mStatus;
    }


//    public RecordStreamListener getListener() {
//        return listener;
//    }

//    public void setListener(RecordStreamListener listener) {
//        this.listener = listener;
//    }

}
