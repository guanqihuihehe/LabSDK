package com.szu.photo;

public interface IPhotoListener {
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_FIRST_USER = 1;
    public static final int RESULT_OK = -1;

    /**
     * 回调拍照的结果
     * @param resultCode = -1代表拍照成功。
     * @param photoPath 成功时返回照片的绝对路径，不成功时返回null
     * */
    void onTakePhotoResult(int resultCode, String photoPath);

    /**
     * 回调从相册选择照片的结果
     * @param resultCode = -1代表拍照成功。
     * @param photoPath 成功时返回照片的绝对路径，不成功时返回null
     * */
    void onChoosePhotoResult(int resultCode, String photoPath);
}
