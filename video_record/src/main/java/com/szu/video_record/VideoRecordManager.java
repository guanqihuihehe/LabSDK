package com.szu.video_record;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;

public class VideoRecordManager {
    private static final String TAG = "VideoRecord";
    private MediaRecorder mRecorder;
    private Camera mCamera;
    private IVideoListener mVideoListener = null;
    public VideoRecordManager() {

    }

    /**
     * 初始化录制
     */
    public boolean initVideoRecord(VideoRecorderConfig config, IVideoListener videoListener) {
        MediaRecorder.OnErrorListener errorListener;
        if (videoListener != null) {
            mVideoListener = videoListener;
            errorListener = new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int what, int extra) {
                    Log.e(TAG, "来自MediaRecorder.OnErrorListener的失败: " + what);
                    videoListener.onFailed(what, "来自MediaRecorder.OnErrorListener的失败");
                }
            };
        } else {
            errorListener = new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mediaRecorder, int what, int extra) {
                    Log.e(TAG, "来自MediaRecorder.OnErrorListener的失败: " + what);
                }
            };
        }

        if (config == null || !config.checkParam()) {
            Log.e(TAG, "参数错误");
            if (mVideoListener != null) {
                mVideoListener.onFailed(-1, "参数错误");
            }
            return false;
        }

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        mRecorder.setOnErrorListener(errorListener);
        mRecorder.reset();

        mCamera = config.getCamera();
        if (mCamera == null) {
            mCamera = Camera.open();
        }

        mRecorder.setCamera(mCamera);

        //设置音频通道
//        mRecorder.setAudioChannels(config.getAudioChannels());
        //声音源
        mRecorder.setAudioSource(config.getAudioSource());
        //视频源
        mRecorder.setVideoSource(config.getVideoSource());

        try {
            //推荐使用以下代码进行参数配置
            CamcorderProfile bestCamcorderProfile = config.getProfile();
            if (bestCamcorderProfile == null) {
                bestCamcorderProfile = getBestCamcorderProfile(config.getCameraId());
            }
            mRecorder.setProfile(bestCamcorderProfile);
        } catch (Exception e) {
            //设置输出格式
            mRecorder.setOutputFormat(config.getOutputFormat());
            //声音编码格式
            mRecorder.setAudioEncoder(config.getAudioEncoder());
            //视频编码格式
            mRecorder.setVideoEncoder(config.getVideoEncoder());
        }

        //设置视频的长宽(分辨率)
        mRecorder.setVideoSize(config.getVideoWidth(), config.getVideoHeight());
//        设置取样帧率
//        mRecorder.setVideoFrameRate(config.getVideoFrameRate());
        //设置比特率（比特率越高质量越高同样也越大）
//        mRecorder.setAudioEncodingBitRate(config.getAudioEncodingBitRate());
//        设置比特率（比特率越高质量越高同样也越大）
//        mRecorder.setVideoEncodingBitRate(config.getVideoEncodingBitRate());
//        这里是调整旋转角度（前置和后置的角度不一样）
        mRecorder.setOrientationHint(config.getCameraRotation());
//        设置记录会话的最大持续时间（毫秒）
//        mRecorder.setMaxDuration(15 * 1000);

        String path = config.getPath();
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            boolean result = file.getParentFile().mkdirs();
            if (!result) {
                if (mVideoListener != null) {
                    mVideoListener.onFailed(-1, "创建保存路径的文件夹失败");
                }
                return false;
            }
        }
        //设置输出的文件路径
        mRecorder.setOutputFile(path);
        //设置预览对象（可以使用SurfaceHolder代替）
        mRecorder.setPreviewDisplay(new Surface(config.getSurfaceTexture()));

        return true;

    }

    /**
     * 开始录制
     */
    public boolean startRecord() {
        try {
            mCamera.unlock();
            //预处理
            mRecorder.prepare();
            //开始录制
            mRecorder.start();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            if (mVideoListener != null) {
                mVideoListener.onFailed(-1, e.getMessage());
            }
            return false;
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
                if (mVideoListener != null) {
                    mVideoListener.onFailed(-1, e.getMessage());
                }
                Log.e(TAG, e.getMessage());
            }

        }
    }

    /**
     * 暂停录制
     *
     * @return
     */
    public boolean pause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mRecorder != null) {
            mRecorder.pause();
            return true;
        }
        return false;
    }

    /**
     * 继续录制
     *
     * @return
     */
    public boolean resume() {
        if (mRecorder != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRecorder.resume();
            return true;
        }
        return false;
    }

    private CamcorderProfile getBestCamcorderProfile(int cameraID) {
        CamcorderProfile profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_HIGH);
        if (CamcorderProfile.hasProfile(cameraID, CamcorderProfile.QUALITY_2160P)) {
            profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_2160P);
            return profile;
        }
        if (CamcorderProfile.hasProfile(cameraID, CamcorderProfile.QUALITY_1080P)) {
            profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_1080P);
            return profile;
        }
        if (CamcorderProfile.hasProfile(cameraID, CamcorderProfile.QUALITY_720P)) {
            //对比480p 这个选择 动作大时马赛克!!
            profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_720P);
            return profile;
        }
        if (CamcorderProfile.hasProfile(cameraID, CamcorderProfile.QUALITY_480P)) {
            //对比720p 这个选择 每帧不是很清晰
            profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_480P);
            return profile;
        }
        if (CamcorderProfile.hasProfile(cameraID, CamcorderProfile.QUALITY_CIF)) {
            profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_CIF);
            return profile;
        }
        if (CamcorderProfile.hasProfile(cameraID, CamcorderProfile.QUALITY_QVGA)) {
            profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_QVGA);
            return profile;
        }
        return profile;
    }
}