package com.tcl.mie.downloader;

/**
 * Created by Rex on 2015/6/3.
 */
public interface IDownloadStrategy {

    int WIFI = 1;

    int MOBILE = 2;

    /**
     * 当网络改变时，是否继续下载
     * @param networkType 网络类型, WIFI, MOBILE
     * @return true 可以继续下载，false 不用下载
     */
    boolean onNetworkChange(int networkType);

    boolean onRetry(int lastRetry);
}
