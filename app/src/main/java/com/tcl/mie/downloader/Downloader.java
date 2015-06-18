package com.tcl.mie.downloader;

import android.content.Context;

import java.util.HashSet;
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

    /**自动任务队列，低优先级*/
    private final BlockingQueue<DownloadTask> mAutoTasks = new PriorityBlockingQueue<DownloadTask>();

    private final Set<DownloadTask> mDownloadingTasks = new HashSet<>(2);

    /**所有任务*/
    private final Set<DownloadTask> mCurrentTasks = new HashSet<DownloadTask>();

    private ThreadPoolExecutor executorService;

    public void init(DownloaderConfig config, Context context) {
        if( config == null) {
            config = DownloaderConfig.getDefaultConfig(context);
        }
        mDownloaderConfig = config;
        mContext = context;
        executorService = new ThreadPoolExecutor(3, 3,
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
    public void startDownload(DownloadTask item) {
        item.setDownloader(this);
        synchronized (mCurrentTasks) {
            mCurrentTasks.add(item);
        }
        item.setPriority(PriorityUtils.getMaxPriority(mWaitingTasks)+1);
        mWaitingTasks.add(item);
    }

    private void initBlankField(DownloadTask item) {

    }

    /**
     * 自动下载,优先级低于手动下载
     * @param item
     */
    public void autoStartDownload(DownloadTask item) {
        item.setDownloader(this);
        synchronized (mCurrentTasks) {
            mCurrentTasks.add(item);
        }
        item.setPriority(PriorityUtils.getMaxPriority(mWaitingTasks)+1);
        mWaitingTasks.add(item);

    }

    public void pauseDownload(DownloadTask item){

    }

    public void cancelDownload(DownloadTask item){

    }


}
