package com.szu.bluetooth;

public interface IDiscoveryCallback {
    int ACTION_DISCOVERY_START = 0;
    int ACTION_DISCOVERY_FOUND = 1;
    int ACTION_DISCOVERY_FINISH = 2;
    void onResult(int actionCode);
}
