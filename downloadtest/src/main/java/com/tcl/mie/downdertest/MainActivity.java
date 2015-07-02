package com.tcl.mie.downdertest;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;

import com.tcl.mie.downloader.DownloadTask;
import com.tcl.mie.downloader.IDownloadListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity {

    private IDownloadListener listener = new IDownloadListener() {
        @Override
        public void onDownloadStatusChange(DownloadTask item) {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onDownloadProgress(DownloadTask item, long downloadSize, long totalSize, int speed, int maxSpeed, long timeCost) {
            adapter.notifyDataSetChanged();
        }

        @Override
        public boolean acceptItem(DownloadTask item) {
            return true;
        }
    };

    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new DownloadItemAdapter(this, getTestData());
        setListAdapter(adapter);

        DownloadManager.getInstance(this).getDownloader().addDownloadListener(listener);

    }

    @Override
    protected void onDestroy() {
        DownloadManager.getInstance(this).getDownloader().removeDownloadListener(listener);
        super.onDestroy();
    }

    public List<DownloadTask> getTestData() {
        ArrayList<DownloadTask> downloadTasks = new ArrayList<>();
        DownloadTask dt = new DownloadTask();
        dt.mUrl = "http://113.107.238.15/dd.myapp.com/16891/75FE2C34A71E326C07C13BEA362BB28E.apk?mkey=5594f37b12944e3f&f=a20e&fsname=com.tencent.mm_6.2.2.51rc9c7176_580.apk&asr=8eff&p=.apk";

        dt.mName = "wechat";
        downloadTasks.add(dt);

        dt = new DownloadTask();
        dt.mName = "qq";
        dt.mUrl = "http://183.61.62.149/dd.myapp.com/16891/7FEF6275712B7E703AF1F5604D2ACCC1.apk?mkey=5594f0ae12944e3f&f=2b01&fsname=com.tencent.mobileqq_5.7.2_260.apk&asr=8eff&p=.apk";
        downloadTasks.add(dt);

        return downloadTasks;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
