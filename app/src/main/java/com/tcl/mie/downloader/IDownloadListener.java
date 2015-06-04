package com.tcl.mie.downloader;

/**
 * Created by Rex on 2015/6/3.
 */
public interface IDownloadListener {

    public void onDownloadWaiting(DownloadTask item);

    public void onDownloadGoing(DownloadTask item);

    public void onDownloadPause(DownloadTask item);

    public void onDownloadFinish(DownloadTask item);

    public void onDownloadError(DownloadTask item, DownloadException exception);

    public void onDownloadProgress(DownloadTask item, long downloadSize, long totalSize, int speed, int maxSpeed, long timeCost);

}
