package com.tcl.mie.downloader.apk;

import com.tcl.mie.downloader.DownloadTask;

import java.util.HashMap;

/**
 * Created by Rex on 2015/6/3.
 */
public class DownloadApkTask extends DownloadTask {

    public long mAppId;
    public int mVersionCode;
    public int mPackageName;
    public String mAppName;
    public int mStartCount;

    /**
     * 附加信息
     */
    public HashMap<String, String> mExtra;


    /**
     * 下载类型，自动下载或手动下载
     */
    public int mDownloadTag;

}
