package com.szu.upload;

import android.app.Activity;

public interface IUploadService {
    void startUpload(Activity activity, String srcPath, String uploadUrl, IUploadListener uploadListener);
}
