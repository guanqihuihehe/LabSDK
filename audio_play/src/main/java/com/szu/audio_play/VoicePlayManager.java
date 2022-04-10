package com.szu.audio_play;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 * 播放音乐控制类
 * 支持播放较短的音乐
 */
public class VoicePlayManager {

    private static volatile VoicePlayManager mInstance = null;
    private Context mContext;

    private VoicePlayManager(Context context) {
        this.mContext = context;
    }

    public static VoicePlayManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VoicePlayManager(context);
        }
        return mInstance;
    }


    private SoundPool soundPool = null;

    public void createSoundPool() {
        if (soundPool == null) {
            //实例化SoundPool

            //sdk版本21是SoundPool 的一个分水岭
            if (Build.VERSION.SDK_INT >= 21) {
                SoundPool.Builder builder = new SoundPool.Builder();
                builder.setMaxStreams(1);

                AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
                attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                //加载一个AudioAttributes
                builder.setAudioAttributes(attrBuilder.build());
                soundPool = builder.build();
            } else {
                soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            }

        }
    }

    public void playSoundPool(int resId) {

        createSoundPool();

        final int voiceId = soundPool.load(mContext, resId, 10000);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {

                    soundPool.play(voiceId, 1, 1, 10000, 0, 1);
                }
            }
        });
    }
}

