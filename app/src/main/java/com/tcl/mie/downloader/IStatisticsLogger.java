package com.tcl.mie.downloader;

/**
 * Created by Rex on 2015/6/3.
 */
public interface IStatisticsLogger {

    void onDownloadStartLogger(DownloadTask item);

    void onDownloadFinishLogger(DownloadTask item);

    void onPause, onCancel, onContinue, onError
}
