package com.tcl.mie.downloader;

import java.util.Comparator;

/**
 * 下载结构体
 * Created by Rex on 2015/6/3.
 */
public class DownloadTask implements Comparator<DownloadTask>{

    public byte SUPPORT_WIFI = 1;
    public byte SUPPORT_MOBILE =2;

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
     * 临时下载路径
     */
    public String mTempLocalPath;

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

    @Override
    public int compare(DownloadTask lhs, DownloadTask rhs) {
        return lhs.mPriority - rhs.mPriority;
    }

    public void setDownloader(Downloader mDownloader) {
        this.mDownloader = mDownloader;
    }
}
