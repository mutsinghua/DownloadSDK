package com.tcl.mie.downloader.apk;

import com.tcl.mie.downloader.DownloadTask;

/**
 * Created by Rex on 2015/6/3.
 */
public interface IPreloadListener {

    /**
     * 初始化任务
     * @param task
     * @return 对于task进行验证, true 正常， false 需要删除任务
     */
    boolean checkTask(DownloadTask task);

    /**
     * 是否自动开启任务
     * @param task
     * @return true 自动开始下载, false
     */
    boolean onAutoStartTask(DownloadTask task);
}
