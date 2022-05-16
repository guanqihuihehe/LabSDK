package com.szu.upload.api;

import android.app.Activity;

public interface IUploadService {
    /**
     * 调用此接口进行文件上传。
     * @param activity 当前的activity
     * @param srcPath 待上传文件的本地路径
     * @param uploadUrl 上传的服务器url
     * @param uploadListener 上传的回调，回调上传结果
     * */
    void startUpload(Activity activity, String srcPath, String uploadUrl, IUploadListener uploadListener);
}
