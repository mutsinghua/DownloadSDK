package com.tcl.mie.downloader;

import java.util.Collection;

/**
 * Created by difei.zou on 2015/6/9.
 */
public class PriorityUtils {

    public static int getMinPriority(Collection<DownloadTask> data) {
        int min = -1;
        synchronized (data) {
            for(DownloadTask task:data) {
                if( task.mPriority < min) {
                    min = task.mPriority;
                }
            }
        }
        return min;
    }

    public static int getMaxPriority(Collection<DownloadTask> data) {
        int max = -1;
        synchronized (data) {
            for(DownloadTask task:data) {
                if( task.mPriority > max) {
                    max = task.mPriority;
                }
            }
        }
        return max;
    }
}
