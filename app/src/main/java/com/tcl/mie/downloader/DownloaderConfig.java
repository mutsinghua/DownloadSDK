package com.tcl.mie.downloader;


import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.tcl.mie.downloader.util.Tools;

/**
 * 下载配置
 */
public class DownloaderConfig  {
    public static final int MAX_RUNING_TASK_NUMBER = 2;
    public static final String UA = "MIE_DOWNLOADER_V" + Constant.VERSION;
    public static final String DEFAULT_TEMP_EXT_NAME = ".mie";
    public String mDefaultDownloadPath;
    public int mRunningTask;
    public IDownloadStrategy mStrategy;
    public IStatisticsLogger mLogger;
    public String mUA;
    public String mProxy;
    public String mDefaultTempSurfix;
    public DownloaderConfig(Builder builder) {
        mDefaultDownloadPath = builder.mDefaultDownloadPath;
        mRunningTask = builder.mRunningTask;
        mStrategy = builder.mStrategy;
        mLogger = builder.mLogger;
        mUA = builder.mUA;
        mProxy = builder.mProxy;
        mDefaultTempSurfix = builder.mDefaultTempSurfix;
    }

    public static DownloaderConfig getDefaultConfig(Context context) {
        Builder builder = new Builder(context);
        return builder.build();
    }



    public static class Builder {

        private Context mContext;

        private String mDefaultDownloadPath;
        private int mRunningTask;
        private IDownloadStrategy mStrategy;
        private IStatisticsLogger mLogger;
        private String mUA;
        private String mProxy;
        public String mDefaultTempSurfix;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setDownloadPath(String path) {
            mDefaultDownloadPath = path;
            return this;
        }

        public Builder setRunningTaskNumber(int max) {
            mRunningTask = max;
            return this;
        }

        public Builder setDownloadStrategy( IDownloadStrategy strategy) {
            mStrategy = strategy;
            return this;
        }

        public Builder setLogger ( IStatisticsLogger logger) {
            mLogger = logger;
            return this;
        }

        public Builder setUA(String ua) {
            mUA = ua;
            return this;
        }

        public Builder setProxy(String proxy) {
            mProxy = proxy;
            return this;
        }

        public Builder setTempSuffix(String suffix) {
            mDefaultTempSurfix = suffix;
            return this;
        }

        public DownloaderConfig build() {
            initEmptyField();
            return new DownloaderConfig(this);
        }

        private void initEmptyField() {
            if(TextUtils.isEmpty(mDefaultDownloadPath)) {
                mDefaultDownloadPath = Tools.getCommonDownloadPath(mContext);
            }

            if( mRunningTask == 0) {
                mRunningTask = MAX_RUNING_TASK_NUMBER;
            }

            if( mStrategy == null) {
                mStrategy = new DefaultStrategy();
            }

            if ( mUA == null) {
                mUA = UA + mContext.getPackageName();
            }

            if ( mDefaultTempSurfix == null) {
                mDefaultTempSurfix = DEFAULT_TEMP_EXT_NAME;
            }
        }
    }
}