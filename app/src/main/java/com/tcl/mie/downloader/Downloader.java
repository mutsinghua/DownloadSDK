package com.tcl.mie.downloader;

import android.content.Context;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 通用多优先级下载器
 * Created by difei.zou on 2015/6/4.
 */
public class Downloader  {

    private DownloaderConfig mDownloaderConfig;
    private Context mContext;

    /**手动任务队列,高优先级*/
    private final BlockingQueue<DownloadTask> mWaitingTasks = new PriorityBlockingQueue<DownloadTask>();


    private final LinkedList<DownloadTask> mDownloadingTasks = new LinkedList<>();

    /**所有任务*/
    private final Set<DownloadTask> mCurrentTasks = new HashSet<DownloadTask>();

    private ThreadPoolExecutor executorService;

    public void init(DownloaderConfig config, Context context) {
        if( config == null) {
            config = DownloaderConfig.getDefaultConfig(context);
        }
        mDownloaderConfig = config;
        mContext = context;
        executorService = new ThreadPoolExecutor(mDownloaderConfig.mRunningTask, mDownloaderConfig.mRunningTask,
                10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());

    }

    public void init(Context context) {
        init(null, context);
    }

    /**
     * 手动下载
     * @param item
     */
    public void manualStartDownload(DownloadTask item) {

        item.setPriority(DownloadTask.PRORITY_MANUAL);
        startDownload(item);
        //在开始之前，把自动下载的暂停掉
        pauseAutoTask();
    }

    private void pauseAutoTask() {
        if( mDownloadingTasks.size() < mDownloaderConfig.mRunningTask) {
            //有空余线程
           return;
        }
        else {
            synchronized (mDownloadingTasks) {
                for (int i = 0; i < mDownloadingTasks.size(); i++) {
                    DownloadTask task = mDownloadingTasks.get(i);
                    if( task.mPriority > DownloadTask.PRORITY_MANUAL) {
                        //找到自动下载的，停掉。
                        pauseDownload(task);
                        //这句话加在这儿可能会有问题
                        mDownloadingTasks.remove(task);
                        //重新加入队列
                        autoStartDownload(task);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 保持原来的下载方式下载
     * @param item
     */
    public void startDownload(DownloadTask item) {
        item.setDownloader(this);
        synchronized (mCurrentTasks) {
            mCurrentTasks.add(item);
        }
        mWaitingTasks.add(item);
    }

    /**
     * 自动下载,优先级低于手动下载
     * @param item
     */
    public void autoStartDownload(DownloadTask item) {
        item.setPriority(DownloadTask.PRORITY_AUTO);
        startDownload(item);

    }

    public void pauseDownload(DownloadTask item){

    }

    public void cancelDownload(DownloadTask item){

    }


}
