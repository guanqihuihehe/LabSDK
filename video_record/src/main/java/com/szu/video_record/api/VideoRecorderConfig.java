package com.szu.video_record.api;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.view.Surface;

public class VideoRecorderConfig {

    //Camera
    private Camera camera = null;

    //摄像头预览宽度(设置录制预览时必须设置）
    private int videoWidth;

    //摄像头预览高度(设置录制预览时必须设置）
    private int videoHeight;

    //摄像头预览偏转角度
    private int cameraRotation = 0;

    //保存的文件路径(必须设置)
    private String path;

    //由于Camera使用的是SurfaceTexture，所以这里使用了SurfaceTexture
    //也可使用SurfaceHolder
    private SurfaceTexture surfaceTexture;

    //cameraId
    private int cameraId = 0;

    //取样帧率
    private int videoFrameRate = 30;

    //比特率（比特率越高质量越高同样也越大）
    private int audioEncodingBitRate = 44100;

    //比特率（比特率越高质量越高同样也越大）
    private int videoEncodingBitRate = 800 * 1024;

    //输出格式
    private int outputFormat = MediaRecorder.OutputFormat.DEFAULT;

    //声音编码格式
    private int audioEncoder = MediaRecorder.AudioEncoder.DEFAULT;

    //视频编码格式
    private int videoEncoder = MediaRecorder.VideoEncoder.DEFAULT;

    //设置音频通道
    private int audioChannels = 1;

    //声音源
    private int audioSource = MediaRecorder.AudioSource.DEFAULT;

    //视频源
    private int videoSource = MediaRecorder.VideoSource.CAMERA;

    //设置视频的输出格式和编码
    //在Android 2.2 之后，建议使用CamcorderProfile取代outputFormat、audioEncoder、videoEncoder
    private CamcorderProfile profile = null;


    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getCameraRotation() {
        return cameraRotation;
    }

    public void setCameraRotation(int cameraRotation) {
        this.cameraRotation = cameraRotation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public boolean checkParam() {
        return surfaceTexture != null && camera != null && videoWidth > 0 && videoHeight > 0 && !TextUtils.isEmpty(path);
    }

    public int getVideoFrameRate() {
        return videoFrameRate;
    }

    public void setVideoFrameRate(int videoFrameRate) {
        this.videoFrameRate = videoFrameRate;
    }

    public int getAudioEncodingBitRate() {
        return audioEncodingBitRate;
    }

    public void setAudioEncodingBitRate(int audioEncodingBitRate) {
        this.audioEncodingBitRate = audioEncodingBitRate;
    }

    public int getVideoEncodingBitRate() {
        return videoEncodingBitRate;
    }

    public void setVideoEncodingBitRate(int videoEncodingBitRate) {
        this.videoEncodingBitRate = videoEncodingBitRate;
    }

    public int getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(int outputFormat) {
        this.outputFormat = outputFormat;
    }

    public int getAudioEncoder() {
        return audioEncoder;
    }

    public void setAudioEncoder(int audioEncoder) {
        this.audioEncoder = audioEncoder;
    }

    public int getVideoEncoder() {
        return videoEncoder;
    }

    public void setVideoEncoder(int videoEncoder) {
        this.videoEncoder = videoEncoder;
    }

    public CamcorderProfile getProfile() {
        return profile;
    }

    public void setProfile(CamcorderProfile profile) {
        this.profile = profile;
    }

    public int getAudioChannels() {
        return audioChannels;
    }

    public void setAudioChannels(int audioChannels) {
        this.audioChannels = audioChannels;
    }

    public int getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(int audioSource) {
        this.audioSource = audioSource;
    }

    public int getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(int videoSource) {
        this.videoSource = videoSource;
    }
}