package com.tcl.mie.downloader;

import android.content.Context;
import android.text.TextUtils;

import com.tcl.mie.downloader.util.FileUtil;
import com.tcl.mie.downloader.util.Tools;

import java.io.File;
import java.util.Comparator;

/**
 * 下载结构体
 * Created by Rex on 2015/6/3.
 */
public class DownloadTask implements Comparable<DownloadTask>{


    public byte SUPPORT_WIFI = 1;
    public byte SUPPORT_MOBILE =2;
    //自动下载
    public static final int PRORITY_MANUAL = 0;

    //手动下载
    public static final int PRORITY_AUTO  = 1;
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

    public void setPriority(int mPriority) {
        this.mPriority = mPriority;
    }

    /**
     * 任务的优先级,理论上按照任务的加入来排序
     */
    public int mPriority;




    public Downloader mDownloader;


    public void setDownloader(Downloader mDownloader) {
        this.mDownloader = mDownloader;
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
            mTempFileName = mFileName + config.getTempSuffix();
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
                right - left.;
    }
}
