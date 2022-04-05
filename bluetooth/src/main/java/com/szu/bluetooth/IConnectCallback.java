package com.szu.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;

public interface IConnectCallback {
    void onSuccess(InputStream inputStream, OutputStream outputStream);
    void onFailed(int errorCode, String errorMsg);
}
