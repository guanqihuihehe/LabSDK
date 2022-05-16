package com.szu.video_record.api;

public interface IVideoListener {
    void onSuccess();

    void onFailed(int errorCode, String errorMsg);
}
