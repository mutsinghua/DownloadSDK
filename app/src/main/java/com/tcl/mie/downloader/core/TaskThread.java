package com.tcl.mie.downloader.core;

import com.tcl.mie.downloader.DownloadException;
import com.tcl.mie.downloader.DownloadStatus;
import com.tcl.mie.downloader.DownloadTask;

import java.util.concurrent.BlockingQueue;

/**
 * 下载线程
 * Created by difei.zou on 2015/6/13.
 */
public class TaskThread extends Thread {

    private BlockingQueue<DownloadTask> mWaitTasks;
    private INetworkDownloader mNetworkDownloader;
    public volatile boolean mCancel = false;
    public TaskThread(BlockingQueue<DownloadTask> waitTasks, INetworkDownloader networkDownloader) {
        super("TaskThread" + System.nanoTime());
        this.mWaitTasks = waitTasks;
        this.mNetworkDownloader = networkDownloader;
    }

    @Override
    public void run() {
        while(!mCancel) {
            try {
                DownloadTask task = mWaitTasks.take();
                if( mCancel) {
                    break;
                }
                if (task != null) {
                    try {
                        task.getDownloader().onTaskGoing(task);
                        mNetworkDownloader.download(task);
                    } catch (DownloadException e) {
                        e.printStackTrace();
                        if (e.mErrorCode == e.ECODE_PAUSE) {
                            if (task.mPriority == DownloadTask.PRORITY_LOW) {
                                //自动下载的。重新加入
                                task.getDownloader().startDownloadInLow(task);
                            } else {
                                task.mStatus = DownloadStatus.STOP;
                                task.getDownloader().getEventCenter().onDownloadStatusChange(task);
                            }
                        } else if (e.mErrorCode == e.ECODE_NETWORK) {
                            //如果是网络下载失败的,缓存到任务队列中，等有网络的时候再继续下载
                            task.getDownloader().retry(task);
                        } else {
                            task.mStatus = DownloadStatus.ERROR;
                            task.getDownloader().getEventCenter().onDownloadStatusChange(task);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
                        task.getDownloader().onTaskStop(task);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


}
