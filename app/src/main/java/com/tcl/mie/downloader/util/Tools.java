package com.tcl.mie.downloader.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import org.apache.http.HttpResponse;

import java.io.File;
import java.io.IOException;

/**
 * Created by Rex on 2015/6/18.
 */
public class Tools {

    public static boolean checkPermission(String permission, Context context)
    {

        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @TargetApi(11)
    public static boolean canWriteExtraStorage(Context context) {
        boolean hasPermission = false;
        if( Build.VERSION.SDK_INT < 19 && Build.VERSION.SDK_INT > 3) {
            hasPermission = checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", context);
        }
        else if( Build.VERSION.SDK_INT >= 19 ) {
            //19以上可以直接调用
            hasPermission = true;
        }

        boolean sdcard = context.getExternalFilesDir(null) != null;
        return hasPermission && sdcard;
    }

    public static String getCommonDownloadPath(Context context) {
        if( canWriteExtraStorage(context)) {
            File file = context.getExternalFilesDir(null);
            if( file != null) {
                return file.getAbsolutePath();
            }
            else{
                return context.getFilesDir().getAbsolutePath();
            }
        }
        else {
            return context.getFilesDir().getAbsolutePath();
        }
    }

    public static void safeClose(HttpResponse response) {
        if( response != null) {
            try {
                response.getEntity().getContent().close();
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }
}
