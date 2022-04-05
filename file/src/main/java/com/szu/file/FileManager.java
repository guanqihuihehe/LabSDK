package com.szu.file;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.szu.file.bean.AppInfo;
import com.szu.file.bean.FileBean;
import com.szu.file.bean.ImgFolderBean;
import com.szu.file.bean.Music;
import com.szu.file.bean.Video;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileManager {

    private static FileManager mInstance;
    private static Context mContext;
    private static ContentResolver mContentResolver;
    private static Object mLock = new Object();

    private FileManager() {

    }

    public static FileManager getInstance(Context context){
        if (mInstance == null){
            synchronized (mLock){
                if (mInstance == null){
                    mInstance = new FileManager();
                    mContext = context;
                    mContentResolver = context.getContentResolver();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取app的内部储存路径。
     * 手机上正常不可见，需要root之后才可见。不推荐使用这个路径来手动储存文件。
     * */
    public String getInternalFilePath() {
        Log.e("getInternalFilePath: ", mContext.getFilesDir().getAbsolutePath());
        return mContext.getFilesDir().getAbsolutePath();
    }

    /**
     * 获取app的外部储存路径。
     * 手机上正常可见，手动储存文件推荐使用这个路径。
     * */
    public String getExternalFilePath() {
        String filePath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                Log.e("Build.VERSION.SDK_INT: ", String.valueOf(Build.VERSION.SDK_INT));
                filePath = mContext.getExternalFilesDir(null).getAbsolutePath();
                Log.e("getExternalFilePath: ", filePath);
            } else {
                Log.e("getExternalFilePath: ", "Android 8 之前不支持获取私有储存空间,现在返回的是内部储存路径");
                filePath = mContext.getFilesDir().getPath();
            }
        } else {
            Log.e("getExternalFilePath: ", "外部存储不可用,现在返回的是内部储存路径");
            filePath = mContext.getFilesDir().getPath();
        }
        return filePath;
    }

    /**
     * 获取app的内部缓存路径。
     * 手机上正常不可见，需要root之后才可见。不推荐使用这个路径来手动储存文件。
     * */
    public String getInternalCachePath() {
        Log.e("getInternalFilePath: ", mContext.getCacheDir().getAbsolutePath());
        return mContext.getCacheDir().getAbsolutePath();
    }

    /**
     * 获取app的外部缓存路径。
     * 手机上正常可见。不推荐使用这个路径来手动储存文件。
     * */
    public String getExternalCachePath() {
        String cachePath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                Log.e("getExternalFilePath: ", mContext.getExternalCacheDir().getAbsolutePath());
                cachePath = mContext.getExternalCacheDir().getAbsolutePath();
            } else {
                Log.e("getExternalFilePath: ", "Android 8 之前不支持获取私有储存空间");
                cachePath = mContext.getCacheDir().getPath();
            }
        } else {
            Log.e("getExternalFilePath: ", "外部存储不可用，现在返回的是内部储存路径");
            cachePath = mContext.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 获取手机储存的根路径。
     * 手机上正常可见。不推荐在这个路径之下新建目录来手动储存文件。
     * */
    public String getExternalStoragePath() {
        String storagePath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = Environment.getExternalStorageDirectory();
            storagePath = file.getAbsolutePath();
            Log.e("getExternalStoragePath", storagePath);
        } else {
            Log.e("getExternalFilePath: ", "外部存储不可用");
        }
        return storagePath;
    }

    /**
     * 在APP专属目录下创建文件夹
     * */
    public void createDir(String dirName) {
        String parentPath = getExternalFilePath();
        createDir(dirName, parentPath);
    }

    /**
     * 在指定路径下创建文件夹
     * */
    public void createDir(String dirName, String parentPath) {
        verifyAudioPermissions((Activity) mContext);
        String appPath = getExternalFilePath();
        if (!parentPath.startsWith(appPath)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.e("createDir", "android 10及以上版本，应用只能访问外部存储空间上的应用专属目录");
                return;
            }
        }

        File dir = new File(parentPath+"/"+dirName);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            Log.e("createDir", String.valueOf(result));
        }
    }

    /**
     * 在APP专属目录下创建文件
     * */
    public void createFile(String fileName) {
        String parentPath = getExternalFilePath();
        createFile(fileName, parentPath);
    }

    /**
     * 在指定路径下创建文件
     * */
    public void createFile(String fileName, String parentPath) {
        verifyAudioPermissions((Activity) mContext);
        String appPath = getExternalFilePath();
        if (!parentPath.startsWith(appPath)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.e("createDir", "android 10及以上版本，应用只能访问外部存储空间上的应用专属目录");
                return;
            }
        }

        File file = new File(parentPath+"/"+fileName);
        File parentFile = new File(parentPath);
        if (!parentFile.exists()) {
            boolean result = parentFile.mkdirs();
            Log.e("createParentFile", String.valueOf(result));
        }
        if (!file.exists()) {
            boolean result = false;
            try {
                result = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("createFile", String.valueOf(result));
        } else {
            Log.e("createFile","文件已存在");
        }
    }

    /**
     * 在APP专属目录下删除文件
     * */
    public void deleteFile(String fileName) {
        String parentPath = getExternalFilePath();
        deleteFile(fileName, parentPath);
    }

    /**
     * 在指定路径下删除文件
     * */
    public void deleteFile(String fileName, String parentPath) {
        verifyAudioPermissions((Activity) mContext);
        String appPath = getExternalFilePath();
        if (!parentPath.startsWith(appPath)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.e("deleteFile", "android 10及以上版本，应用只能访问外部存储空间上的应用专属目录");
                return;
            }
        }

        File file = new File(parentPath+"/"+fileName);
        File parentFile = new File(parentPath);
        if (!parentFile.exists()) {
            Log.e("deleteFile", "路径不存在");
            return;
        }
        if (!file.exists()) {
            Log.e("deleteFile","文件不存在");
        } else {
            boolean result = file.delete();
            Log.e("deleteFile",String.valueOf(result));
        }
    }

    /**
     * 在APP专属目录下删除文件夹
     * */
    public void deleteDir(String dirName) {
        String parentPath = getExternalFilePath();
        deleteDir(dirName, parentPath);
    }

    /**
     * 在指定路径下删除文件夹
     * */
    public void deleteDir(String dirName, String parentPath) {
        verifyAudioPermissions((Activity) mContext);
        String appPath = getExternalFilePath();
        if (!parentPath.startsWith(appPath)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Log.e("deleteDir", "android 10及以上版本，应用只能访问外部存储空间上的应用专属目录");
                return;
            }
        }

        File file = new File(parentPath+"/"+dirName);
        File parentFile = new File(parentPath);
        if (!parentFile.exists()) {
            Log.e("deleteDir", "路径不存在");
            return;
        }
        if (!file.exists()) {
            Log.e("deleteDir","目录不存在");
        } else if (!file.isDirectory()) {
            Log.e("deleteDir","待删除的不是一个目录");
        } else {
            boolean result = file.delete();
            Log.e("deleteDir", String.valueOf(result));
        }
    }


    /**获取文件夹下文件列表*/
    public void getFileList(String parentPath) {
        getFileList(parentPath, null);
    }

    /**获取文件夹下文件列表*/
    public ArrayList<String> getFileList(String parentPath, String fileType) {
        ArrayList<String>  result = new ArrayList<String>();
        File file = new File(parentPath);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (!files[i].isDirectory()) {
                String fileName = files[i].getName();
                if (fileType != null) {
                    if (fileName.trim().toLowerCase().endsWith(fileType)) {
                        result.add(fileName);
                    }
                } else {
                    result.add(fileName);
                }
            }
        }
        return result;
    }

    /*申请读写文件权限*/
    private static final int GET_WRITE_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSION_FILE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /*
     * 申请读写文件权限*/
    public static void verifyAudioPermissions(Activity activity) {
        int permissionWrite = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_FILE,
                    GET_WRITE_EXTERNAL_STORAGE);
        }
    }




    /**
     * 获取本机音乐列表
     * @return
     */
    public List<Music> getMusics() {
        ArrayList<Music> musics = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));// 路径

                if (!new File(path).exists()) {
                    continue;
                }

                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)); // 歌曲名
                String album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)); // 专辑
                String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)); // 作者
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));// 大小
                int duration = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));// 时长
                int time = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));// 歌曲的id
                // int albumId = c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                Music music = new Music(name, path, album, artist, size, duration);
                musics.add(music);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return musics;
    }

    /**
     * 获取本机视频列表
     * @return
     */
    public List<Video> getVideos() {

        List<Video> videos = new ArrayList<Video>();

        Cursor c = null;
        try {
            // String[] mediaColumns = { "_id", "_data", "_display_name",
            // "_size", "date_modified", "duration", "resolution" };
            c = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                if (!new File(path).exists()) {
                    continue;
                }

                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));// 视频的id
                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 视频名称
                String resolution = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)); //分辨率
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
                long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));// 时长
                long date = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));//修改时间

                Video video = new Video(id, path, name, resolution, size, date, duration);
                videos.add(video);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return videos;
    }

    // 获取视频缩略图
    public Bitmap getVideoThumbnail(int id) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, options);
        }
        return bitmap;
    }

    /**
     * 通过文件类型得到相应文件的集合
     **/
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public List<FileBean> getFilesByType(int fileType) {
        List<FileBean> files = new ArrayList<FileBean>();
        // 扫描files文件库
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Files.getContentUri("external"), new String[]{"_id", "_data", "_size"}, null, null, null);
            int dataindex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            int sizeindex = c.getColumnIndex(MediaStore.Files.FileColumns.SIZE);

            while (c.moveToNext()) {
                String path = c.getString(dataindex);

                if (FileUtils.getFileType(path) == fileType) {
                    if (!FileUtils.isExists(path)) {
                        continue;
                    }
                    long size = c.getLong(sizeindex);
                    FileBean fileBean = new FileBean(path);
                    files.add(fileBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return files;
    }

    /**
     * 得到图片文件夹集合
     */
    public List<ImgFolderBean> getImageFolders() {
        List<ImgFolderBean> folders = new ArrayList<ImgFolderBean>();
        // 扫描图片
        Cursor c = null;
        try {
            c = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    MediaStore.Images.Media.MIME_TYPE + "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ?",
                    new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
            List<String> mDirs = new ArrayList<String>();//用于保存已经添加过的文件夹目录
            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));// 路径
                File parentFile = new File(path).getParentFile();
                if (parentFile == null)
                    continue;

                String dir = parentFile.getAbsolutePath();
                if (mDirs.contains(dir))//如果已经添加过
                    continue;

                mDirs.add(dir);//添加到保存目录的集合中
                ImgFolderBean folderBean = new ImgFolderBean();
                folderBean.setDir(dir);
                folderBean.setFistImgPath(path);
                if (parentFile.list() == null)
                    continue;
                int count = Objects.requireNonNull(parentFile.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if (filename.endsWith(".jpeg") || filename.endsWith(".jpg") || filename.endsWith(".png")) {
                            return true;
                        }
                        return false;
                    }
                })).length;

                folderBean.setCount(count);
                folders.add(folderBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return folders;
    }

    /**
     * 通过图片文件夹的路径获取该目录下的图片
     */
    public List<String> getImgListByDir(String dir) {
        ArrayList<String> imgPaths = new ArrayList<>();
        File directory = new File(dir);
        if (directory == null || !directory.exists()) {
            return imgPaths;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            String path = file.getAbsolutePath();
            if (FileUtils.isPicFile(path)) {
                imgPaths.add(path);
            }
        }
        return imgPaths;
    }

    /**
     * 获取已安装apk的列表
     */
    public List<AppInfo> getAppInfos() {

        ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
        //获取到包的管理者
        PackageManager packageManager = mContext.getPackageManager();
        //获得所有的安装包
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

        //遍历每个安装包，获取对应的信息
        for (PackageInfo packageInfo : installedPackages) {

            AppInfo appInfo = new AppInfo();

            appInfo.setApplicationInfo(packageInfo.applicationInfo);
            appInfo.setVersionCode(packageInfo.versionCode);

            //得到icon
            Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(drawable);

            //得到程序的名字
            String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setApkName(apkName);

            //得到程序的包名
            String packageName = packageInfo.packageName;
            appInfo.setApkPackageName(packageName);

            //得到程序的资源文件夹
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            //得到apk的大小
            long size = file.length();
            appInfo.setApkSize(size);

            System.out.println("---------------------------");
            System.out.println("程序的名字:" + apkName);
            System.out.println("程序的包名:" + packageName);
            System.out.println("程序的大小:" + size);


            //获取到安装应用程序的标记
            int flags = packageInfo.applicationInfo.flags;

            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //表示系统app
                appInfo.setIsUserApp(false);
            } else {
                //表示用户app
                appInfo.setIsUserApp(true);
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                //表示在sd卡
                appInfo.setIsRom(false);
            } else {
                //表示内存
                appInfo.setIsRom(true);
            }


            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
