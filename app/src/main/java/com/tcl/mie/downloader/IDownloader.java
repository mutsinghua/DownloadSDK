package com.tcl.mie.downloader;

import android.content.Context;

import java.util.List;

/**
 * Created by Rex on 2015/6/30.
 */
public interface IDownloader {
    void init(Context context);
    void init(DownloaderConfig config, Context context);
    void startDownload(DownloadTask item);
    void startDownloadInLow(DownloadTask item);
    void pauseDownload(DownloadTask item);
    void deleteDownload(DownloadTask item);
    void stopAllDownload();
    void addDownloadListener(IDownloadListener downloadListener);
    void removeDownloadListener(IDownloadListener downloadListener);
    void quit();
    List<DownloadTask> getAllTask();

}
