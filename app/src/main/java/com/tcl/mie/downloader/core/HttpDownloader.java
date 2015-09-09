package com.tcl.mie.downloader.core;

import android.os.SystemClock;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Response;
import com.tcl.mie.downloader.DownloadException;
import com.tcl.mie.downloader.DownloadStatus;
import com.tcl.mie.downloader.DownloadTask;
import com.tcl.mie.downloader.util.DLog;
import com.tcl.mie.downloader.util.Tools;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by difei.zou on 2015/6/13.
 */
public class HttpDownloader implements INetworkDownloader{


    private static final String TAG = "HttpDownloader";

    public HttpDownloader(){


    }


    //不要访问外面的成员变量
    @Override
    public void download(DownloadTask task) throws DownloadException {
        task.mStatus = DownloadStatus.DOWNLOADING;
        task.getDownloader().getEventCenter().onDownloadStatusChange(task);
        if( !task.checkUrl()) {
            throw new DownloadException(DownloadException.ECODE_URL_CHECK_FAILED);
        }

        //先检查目录存在不
        if(!task.checkDownloadPathAndMkDirs()) {
            throw new DownloadException(DownloadException.ECODE_PATH_NOT_EXIST);
        }

        //检查文件存在否
        task.checkDownloadFileAndDelete();

        long tempLocalLenth = task.getTempFileSize();

        if( task.mFileTotalSize <= 0) {
            //获取服务器文件大小
            long serverLenth = getNetworkFileLenth(task);
            task.mFileTotalSize = serverLenth;
        }

        if( task.mFileTotalSize < tempLocalLenth) {
            //下多了？
            task.resetTask();
            throw new DownloadException(DownloadException.ECODE_LARGER_THAN_TARGET);
        }
        else if( task.mFileTotalSize == tempLocalLenth) {
            //两者一样大，算是下载完成
            doDownloadFinish(task);
        }

        //断点续传
        downloadContent(task, tempLocalLenth);

    }

    private void downloadContent(DownloadTask task, long localSize) throws DownloadException{
        Response response = null;
        Call call = null;
        long downloadSize = localSize;
        try {
            Map<String, String> headerParam = new HashMap<>(2);
            headerParam.put("RANGE", "bytes=" + localSize + "-");
            headerParam.put("User-Agent", task.getDownloader().getDownloaderConfig().mUA);
            call = HttpNetwork.connect(task.mUrl, headerParam, "GET");
            response = call.execute();
            int resCode = response.code();
            DLog.d("http get code = %s" ,response.code());
            if( resCode == HttpURLConnection.HTTP_OK || resCode == HttpURLConnection.HTTP_PARTIAL) {


                String contentType = response.body().contentType().type();
                if( !task.checkContentType(contentType)) {
                    throw new DownloadException(DownloadException.ECODE_CONTENT_TYPE_NOT_ACCEPTABLE);
                }
                byte[] buf = new byte[1024 * 4];
                int nRead;
                boolean downloadFinish = false;
                InputStream input = response.body().byteStream();
                OutputStream output = new FileOutputStream(task.getTempFilePath(), true);
                while( !task.isCancel && ( (nRead = input.read(buf)) >= 0)) {
                    output.write(buf, 0, nRead);
                    downloadSize += nRead;
//                    Log.d("DOWNLOADER", "nRead " + nRead );
                    task.mFileDownloadedSize = downloadSize;
                    downloadFinish = (downloadSize == task.mFileTotalSize);
                    doDownloading(task, downloadSize,downloadFinish);
                    if( downloadFinish) {
                        break;
                    }
                }
                if( task.isCancel) {
                    throw new DownloadException(DownloadException.ECODE_PAUSE);
                }
                Log.d("DOWNLOADER", "downloadFinish " + downloadFinish );
                if( downloadFinish) {
                    task.mDownloadFinishtime = System.currentTimeMillis();
                    doDownloadFinish(task);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new DownloadException(e, DownloadException.ECODE_NETWORK);
        }
        finally {
            DLog.d("close response");
            call.cancel();
        }
    }

    private long lastTime = 0;
    private long lastDownloadSize = 0;
    private void doDownloading(DownloadTask task, long downloadSize, boolean force) {
        task.mFileDownloadedSize = downloadSize;
        if( lastTime != 0 ) {
            long timeInterval = SystemClock.elapsedRealtime() - lastTime;
            if( timeInterval > 1000 || force) {
                long space = downloadSize - lastDownloadSize;
                int currentSpeed = (int) ((space) / (timeInterval / 1000.0)); //每秒字节数
                Log.d("DOWNLOADER", "notify " + downloadSize );
                task.getDownloader().getEventCenter().onDownloadProgress(task, downloadSize, task.mFileTotalSize, currentSpeed, 0, 0);
                lastTime = SystemClock.elapsedRealtime();
                lastDownloadSize = downloadSize;
            }

        }
        else {
            lastTime = SystemClock.elapsedRealtime();
            lastDownloadSize = downloadSize;
        }

    }

    private void doDownloadFinish(DownloadTask task) throws DownloadException{
        File file = new File(task.getTempFilePath());
        boolean  ret = file.renameTo(new File(task.getFinalFilePath()));
        if( ret ) {
            //重命名成功
            task.mStatus = DownloadStatus.DOWNLOADED;
            task.getDownloader().getEventCenter().onDownloadStatusChange(task);
        }
        else {
            throw new DownloadException(DownloadException.ECODE_RENAME_FAILED);
        }
    }

    /**
     * 获取服务器上文件信息
     * @param task
     * @return
     * @throws DownloadException
     */
    private long getNetworkFileLenth(DownloadTask task) throws DownloadException{
            Call call = null;
            Response response = null;
            try {
                Map<String, String> headerParam = new HashMap<>(2);
                headerParam.put("User-Agent", task.getDownloader().getDownloaderConfig().mUA);
                call = HttpNetwork.connect(task.mUrl,headerParam, "HEAD");
                response = call.execute();
            DLog.d("http head code = %s" ,response.code());
            if( response.code() != HttpURLConnection.HTTP_OK) {
                throw new DownloadException(DownloadException.ECODE_SERVER);
            }


            String contentType = response.body().contentType().type();
            if( !task.checkContentType(contentType)) {
                throw new DownloadException(DownloadException.ECODE_CONTENT_TYPE_NOT_ACCEPTABLE);
            }

            long length = response.body().contentLength();
            DLog.d("content length = %d", length);

            return length;

        } catch (IOException e) {
            e.printStackTrace();
            throw new DownloadException(e, DownloadException.ECODE_NETWORK);
        }
        finally {
            call.cancel();
        }
    }


}
