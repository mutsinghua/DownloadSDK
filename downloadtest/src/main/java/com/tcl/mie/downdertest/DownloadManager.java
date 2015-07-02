package com.tcl.mie.downdertest;

import android.content.Context;

import com.tcl.mie.downloader.Downloader;
import com.tcl.mie.downloader.IDownloader;

/**
 * Created by Rex on 2015/7/2.
 */
public class DownloadManager {

    private static DownloadManager instance;
    private IDownloader downloader;


    private DownloadManager(Context context) {
        downloader = new Downloader();
        downloader.init(context);
    }

    public static DownloadManager getInstance(Context context) {
        if ( instance == null) {
            synchronized (DownloadManager.class) {
                if( instance == null) {
                    instance = new DownloadManager(context);
                }
            }
        }
        return instance;
    }

    public IDownloader getDownloader() {
        return downloader;
    }
}
