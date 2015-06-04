package com.tcl.mie.downloader;

/**
 * 下载结构体
 * Created by Rex on 2015/6/3.
 */
public class DownloadTask {

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


}
