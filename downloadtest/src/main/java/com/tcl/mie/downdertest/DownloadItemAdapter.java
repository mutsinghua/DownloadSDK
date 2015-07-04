package com.tcl.mie.downdertest;

import android.content.Context;
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
        name.setText((int) (task.mFileDownloadedSize * 100 / task.mFileTotalSize));
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
        button.setOnClickListener(clickListener);
        return view;
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
                    Toast.makeText(mContext, "to be installed", Toast.LENGTH_LONG).show();
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
