package com.szu.video_record;

public interface IVideoListener {
    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
