package com.tcl.mie.downloader;

/**
 * Created by Rex on 2015/6/3.
 */
public interface Downloader  {

    public void startDownload(DownloadTask item);

    public void pauseDownload(DownloadTask item);

    public void cancelDownload(DownloadTask item);

    public void setDownloadStrategy(IDownloadStrategy strategy);

    public void setStatisticsLogger(IStatisticsLogger logger);

    public void setDownloadListener(IDownloadListener listener);
}
