package com.szu.download.impl;

public interface DownloadListenerInterface {

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();

}
