package com.anpoz.cutitdown;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by anpoz on 2015/9/1.
 */
public class App extends Application {
    public static RequestQueue queues;

    @Override
    public void onCreate() {
        super.onCreate();
        queues = Volley.newRequestQueue(getApplicationContext());
        queues.start();
    }

    public static RequestQueue getHttpQueues() {
        return queues;
    }
}
