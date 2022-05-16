package com.szu.audio_record_demo;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.szu.audio_record.impl.AudioRecordService;
import com.szu.audio_record.api.IAudioRecordService;

import java.io.File;


public class AudioRecordTestActivity extends AppCompatActivity {
    String mRecordFileName = "";
    String mRootPath = "";

    EditText mRecordNameEditText;
    IAudioRecordService mAudioRecordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record_test);
        mRootPath = getPathFromStorage();
        mAudioRecordService = new AudioRecordService();
        initUI();
    }

    public void initUI() {

        mRecordNameEditText = findViewById(R.id.record_name);
        TextView rootPathTextView = findViewById(R.id.root_path);

        rootPathTextView.setText(mRootPath);
        
        findViewById(R.id.record).setOnClickListener(v -> {
            setFileName();
            doDefaultRecord(mRecordFileName);
        });
        findViewById(R.id.stop_record).setOnClickListener(v -> {
            mAudioRecordService.stopRecording(AudioRecordTestActivity.this);
        });
        findViewById(R.id.pause_record).setOnClickListener(v -> {
            mAudioRecordService.pauseRecording(AudioRecordTestActivity.this);
        });
        findViewById(R.id.restart_record).setOnClickListener(v -> {
            mAudioRecordService.restartRecording(AudioRecordTestActivity.this);
        });
        findViewById(R.id.cancel_record).setOnClickListener(v -> {
            mAudioRecordService.cancelRecording(AudioRecordTestActivity.this);
        });

    }

    public void setFileName() {
        mRecordFileName = mRecordNameEditText.getText().toString();
    }

    public void doDefaultRecord(String fileName) {
        if (fileName.equals("")) {
            Toast.makeText(AudioRecordTestActivity.this,"文件名为空",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AudioRecordTestActivity.this, fileName, Toast.LENGTH_SHORT).show();
            mAudioRecordService.initRecorder(AudioRecordTestActivity.this, fileName, mRootPath);
            mAudioRecordService.startRecording(AudioRecordTestActivity.this);
        }
    }

    public void doRecord (String fileName, int audioSource, int sampleRateInHz, int channelConfig, int audioEncoding) {
        if (fileName.equals("")) {
            Toast.makeText(AudioRecordTestActivity.this,"文件名为空",Toast.LENGTH_SHORT).show();
        } else {
            mAudioRecordService.initRecorder(AudioRecordTestActivity.this, fileName, mRootPath, audioSource, sampleRateInHz, channelConfig, audioEncoding);
            mAudioRecordService.startRecording(AudioRecordTestActivity.this);
        }
    }


    public void doStopRecord() {
        mAudioRecordService.stopRecording(AudioRecordTestActivity.this);
    }

    /**
     * 获取当前根目录的绝对路径
     */
    public String getPathFromStorage() {
        File file = this.getExternalFilesDir("Audio");
        if (!file.exists()) {
            try {
                file.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取当前raw文件目录的绝对路径
     */
    public String getPathFromRaw() {
        return null;
    }


}