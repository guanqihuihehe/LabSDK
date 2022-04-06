package com.szu.download;

public interface IDownloadClientListener {

    void onProgress(int progress);

    void onStart(int downloadID);

    void onSuccess(int downloadID);

    void onFailed(int downloadID, int errorCode, String errorMsg);

    /**result为0时，表示暂停成功*/
    void onPaused(int result);

    /**result为0时，表示取消成功*/
    void onCanceled(int result);

}
