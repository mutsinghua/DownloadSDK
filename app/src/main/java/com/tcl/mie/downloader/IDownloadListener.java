package com.tcl.mie.downloader;

/**
 * Created by Rex on 2015/6/3.
 */
public interface IDownloadListener {

    public void onDownloadStatusChange(DownloadTask item);

    public void onDownloadProgress(DownloadTask item, long downloadSize, long totalSize, int speed, int maxSpeed, long timeCost);

    public boolean acceptItem(DownloadTask item);
}
