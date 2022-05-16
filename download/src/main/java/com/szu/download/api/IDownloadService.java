package com.szu.download.api;

import android.content.Context;

public interface IDownloadService {

    /**
     * 开始下载
     * @param context 上下文
     * @param fileName 保存文件的路径
     * @param url 下载的链接
     * @param type 下载类型，图片类型为1
     * @param clientListener 回调，下载的状态会在此进行回调
     * */
    void startDownload(Context context, String fileName, String url, String path, int type, IDownloadClientListener clientListener);

    /**
     * 暂停下载
     * @param downloadID 下载任务的id，在回调onStart或者onFail或者onSuccess时获取到。
     * */
    void pauseDownload(int downloadID);

    /**
     * 恢复下载
     * @param downloadID 下载任务的id，在回调onStart或者onFail或者onSuccess时获取到。
     * */
    void resumeDownload(int downloadID);

    /**
     * 取消下载
     * @param downloadID 下载任务的id，在回调onStart或者onFail或者onSuccess时获取到。
     * */
    void cancelDownload(int downloadID);
}
