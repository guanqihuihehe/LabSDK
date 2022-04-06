package com.szu.upload;

public interface IUploadService {
    void startUpload(String srcPath, String uploadUrl, IUploadListener uploadListener);
}
