package com.anpoz.cutitdown;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.anpoz.cutitdown.Utils.LogUtils;
import com.anpoz.cutitdown.Utils.OkHttpStack;

/**
 * Created by anpoz on 2015/9/1.
 */
public class App extends Application {
    public static RequestQueue queues;

    @Override
    public void onCreate() {
        super.onCreate();
        queues = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack());
        queues.start();

        if (!BuildConfig.DEBUG)
            LogUtils.enable(false);
        else
            LogUtils.enable(true);
    }

    public static RequestQueue getHttpQueues() {
        return queues;
    }
}
