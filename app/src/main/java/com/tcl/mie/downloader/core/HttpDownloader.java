package com.tcl.mie.downloader.core;

import com.tcl.mie.downloader.DownloadException;
import com.tcl.mie.downloader.DownloadTask;
import com.tcl.mie.downloader.IDownloadListener;

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by difei.zou on 2015/6/13.
 */
public class HttpDownloader implements INetworkDownloader{


    private HttpNetwork mNetwork = new HttpNetwork();

    //不要访问外面的成员变量
    @Override
    public void download(DownloadTask task) {
        String url = task.mUrl;





    }

    private void checkEnvironment(DownloadTask task) {
        File path = new File(task.mLocalPath);
        if( !path.exists()) {

        }
    }

    private long getTotalLenth(DownloadTask task) {
        if( task.mFileTotalSize == 0) {

        }
    }

}
