//package com.szu.labsdk;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.media.AudioFormat;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.szu.audio_play.AudioPlayService;
//import com.szu.audio_play.IAudioPlayService;
//import com.szu.audio_record.AudioRecordService;
//import com.szu.audio_record.IAudioRecordService;
//
//import java.io.File;
//
//
//public class AudioTestActivity  extends AppCompatActivity {
//    String mRecordFileName = "";
//    String mPlayFileName = "";
//    String mRootPath = "";
//    String defaultPlayFile = "Tx_signal_60s.wav";
//
//    EditText mRecordNameEditText;
//    EditText mPlayNameEditText;
//    IAudioRecordService mAudioRecordService;
//    IAudioPlayService mAudioPlayService;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_audio_test);
//        mRootPath = getPathFromStorage();
//        mAudioRecordService = new AudioRecordService();
//        mAudioPlayService = new AudioPlayService();
//        initUI();
//    }
//
//    public void initUI() {
//
//        mRecordNameEditText = findViewById(R.id.record_name);
//        mPlayNameEditText = findViewById(R.id.play_name);
//        TextView rootPathTextView = findViewById(R.id.root_path);
//
//        rootPathTextView.setText(mRootPath);
//
//        findViewById(R.id.play).setOnClickListener(v -> {
//            setFileName();
//            doAudioPlayerPlay();
//        });
//        findViewById(R.id.stop_play).setOnClickListener(v -> {
//            doStopPlay();
//        });
//        findViewById(R.id.pause_play).setOnClickListener(v -> {
//            mAudioPlayService.pausePlay();
//        });
//        findViewById(R.id.restart_play).setOnClickListener(v -> {
//            mAudioPlayService.restartPlay();
//        });
//        findViewById(R.id.ear_phone_play).setOnClickListener(v -> {
//            setFileName();
//            String playPath = mRootPath + "/" + mPlayFileName;
//            Toast.makeText(AudioTestActivity.this, "正在播放："+playPath, Toast.LENGTH_SHORT).show();
//            mAudioPlayService.initPlayer(playPath,1);
//            mAudioPlayService.startEarPhonePlay(AudioTestActivity.this);
//        });
//        findViewById(R.id.record).setOnClickListener(v -> {
//            setFileName();
//            doDefaultRecord(mRecordFileName);
//        });
//        findViewById(R.id.stop_record).setOnClickListener(v -> {
//            mAudioRecordService.stopRecording(AudioTestActivity.this);
//        });
//        findViewById(R.id.stop_record_play).setOnClickListener(v -> {
//            doStopRecord();
//            doStopPlay();
//        });
//        findViewById(R.id.pause_record).setOnClickListener(v -> {
//            mAudioRecordService.pauseRecording(AudioTestActivity.this);
//        });
//        findViewById(R.id.restart_record).setOnClickListener(v -> {
//            mAudioRecordService.restartRecording(AudioTestActivity.this);
//        });
//        findViewById(R.id.cancel_record).setOnClickListener(v -> {
//            mAudioRecordService.cancelRecording(AudioTestActivity.this);
//        });
//
//        findViewById(R.id.both_record_mono).setOnClickListener(v -> {
//            setFileName();
//            doAudioPlayerPlay();
//            doRecord(mRecordFileName,
//                    MediaRecorder.AudioSource.DEFAULT,
//                    48000,
//                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT);
//        });
//
//        findViewById(R.id.both_record_stereo).setOnClickListener(v -> {
//            setFileName();
//            doAudioPlayerPlay();
//            doRecord(mRecordFileName,
//                    MediaRecorder.AudioSource.DEFAULT,
//                    48000,
//                    AudioFormat.CHANNEL_CONFIGURATION_STEREO,
//                    AudioFormat.ENCODING_PCM_16BIT);
//        });
//
//        findViewById(R.id.bottom_record_mono).setOnClickListener(v -> {
//            setFileName();
//            doAudioPlayerPlay();
//            doRecord(mRecordFileName,
//                    MediaRecorder.AudioSource.MIC,
//                    48000,
//                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT);
//        });
//
//        findViewById(R.id.bottom_record_stereo).setOnClickListener(v -> {
//            setFileName();
//            doAudioPlayerPlay();
//            doRecord(mRecordFileName,
//                    MediaRecorder.AudioSource.MIC,
//                    48000,
//                    AudioFormat.CHANNEL_CONFIGURATION_STEREO,
//                    AudioFormat.ENCODING_PCM_16BIT);
//        });
//
//        findViewById(R.id.audio_track_play).setOnClickListener(v -> {
//            setFileName();
//            String playPath = mRootPath + "/" + mPlayFileName;
//            Toast.makeText(AudioTestActivity.this, "正在播放："+playPath, Toast.LENGTH_SHORT).show();
//
//            mAudioPlayService.initPlayer(playPath,2);
//            mAudioPlayService.startPlay();
//        });
//
//        findViewById(R.id.audio_track_stop).setOnClickListener(v -> {
//            mAudioPlayService.stopPlay();
//        });
//
//        findViewById(R.id.jump).setOnClickListener(v -> {
//            Intent intent = new Intent(AudioTestActivity.this, TestActivity.class);
//            AudioTestActivity.this.startActivity(intent);
//        });
//    }
//
//    public void setFileName() {
//        mRecordFileName = mRecordNameEditText.getText().toString();
//        String tempPlayName = mPlayNameEditText.getText().toString();
//        if (!tempPlayName.equals("")) {
//            mPlayFileName = tempPlayName;
//        } else {
//            mPlayFileName = defaultPlayFile;
//        }
//    }
//
//    public void doDefaultRecord(String fileName) {
//        if (fileName.equals("")) {
//            Toast.makeText(AudioTestActivity.this,"文件名为空",Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(AudioTestActivity.this, fileName, Toast.LENGTH_SHORT).show();
//            mAudioRecordService.initRecorder(AudioTestActivity.this, fileName, mRootPath);
//            mAudioRecordService.startRecording(AudioTestActivity.this);
//        }
//    }
//
//    public void doRecord (String fileName, int audioSource, int sampleRateInHz, int channelConfig, int audioEncoding) {
//        if (fileName.equals("")) {
//            Toast.makeText(AudioTestActivity.this,"文件名为空",Toast.LENGTH_SHORT).show();
//        } else {
//            mAudioRecordService.initRecorder(AudioTestActivity.this, fileName, mRootPath, audioSource, sampleRateInHz, channelConfig, audioEncoding);
//            mAudioRecordService.startRecording(AudioTestActivity.this);
//        }
//    }
//
//    public void doAudioPlayerPlay () {
//        String playPath = mRootPath + "/" + mPlayFileName;
//        Toast.makeText(AudioTestActivity.this, "正在播放："+playPath, Toast.LENGTH_SHORT).show();
//        mAudioPlayService.initPlayer(playPath,1);
//        mAudioPlayService.startPlay();
//    }
//
//    public void doStopRecord() {
//        mAudioRecordService.stopRecording(AudioTestActivity.this);
//    }
//
//    public void doStopPlay() {
//        mAudioPlayService.stopPlay();
//    }
//
//    /**
//     * 获取当前根目录的绝对路径
//     */
//    public String getPathFromStorage() {
//        File file = this.getExternalFilesDir("AudioRecord");
//        if (!file.exists()) {
//            try {
//                file.mkdir();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return file.getAbsolutePath();
//    }
//
//    /**
//     * 获取当前raw文件目录的绝对路径
//     */
//    public String getPathFromRaw() {
//        return null;
//    }
//
//
//}