package com.tcl.mie.downloader.core;

import com.tcl.mie.downloader.DownloadTask;

import java.util.concurrent.BlockingQueue;

/**
 * 下载线程
 * Created by difei.zou on 2015/6/13.
 */
public class TaskThread implements Runnable {

    private BlockingQueue<DownloadTask> mWaitTasks;
    private INetworkDownloader mNetworkDownloader;

    public TaskThread(BlockingQueue<DownloadTask> waitTasks, INetworkDownloader networkDownloader) {
        this.mWaitTasks = waitTasks;
        this.mNetworkDownloader = networkDownloader;
    }

    @Override
    public void run() {
        try
        {
            DownloadTask task = mWaitTasks.take();
            if( task != null) {
                mNetworkDownloader.download(task);
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }


}
