package com.szu.upload;

public interface IUploadListener {
    void onStart();

    void onResult(int resultCode, String resultMsg);

    void onFailed(int errorCode, String errorMsg);
}
