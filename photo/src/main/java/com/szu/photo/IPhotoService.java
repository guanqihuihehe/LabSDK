package com.szu.photo;

public interface IPhotoService {

    /**
     * 调用此接口设置回调
     * */
    void setListener(IPhotoListener photoListener);

    /**
     * 调用此接口进行拍照
     * */
    void takePhoto();//三星手机在使用bitmap展示照片时，可能会出现照片被旋转的问题。

    /**
     * 调用此接口进行拍照
     * */
    void takePhoto(String fileName);

    /**
     * 调用此接口从相册选择图片
     * */
    void choosePhoto();
}
