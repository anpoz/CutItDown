package com.anpoz.cutitdown.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.anpoz.cutitdown.App;
import com.anpoz.cutitdown.Beans.BaiduUrl;
import com.anpoz.cutitdown.Beans.IsgdUrl;
import com.anpoz.cutitdown.Beans.Result;
import com.anpoz.cutitdown.Beans.SinaUrl;
import com.anpoz.cutitdown.Beans.Url;
import com.anpoz.cutitdown.Beans._985soUrl;
import com.anpoz.cutitdown.R;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anpoz on 2015/8/20.
 */
public class UrlShortener {

    private static final String SINA_API_URL = "http://api.weibo.com/2/short_url/shorten.json?source=5786724301&url_long=";
    private static final String BAIDU_API_URL = "http://dwz.cn/create.php";
    private static final String _985so_API_URL = "http://985.so/api.php?format=json&url=";
    private static final String ISGE_API_URL = "http://is.gd/create.php?format=json&url=";

    private String mUrlShortenerType;
    private Context mContext;
    private Handler handler;
    private StringRequest stringRequest;

    public UrlShortener(String urlShortenerType, Context context, Handler handler) {
        this.mUrlShortenerType = urlShortenerType;
        this.mContext = context;
        this.handler = handler;
    }

    /**
     * 使用volley请求返回string，然后用gson解析返回的json字符串
     * 并通过handler返回数据
     *
     * 通过debug发现使用volley返回的string会在json字符串前加上一些乱码
     * 解决方法：s.substring(s.indexOf('{'))
     * @param long_url
     */
    public void makeUrlShortened(final String long_url) {
        String url = null;
        switch (mUrlShortenerType) {
            case "1"://sina
                try {
                    url = SINA_API_URL + URLEncoder.encode(long_url, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LogUtils.d("jsonString:" + s);
                        Gson gson = new Gson();
                        Result r = gson.fromJson(s.substring(s.indexOf('{')), Result.class);
                        SinaUrl sinaUrl = r.getUrls().get(0);
                        Message message = Message.obtain();
                        if (sinaUrl.isResult()) {
                            Url item = new Url();
                            item.setLongUrl(long_url);
                            item.setShortUrl(sinaUrl.getUrl_short());
                            message.arg1 = 0;
                            message.obj = item;
                        } else {
                            message.arg1 = 1;
                            message.obj = mContext.getResources().getString(R.string.msg_url_unavailable);
                        }
                        handler.sendMessage(message);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        sendErrorMessage(volleyError);
                    }
                });
                break;
            case "2"://baidu
                stringRequest = new StringRequest(Request.Method.POST, BAIDU_API_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LogUtils.d("jsonString:" + s);
                        Gson gson = new Gson();
                        BaiduUrl baiduUrl = gson.fromJson(s.substring(s.indexOf('{')), BaiduUrl.class);
                        Message message = Message.obtain();
                        if (baiduUrl.getStatus() == 0) {
                            Url item = new Url();
                            item.setLongUrl(long_url);
                            item.setShortUrl(baiduUrl.getTinyurl());
                            message.arg1 = 0;
                            message.obj = item;
                        } else {
                            message.arg1 = 1;
                            message.obj = baiduUrl.getErr_msg();
                        }
                        handler.sendMessage(message);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        sendErrorMessage(volleyError);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("url", long_url);
                        return map;
                    }
                };
                break;
            case "3"://985so
                try {
                    url = _985so_API_URL + URLEncoder.encode(long_url, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LogUtils.d("jsonString:" + s);
                        Gson gson = new Gson();
                        _985soUrl soUrl = gson.fromJson(s.substring(s.indexOf('{')), _985soUrl.class);
                        Message message = Message.obtain();
                        if (soUrl.getError() == 0) {
                            Url item = new Url();
                            item.setLongUrl(long_url);
                            item.setShortUrl(soUrl.getUrl());
                            message.arg1 = 0;
                            message.obj = item;
                        } else if (soUrl.getError() == -1) {
                            message.arg1 = 1;
                            message.obj = mContext.getResources().getString(R.string.msg_url_unsafe_illegal);
                        } else if (soUrl.getError() == 0) {
                            message.arg1 = 1;
                            message.obj = mContext.getResources().getString(R.string.msg_url_other_reason);
                        }
                        handler.sendMessage(message);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        sendErrorMessage(volleyError);
                    }
                });
                break;
            case "4"://isgd
                try {
                    url = ISGE_API_URL + URLEncoder.encode(long_url, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        LogUtils.d("jsonString:" + s);
                        Gson gson = new Gson();
                        IsgdUrl isgdUrl = gson.fromJson(s.substring(s.indexOf('{')), IsgdUrl.class);
                        Message message = Message.obtain();
                        if (isgdUrl.getErrorcode() == 1) {
                            message.arg1 = 1;
                            message.obj = mContext.getResources().getString(R.string.msg_url_unsafe_illegal);
                        } else if (isgdUrl.getErrorcode() == 2 || isgdUrl.getErrorcode() == 3 || isgdUrl.getErrorcode() == 4) {
                            message.arg1 = 1;
                            message.obj = mContext.getResources().getString(R.string.msg_url_server_busy);
                        } else {
                            Url item = new Url();
                            item.setLongUrl(long_url);
                            item.setShortUrl(isgdUrl.getShorturl());
                            message.arg1 = 0;
                            message.obj = item;
                        }
                        handler.sendMessage(message);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        sendErrorMessage(volleyError);
                    }
                });
                break;
        }
        stringRequest.setTag("URL");
        App.getHttpQueues().add(stringRequest);
    }

    private void sendErrorMessage(VolleyError volleyError) {
        LogUtils.e(volleyError.toString());
        Message message = Message.obtain();
        message.arg1 = 1;
        message.obj = mContext.getResources().getString(R.string.msg_network_error);
        handler.sendMessage(message);
    }

}
