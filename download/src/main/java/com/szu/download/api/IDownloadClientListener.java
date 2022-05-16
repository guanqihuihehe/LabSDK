package com.szu.download.api;

public interface IDownloadClientListener {

    /**回调下载进度，progress的值代表已下载的百分比*/
    void onProgress(int progress);

    /**开始下载时回调，downloadID代表本次下载任务的ID，暂停或者取消时需要使用此ID作为入参*/
    void onStart(int downloadID);

    /**下载成功时回调，downloadID代表本次下载任务的ID*/
    void onSuccess(int downloadID);

    /**result为0时，表示暂停成功*/
    void onFailed(int downloadID, int errorCode, String errorMsg);

    /**result为0时，表示暂停成功*/
    void onPaused(int result);

    /**result为0时，表示取消成功*/
    void onCanceled(int result);

}
