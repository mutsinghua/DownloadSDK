package com.tcl.mie.downdertest;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
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
            Log.d("DOWNLOADER", "STATUS CHANGE" + item);
        }

        @Override
        public void onDownloadProgress(DownloadTask item, long downloadSize, long totalSize, int speed, int maxSpeed, long timeCost) {
            adapter.notifyDataSetChanged();
            Log.d("DOWNLOADER", "progress " + downloadSize + "  " + speed);
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
        dt.mUrl = "http://113.105.73.150/dd.myapp.com/16891/6C168C910722759D622DEB85E34B4E0A.apk?mkey=5597599a12944e3f&f=8e5d&fsname=com.mybook66_2.9.5_36.apk&asr=8eff&p=.apk";

        dt.mName = "wechat";
        downloadTasks.add(dt);

        dt = new DownloadTask();
        dt.mName = "qq";
        dt.mUrl = "http://113.108.88.66/dd.myapp.com/16891/A79DF392E5978F4B40982EB3232A6252.apk?mkey=5597594112944e3f&f=d388&fsname=com.hunter.kuaikan_2.6.7_267001.apk&asr=8eff&p=.apk";
        downloadTasks.add(dt);

        dt = new DownloadTask();
        dt.mName = "qq";
        dt.mUrl = "http://113.108.88.67/dd.myapp.com/16891/DD99E868F1026CD04929862324B2A289.apk?mkey=5597592712944e3f&f=8d5d&fsname=com.bytetech1_3.6.0.myapp_56.apk&asr=8eff&p=.apk";
        downloadTasks.add(dt);

        dt = new DownloadTask();
        dt.mName = "qq";
        dt.mUrl = "http://113.105.73.150/dd.myapp.com/16891/77BA911D5D6190C16B67E2694B324960.apk?mkey=5597a6f312944e3f&f=d488&fsname=com.quanbennovel_1.1_2.apk&asr=8eff&p=.apk";
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
