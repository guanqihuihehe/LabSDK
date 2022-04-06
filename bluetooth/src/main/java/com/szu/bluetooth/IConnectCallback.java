package com.szu.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;

public interface IConnectCallback {
    /**蓝牙设备连接成功时回调，inputStream是本端连接的输入流，outputStream是本端连接的输出流*/
    void onSuccess(InputStream inputStream, OutputStream outputStream);

    /**蓝牙设备连接失败时回调，errorCode是错误码，errorMsg是错误信息*/
    void onFailed(int errorCode, String errorMsg);
}
