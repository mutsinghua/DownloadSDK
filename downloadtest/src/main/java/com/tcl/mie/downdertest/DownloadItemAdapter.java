package com.tcl.mie.downdertest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tcl.mie.downloader.DownloadTask;
import com.tcl.mie.downloader.IDownloader;

import java.io.File;
import java.util.List;

/**
 * Created by Rex on 2015/7/1.
 */
public class DownloadItemAdapter extends BaseAdapter {

    private Context mContext;

    private List<DownloadTask> mTasks;

    public DownloadItemAdapter(Context context, List<DownloadTask> tasks) {
        mContext = context.getApplicationContext();
        mTasks = tasks;
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Object getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTasks.get(position).mSequence;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if( view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.download_item, null);
        }
        TextView name = (TextView) view.findViewById(R.id.textView);
        Button button = (Button) view.findViewById(R.id.button);
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.progressBar);
        DownloadTask task = (DownloadTask) getItem(position);
        button.setTag(task);
        bar.setMax((int) task.mFileTotalSize);
        bar.setProgress((int) task.mFileDownloadedSize);
        if( task.mFileTotalSize == 0) {

        }
        else {
            name.setText(String.valueOf( (task.mFileDownloadedSize * 100 / task.mFileTotalSize)));
        }
        switch (task.mStatus) {
            case NEW:
                button.setText("Download");
                break;
            case WAITING:
                button.setText("Waiting");
                break;
            case DOWNLOADING:
                button.setText("Downloading");
                break;
            case DOWNLOADED:
                button.setText("Install");
                break;
            case STOP:
                button.setText("Continue");
                break;
            case ERROR:
                button.setText("Restore");
                break;
        }
        if( position % 2 == 0)
        button.setOnClickListener(clickListener);
        else
            button.setOnClickListener(autoclickListener);
        return view;
    }

    private View.OnClickListener autoclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DownloadTask task = (DownloadTask) v.getTag();
            IDownloader downloader = DownloadManager.getInstance(mContext).getDownloader();
            switch (task.mStatus) {
                case NEW:
                    downloader.startDownloadInLow(task);
                    break;
                case WAITING:
                    downloader.pauseDownload(task);
                    break;
                case DOWNLOADING:
                    downloader.pauseDownload(task);
                    break;
                case DOWNLOADED:
                    downloader.startDownload(task);
                    break;
                case STOP:
                    downloader.startDownload(task);
                    break;
                case ERROR:
                    downloader.startDownload(task);
                    break;
            }
        }
    };

    /**
     * 获取安装应用的Intent
     */
    public static Intent getInstallIntent(Context mContext, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DownloadTask task = (DownloadTask) v.getTag();
            IDownloader downloader = DownloadManager.getInstance(mContext).getDownloader();
            switch (task.mStatus) {
                case NEW:
                    downloader.startDownload(task);
                    break;
                case WAITING:
                    downloader.pauseDownload(task);
                    break;
                case DOWNLOADING:
                    downloader.pauseDownload(task);
                    break;
                case DOWNLOADED:
                    mContext.startActivity(getInstallIntent(mContext, new File(task.getFinalFilePath())));
                    break;
                case STOP:
                    downloader.startDownload(task);
                    break;
                case ERROR:
                    downloader.startDownload(task);
                    break;
            }
        }
    };
}
