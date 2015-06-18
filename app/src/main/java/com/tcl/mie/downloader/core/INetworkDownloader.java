package com.tcl.mie.downloader.core;

import com.tcl.mie.downloader.DownloadException;
import com.tcl.mie.downloader.DownloadTask;
import com.tcl.mie.downloader.IDownloadListener;

/**
 * Created by difei.zou on 2015/6/13.
 */
public interface INetworkDownloader {
    void download(DownloadTask task) throws DownloadException;
}
