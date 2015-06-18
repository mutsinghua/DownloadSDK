package com.tcl.mie.downloader;

/**
 * Created by difei.zou on 2015/6/4.
 */
public class DefaultStrategy implements IDownloadStrategy {




    @Override
    public boolean onNetworkChange(int networkType, DownloadTask task) {
        return true;
    }

    @Override
    public boolean onRetry(DownloadTask task) {
        return false;
    }
}
