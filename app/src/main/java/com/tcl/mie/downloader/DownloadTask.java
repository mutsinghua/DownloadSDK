package com.tcl.mie.downloader;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.mie.downloader.util.DLog;
import com.tcl.mie.downloader.util.FileUtil;
import com.tcl.mie.downloader.util.Tools;

import java.io.File;
import java.net.URI;
import java.util.Comparator;

/**
 * 下载结构体
 * Created by Rex on 2015/6/3.
 */
public class DownloadTask implements Comparable<DownloadTask>{

    public static final String TAG = "DownloadTask";


    //高优先级下载
    public static final int PRORITY_NORMAL = 0;

    //低优先级下载
    public static final int PRORITY_LOW = 1;

    //排除wifi鉴权的干扰
    public static final String[] ExceptionContentType = new String[] {"text","html"};

    public String mName;

    /**
     * 加入队列的任务
     */
    public int mSequence;

    /**
     * 下载项索引关键字，对于apk，可以是包名
     */
    public String mKey;

    /**
     * 下载地址
     */
    public String mUrl;

    /**
     * 文件总大小
     */
    public long mFileTotalSize;

    /**
     * 当前下载大小
     */
    public long mFileDownloadedSize;

    /**
     * 下载路径
     */
    public String mLocalPath;

    /**
     * 文件名
     */
    public String mFileName;

    /**
     * 临时文件名
     */
    public String mTempFileName;

    /**
     * 下载完成时间
     */
    public long mDownloadFinishtime;

    /**
     * 下载条件，是否支持WIFI，MOBILE
     */
    public int mTaskCondition;

    /**
     * 最大下载速度
     */
    public int mMaxSpeed;

    /**
     * 下载耗时
     */
    public long mTimeCosts;



    /**
     * 任务的优先级,理论上按照任务的加入来排序
     */
    public int mPriority;


    /**
     * 当前任务状态
     */
    public DownloadStatus mStatus = DownloadStatus.NEW;

    public volatile transient boolean isCancel = false;

    public transient Downloader mDownloader;

    public Downloader getDownloader() {
        return mDownloader;
    }

    public void setDownloader(Downloader mDownloader) {
        this.mDownloader = mDownloader;
    }


    public void setPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    public synchronized void setDefaultConfig(DownloaderConfig config, Context context) {
        if(TextUtils.isEmpty(mKey)) {
            mKey = generalKey();
        }

        if( TextUtils.isEmpty(mLocalPath)) {
            mLocalPath = config.mDefaultDownloadPath;
        }
        if( TextUtils.isEmpty(mFileName)) {
            mFileName = FileUtil.getFileNameFromUrl(mUrl);
        }

        if( TextUtils.isEmpty(mTempFileName)) {
            mTempFileName = mFileName + config.mDefaultTempSurfix;
        }
    }

    protected String generalKey() {
        return mUrl;
    }

    @Override
    public int compareTo(DownloadTask another) {
        int left = this.mPriority;
        int right = another.mPriority;

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ?
                this.mSequence - another.mSequence :
                right - left;
    }

    /**
     * 获取最终下载路径
     * @return
     */
    public String getFinalFilePath() {
        return mLocalPath + File.separator + mFileName;
    }

    /**
     * 获取临时下载路径
     * @return
     */
    public String getTempFilePath() {
        return mLocalPath + File.separator + mTempFileName;
    }

    /**
     * 检查下载路径,如果不存在，则创建
     * @return
     */
    public boolean checkDownloadPathAndMkDirs() {
        File downloadPath = new File(mLocalPath);
        if( !downloadPath.exists()) {
           return downloadPath.mkdirs();
        }
        return true;
    }

    /**
     * 检查下载文件，如果重下则把原来的删除
     */
    public void checkDownloadFileAndDelete() {
        File downloadFile = new File(getFinalFilePath());
        if( downloadFile.exists()) {
            downloadFile.delete();
        }
    }

    public void resetTask() {
        mFileTotalSize = 0;
        mFileDownloadedSize = 0;
        File file = new File(getFinalFilePath());
        file.delete();
        file = new File(getTempFilePath());
        file.delete();
        mMaxSpeed = 0;
        mTimeCosts = 0;
    }

    /**
     * 获取临时文件大小
     * @return
     */
    public long getTempFileSize() {
        File tempFile = new File( getTempFilePath());
        if( tempFile.exists()) {
            return tempFile.length();
        }
        else {
            return 0;
        }
    }

    /**
     * 目前只支持http
     * @return
     */
    public boolean checkUrl() {
        try {
            URI uri = URI.create(mUrl);
            if( "http".equalsIgnoreCase(uri.getScheme())) {
                return true;
            }
            else{
                DLog.e(TAG, "do not support %s", uri.getScheme());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查已下载的文件是否正常
     * @return
     */
    public boolean checkDownloadedFile() {
        return true;
    }

    public boolean checkContentType(String contentType) {
        if( TextUtils.isEmpty(contentType)) {
            return true;
        }
        else if( !noAcceptContentType(contentType)) {
            return true;
        }
        return false;
    }

    /**
     * 检查当前的contenttype是不是支持
     * @param contentType
     * @return
     */
    protected boolean noAcceptContentType(String contentType) {
        for( int i=0;i<ExceptionContentType.length;i++) {
            if( contentType.contains(ExceptionContentType[i])) {
                return true;
            }
        }
        return false;
    }

}
