package com.szu.audio_record.api;

import android.content.Context;

public interface IAudioRecordService {

     void initRecorder(Context context, String fileName, String rootPath, int audioSource, int sampleRateInHz, int channelConfig, int audioEncoding);

     void initRecorder(Context context, String fileName, String rootPath);

     void startRecording(Context context);

     void stopRecording(Context context);

     void restartRecording(Context context);

     void pauseRecording(Context context);

     void cancelRecording(Context context);
}
