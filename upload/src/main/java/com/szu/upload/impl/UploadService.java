package com.szu.upload.impl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.szu.upload.api.IUploadListener;
import com.szu.upload.api.IUploadService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadService implements IUploadService {

    private ExecutorService mExecutorService;

    public UploadService() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    @Override
    public void startUpload(Activity activity, String srcPath, String uploadUrl, IUploadListener uploadListener) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                requestUploadPermissions(activity);
                uploadFile(srcPath, uploadUrl, uploadListener);
            }
        });
    }


    /* POST方法上传文件至Server，uploadUrl：接收文件的处理页面 */
    private void uploadFile(String srcPath, String uploadUrl, IUploadListener uploadListener)
    {
        uploadListener.onStart();
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        try
        {
            URL url = new URL(uploadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
            // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                    + srcPath.substring(srcPath.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);

            FileInputStream fis = new FileInputStream(srcPath);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            // 读取文件
            while ((count = fis.read(buffer)) != -1)
            {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();

            uploadListener.onResult(0, result);

            dos.close();
            is.close();


        } catch (Exception e) {
            uploadListener.onFailed(-1, e.getMessage());
            e.printStackTrace();
        }
    }

    /*申请权限*/
    private static final int WRITE_EXTERNAL_STORAGE = 0;
    private static final int READ_EXTERNAL_STORAGE = 1;
    private static final int INTERNET = 2;
    private static String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    /*
     * 申请权限*/
    public static void requestUploadPermissions(Activity activity) {
        int permission1 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    WRITE_EXTERNAL_STORAGE);
        }

        int permission2 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    READ_EXTERNAL_STORAGE);
        }

        int permission3 = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.INTERNET);
        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS,
                    INTERNET);
        }
    }
}