package com.szu.audio_record;

import android.content.Context;

public interface IAudioRecordService {
    public void initRecorder(Context context, String fileName, String rootPath, int audioSource, int sampleRateInHz, int channelConfig, int audioEncoding);

    public void initRecorder(Context context, String fileName, String rootPath);

    public void startRecording(Context context);

    public void stopRecording(Context context);

    public void restartRecording(Context context);

    public void pauseRecording(Context context);

    public void cancelRecording(Context context);
}
