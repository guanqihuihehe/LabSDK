package com.szu.download;

import android.content.Context;

public interface IDownloadService {
    void startDownload(Context context, String fileName, String url, String path, int type, IDownloadClientListener clientListener);

    void pauseDownload(int downloadID);

    void resumeDownload(int downloadID);

    void cancelDownload(int downloadID);
}
