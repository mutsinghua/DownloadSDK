package com.tcl.mie.downloader;

import java.util.ArrayList;

/**
 * Created by difei.zou on 2015/6/4.
 */
public class DefaultStrategy implements IDownloadStrategy {


    @Override
    public boolean onNetworkChange(int networkType, DownloadTask task) {
        if(networkType == WIFI) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onRetry(DownloadTask task) {
        if (!task.isCancel) {
            return true;
        }
        return false;
    }
}
