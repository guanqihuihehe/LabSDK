package com.szu.upload;

public interface IUploadListener {
    /**
     * 开始上传结果时回调
     * */
    void onStart();

    /**
     * 获得上传结果时回调，resultCode和resultMsg是服务端返回的内容。如果服务端没有返回，将默认返回0和空字符串。
     * */
    void onResult(int resultCode, String resultMsg);

    /**
     * 上传失败时回调，errorCode和errorMsg是错误码和错误信息。
     * */
    void onFailed(int errorCode, String errorMsg);
}
