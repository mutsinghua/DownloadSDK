package com.tcl.mie.downloader;

import android.content.Context;

import com.tcl.mie.downloader.core.HttpDownloader;
import com.tcl.mie.downloader.core.INetworkDownloader;
import com.tcl.mie.downloader.core.TaskThread;
import com.tcl.mie.downloader.util.PriorityUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 通用多优先级下载器
 * Created by difei.zou on 2015/6/4.
 */
public class Downloader  implements IDownloader{

    public DownloaderConfig getDownloaderConfig() {
        return mDownloaderConfig;
    }

    private DownloaderConfig mDownloaderConfig;
    private Context mContext;

    /**等待任务队列*/
    private final BlockingQueue<DownloadTask> mWaitingTasks = new PriorityBlockingQueue<DownloadTask>();

    /**
     * 下载中的任务
     */
    private final LinkedList<DownloadTask> mDownloadingTasks = new LinkedList<>();

    /**所有任务*/
    private final Set<DownloadTask> mCurrentTasks = new HashSet<DownloadTask>();

    private final LinkedList<DownloadTask> mRetryTasks = new LinkedList<>();

    private DownloadEventCenter mEventCenter = new DownloadEventCenter();

    private TaskThread[] mThreads;

    private INetworkDownloader mHttpDownloader;
    public void init(DownloaderConfig config, Context context) {
        if( config == null) {
            config = DownloaderConfig.getDefaultConfig(context);
        }
        mDownloaderConfig = config;
        mHttpDownloader = new HttpDownloader();
        mContext = context;
        mThreads = new TaskThread[mDownloaderConfig.mRunningTask];
        for( int i=0;i< mThreads.length;i++) {
            mThreads[i] = new TaskThread(mWaitingTasks,mHttpDownloader );
            mThreads[i].start();
        }

    }

    public void init(Context context) {
        init(null, context);
    }

    /**
     * 手动下载
     * @param item
     */
    public void startDownload(DownloadTask item) {
        //过滤已添加的下载任务
        if( isTaskExist(item)) return;
        item.mSequence = PriorityUtils.getMaxSequence(mWaitingTasks)+1;
        item.setPriority(DownloadTask.PRORITY_NORMAL);
        addTask(item);
        //在开始之前，把低优先级下载的暂停掉
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
                    if( task.mPriority > DownloadTask.PRORITY_NORMAL) {
                        //找到自动下载的，停掉。
                        pauseDownload(task);
                        //这句话加在这儿可能会有问题
                        onTaskStop(task);
                        //重新加入队列
                        startDownloadInLow(task);
                        break;
                    }
                }
            }
        }
    }

    private boolean isTaskExist(DownloadTask item) {
        return mCurrentTasks.contains(item);
    }

    /**
     * 保持原来的下载方式下载
     * @param item
     */
    private void addTask(DownloadTask item) {
        item.setDownloader(this);
        if( isTaskExist(item)) return;
        synchronized (mCurrentTasks) {
            mCurrentTasks.add(item);
        }
        item.mStatus = DownloadStatus.WAITING;
        item.getDownloader().getEventCenter().onDownloadStatusChange(item);
        item.isCancel = false;
        mWaitingTasks.offer(item);
    }

    /**
     * 自动下载,优先级低于手动下载
     * @param item
     */
    public void startDownloadInLow(DownloadTask item) {

        item.mSequence = PriorityUtils.getMaxSequence(mWaitingTasks)+1;
        item.setPriority(DownloadTask.PRORITY_LOW);
        addTask(item);

    }

    public void pauseDownload(DownloadTask item){
        item.isCancel = true;
    }

    public void deleteDownload(DownloadTask item){
        item.isCancel = true;
        item.resetTask();
    }

    @Override
    public void stopAllDownload() {
        synchronized (mCurrentTasks) {
            mCurrentTasks.clear();
        }
        mWaitingTasks.clear();
        ArrayList<DownloadTask> runningTask = new ArrayList<>(mDownloadingTasks);
        for( int i=0;i<runningTask.size();i++) {
            runningTask.get(i).isCancel = true;
        }
    }

    public void onTaskGoing(DownloadTask task) {
        mDownloadingTasks.add(task);
    }
    public void onTaskStop(DownloadTask item) {
        mDownloadingTasks.remove(item);
        synchronized (mCurrentTasks) {
            mCurrentTasks.remove(item);
        }
        mWaitingTasks.remove(item);
    }

    public void addDownloadListener(IDownloadListener downloadListener) {
        mEventCenter.addDownloadListener(downloadListener);
    }

    public void removeDownloadListener(IDownloadListener downloadListener) {
        mEventCenter.removeDownloadListener(downloadListener);
    }



    public DownloadEventCenter getEventCenter() {
        return mEventCenter;
    }


    public void retry(DownloadTask task) {
        mRetryTasks.add(task);
    }

    public void quit() {
        for( int i=0;i< mThreads.length;i++) {
            mThreads[i].mCancel = true;
        }
        stopAllDownload();
    }
}
