package com.szu.audio_play.impl;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Process;
import android.util.Log;


import com.szu.base.utils.ByteUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AudioTrackManager {
    private static final String TAG = AudioTrackManager.class.getSimpleName();
    private AudioTrack mAudioTrack;
    private DataInputStream mDataInputStream;//播放文件的数据流
    private Thread mRecordThread;

    //音频流类型
    private int mStreamType = AudioManager.STREAM_MUSIC;
    //指定采样率 （MediaRecorder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private int mSampleRateInHz = 48000;
    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private int mChannelConfig= AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    //指定音频量化位数 ,在AudioFormat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private int mAudioEncoding =AudioFormat.ENCODING_PCM_16BIT;//8bit的播放就把这个换成ENCODING_PCM_8BIT即可，逻辑代码不需要改动。
    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mMinBufferSize;
    //STREAM的意思是由用户在应用程序通过write方式把数据一次一次得写到AudioTrack中。这个和我们在socket中发送数据一样，
    // 应用层从某个地方获取数据，例如通过编解码得到PCM数据，然后write到AudioTrack。
    private int mPlayMode = AudioTrack.MODE_STREAM;
    private boolean mIsLooping = false;

    private String mPath="";

    public void setConfig(String filePath) {
        mPath = filePath;
    }

    public void setConfig(String filePath, int sampleRateInHz, int channelConfig, int audioEncoding, int playMode, int streamType, boolean isLooping) {
        mSampleRateInHz = sampleRateInHz;
        mChannelConfig = channelConfig;
        mAudioEncoding = audioEncoding;
        mPlayMode = playMode;
        mStreamType = streamType;
        mIsLooping = isLooping;
        mPath = filePath;
    }

    public void initAudioTrack() {
        //根据采样率，采样精度，单双声道来得到frame的大小。
        mMinBufferSize = AudioTrack.getMinBufferSize(mSampleRateInHz,mChannelConfig, mAudioEncoding);//计算最小缓冲区
        //注意，按照数字音频的知识，这个算出来的是一秒钟buffer的大小。

        File file = new File(mPath);
        try {
            mDataInputStream = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mAudioTrack = new AudioTrack(mStreamType, mSampleRateInHz, mChannelConfig,
                mAudioEncoding, mMinBufferSize, mPlayMode);
    }

    public void startPlay() {
        try {
            //AudioTrack未初始化
            if(mAudioTrack.getState() == AudioTrack.STATE_UNINITIALIZED){
                throw new RuntimeException("The AudioTrack is not uninitialized");
            }//AudioRecord.getMinBufferSize的参数是否支持当前的硬件设备
            else if (AudioTrack.ERROR_BAD_VALUE == mMinBufferSize || AudioTrack.ERROR == mMinBufferSize) {
                throw new RuntimeException("AudioTrack Unable to getMinBufferSize");
            }else{
                Log.d(TAG, "开始播放");
                startThread();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        try {
            destroyThread();//销毁线程
            if (mAudioTrack != null) {
                if (mAudioTrack.getState() == AudioRecord.STATE_INITIALIZED) {//初始化成功
                    mAudioTrack.stop();//停止播放
                }
                if (mAudioTrack != null) {
                    mAudioTrack.release();//释放audioTrack资源
                }
            }
            if (mDataInputStream != null) {
                mDataInputStream.close();//关闭数据输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    /**
//     * 获取单例引用
//     *
//     * @return
//     */
//    public static AudioTrackManager getInstance() {
//        if (mInstance == null) {
//            synchronized (AudioTrackManager.class) {
//                if (mInstance == null) {
//                    mInstance = new AudioTrackManager();
//                }
//            }
//        }
//        return mInstance;
//    }

    /**
     * 销毁线程
     */
    private void destroyThread() {
        try {
            if (null != mRecordThread && Thread.State.RUNNABLE == mRecordThread.getState()) {
                try {
                    Thread.sleep(500);
                    mRecordThread.interrupt();
                } catch (Exception e) {
                    mRecordThread = null;
                }
            }
            mRecordThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRecordThread = null;
        }
    }

    /**
     * 启动播放线程
     */
    private void startThread() {
        destroyThread();
        if (mRecordThread == null) {
            mRecordThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    doPlay();
                }
            });
            mRecordThread.start();
        }
    }

    private void doPlay() {
        try {
            //设置线程的优先级
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
            byte[] tempBuffer = new byte[mMinBufferSize];
            int readCount = 0;
            while (mDataInputStream.available() > 0) {
                readCount= mDataInputStream.read(tempBuffer);
                if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                    continue;
                }
                if (readCount != 0 && readCount != -1) {//一边播放一边写入语音数据
                    //判断AudioTrack未初始化，停止播放的时候释放了，状态就为STATE_UNINITIALIZED
                    if(mAudioTrack.getState() == mAudioTrack.STATE_UNINITIALIZED){
                        initAudioTrack();
                    }
                    mAudioTrack.play();
                    mAudioTrack.write(tempBuffer, 0, readCount);
                }
            }
            stopPlay();//播放完就停止播放
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //float的音频播放
    Runnable recordRunnable2 = new Runnable() {
        @Override
        public void run() {
            try {
                //设置线程的优先级
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                byte[] tempBuffer = new byte[mMinBufferSize*4];
                int readCount = 0;
                while (mDataInputStream.available() > 0) {
                    readCount= mDataInputStream.read(tempBuffer);
                    if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                        continue;
                    }
                    if (readCount != 0 && readCount != -1) {//一边播放一边写入语音数据
                        //判断AudioTrack未初始化，停止播放的时候释放了，状态就为STATE_UNINITIALIZED
                        if(mAudioTrack.getState() == mAudioTrack.STATE_UNINITIALIZED){
                            initAudioTrack();
                        }
                        float[] floats = ByteUtils.getFloats(tempBuffer);
                        mAudioTrack.play();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mAudioTrack.write(floats, 0, floats.length, AudioTrack.WRITE_NON_BLOCKING);
                        }
                    }
                }
                stopPlay();//播放完就停止播放
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };


}