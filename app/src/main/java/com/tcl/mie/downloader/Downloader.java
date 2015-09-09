package com.tcl.mie.downloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.tcl.mie.downloader.core.HttpDownloader;
import com.tcl.mie.downloader.core.INetworkDownloader;
import com.tcl.mie.downloader.core.TaskThread;
import com.tcl.mie.downloader.util.DLog;
import com.tcl.mie.downloader.util.PriorityUtils;

import org.aisen.orm.SqliteUtility;
import org.aisen.orm.SqliteUtilityBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 通用多优先级下载器
 * Created by difei.zou on 2015/6/4.
 */
public class Downloader  implements IDownloader{

    public static final String TAG = "DOWNLOADER";
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

    /**
     * 断点重试的任务
     */
    private final LinkedList<DownloadTask> mRetryTasks = new LinkedList<>();


    private ArrayList<DownloadTask> mResumeLowTasks = new ArrayList<>();


    private List<DownloadTask> mAllTasks = null;

    private DownloadEventCenter mEventCenter = new DownloadEventCenter();

    private TaskThread[] mThreads;

    private INetworkDownloader mHttpDownloader;

    public void init(DownloaderConfig config, Context context, ILoadListener loadListener) {
        if( config == null) {
            config = DownloaderConfig.getDefaultConfig(context);
        }
        new SqliteUtilityBuilder().build(context);

        mDownloaderConfig = config;
        mHttpDownloader = new HttpDownloader();
        mContext = context;
        try {
            mContext.registerReceiver(mNetworkMonitorReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mThreads = new TaskThread[mDownloaderConfig.mRunningTask];
        for( int i=0;i< mThreads.length;i++) {
            mThreads[i] = new TaskThread(mWaitingTasks,mHttpDownloader);
            mThreads[i].start();
        }

        loadData(loadListener);
    }


    private void loadData(ILoadListener loadListener) {
        List<DownloadTask> allTask =  SqliteUtility.getInstance().select(null, DownloadTask.class);
        if (loadListener != null) {
            allTask = loadListener.onLoad(allTask);
        }
        for(DownloadTask task : allTask) {
            if( getDownloaderConfig().mStrategy.canAutoStart(task) ) {
                startDownload(task);
            }
        }
        mAllTasks = allTask;
    }


    @Override
    public List<DownloadTask> getAllTask() {
        return mAllTasks;
    }

    public void init(Context context) {
        init(null, context,null);
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
        synchronized (mRetryTasks) {
            mRetryTasks.remove(item);
        }
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
                        synchronized (mResumeLowTasks) {
                            mResumeLowTasks.add(task);
                        }
                        break;
                    }
                }
            }
        }
    }

    private boolean isTaskExist(DownloadTask item) {
        return mCurrentTasks.contains(item);
    }

    public void resumeLowTasks() {
        if( mDownloadingTasks.size() == 0) {
            synchronized (mResumeLowTasks) {
                for (int i = 0; i < mResumeLowTasks.size(); i++) {
                    startDownloadInLow(mResumeLowTasks.get(i));
                }
                mResumeLowTasks.clear();
            }
        }
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
        item.setDefaultConfig(mDownloaderConfig,mContext);
        item.getDownloader().getEventCenter().onDownloadStatusChange(item);
        item.isCancel = false;
        SqliteUtility.getInstance().insertOrReplace(null, item);
        mWaitingTasks.offer(item);
    }

    /**
     * 自动下载,优先级低于手动下载
     * @param item
     */
    public void startDownloadInLow(DownloadTask item) {

        item.mSequence = PriorityUtils.getMaxSequence(mWaitingTasks)+1;
        item.setPriority(DownloadTask.PRORITY_LOW);
        synchronized (mRetryTasks) {
            mRetryTasks.remove(item);
        }
        addTask(item);
    }

    public void pauseDownload(DownloadTask item){
        item.isCancel = true;
        SqliteUtility.getInstance().update(null,item);
    }

    public void deleteDownload(DownloadTask item){
        item.isCancel = true;
        item.resetTask();
        SqliteUtility.getInstance().deleteById(null, DownloadTask.class, item.mKey);
    }

    @Override
    public void stopAllDownload() {
        synchronized (mCurrentTasks) {
            mCurrentTasks.clear();
        }
        synchronized (mResumeLowTasks) {
            mResumeLowTasks.clear();
        }

        mWaitingTasks.clear();
        ArrayList<DownloadTask> runningTask = new ArrayList<>(mDownloadingTasks);
        for( int i=0;i<runningTask.size();i++) {
            runningTask.get(i).isCancel = true;
        }
        SqliteUtility.getInstance().update(null, runningTask);
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

        SqliteUtility.getInstance().update(null, item);
        //需要时，恢复自动下载
        resumeLowTasks();
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
        synchronized (mRetryTasks) {
            mRetryTasks.add(task);
        }
    }

    public void quit() {
        for( int i=0;i< mThreads.length;i++) {
            mThreads[i].mCancel = true;
        }
        stopAllDownload();
        mContext.unregisterReceiver(mNetworkMonitorReceiver);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {

            final Downloader downloader = (Downloader) msg.obj;

            ConnectivityManager cm = (ConnectivityManager) downloader.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null) {
                    final List<DownloadTask> downloadTasks = new ArrayList<>(downloader.mRetryTasks);
                    synchronized (downloader.mRetryTasks) {
                        Collections.sort(downloader.mRetryTasks);
                        for (int i = 0; i < downloader.mRetryTasks.size(); i++) {
                            final DownloadTask task = downloader.mRetryTasks.get(i);
                            if (downloader.mDownloaderConfig.mStrategy.onNetworkChange(ConnectivityManager.TYPE_WIFI, task)) {
                                downloader.addTask(task);
                            }
                            else {
                                downloadTasks.remove(task);
                            }
                        }
                        downloader.mRetryTasks.removeAll(downloadTasks);
                    }
                }
            }
        }
    };

    private BroadcastReceiver mNetworkMonitorReceiver = new BroadcastReceiver() {
        private static final String TAG = "mNetworkMonitorReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if( mContext == null) {
                mContext = context.getApplicationContext();
            }
            if (!intent.getAction().equalsIgnoreCase(
                    ConnectivityManager.CONNECTIVITY_ACTION)) {
                return;
            }

            DLog.v("网络变化");
            // isBreak为false 有网
            boolean isBreak = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            DLog.d("网络变化" + isBreak);
            if (isBreak) {
                return;
            }
            handler.removeMessages(0);
            handler.sendMessageDelayed(Message.obtain(handler, 0, Downloader.this), 3000);
        }

    };
}
