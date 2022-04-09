package com.szu.broadcast;

import android.app.Activity;

public interface IAudioPlayService {
    public void initPlayer(int playerType, String filePath, int sampleRateInHz, int channelConfig, int audioEncoding, int playMode, int streamType, boolean isLooping);

    public void initPlayer(String filePath, int playerType);

    public void startEarPhonePlay(Activity activity);

    public void startPlay();

    public void pausePlay();

    public void stopPlay();

    public void restartPlay();
}
