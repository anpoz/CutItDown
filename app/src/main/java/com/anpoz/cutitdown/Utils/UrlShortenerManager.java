package com.anpoz.cutitdown.Utils;

import android.content.Context;

import com.anpoz.cutitdown.Beans.BaiduUrl;
import com.anpoz.cutitdown.Beans.IsgdUrl;
import com.anpoz.cutitdown.Beans.SinaUrl;
import com.anpoz.cutitdown.Beans.Url;
import com.anpoz.cutitdown.Beans._985soUrl;
import com.anpoz.cutitdown.R;
import com.anpoz.cutitdown.UrlShortener.BaiduUrlShortener;
import com.anpoz.cutitdown.UrlShortener.SinaUrlShortener;
import com.anpoz.cutitdown.UrlShortener._985soUrlShortener;
import com.anpoz.cutitdown.UrlShortener.isgdUrlShortener;

/**
 * Created by anpoz on 2015/8/20.
 */
public class UrlShortenerManager {

    private String mUrlShortenerType;
    private Context mContext;

    public UrlShortenerManager(String urlShortenerType,Context context) {
        this.mUrlShortenerType = urlShortenerType;
        this.mContext=context;
    }

    public Url getUrlByLongUrl(String long_url) throws Exception{
        Url mUrl=new Url();
        mUrl.setDate(System.currentTimeMillis());
        mUrl.setLongUrl(long_url);
        mUrl.setStared(0);
        mUrl.setStatus(0);
        switch (mUrlShortenerType){
            case "1"://sina
                SinaUrl sinaUrl= new SinaUrlShortener().getUrlObject(long_url);
                if (sinaUrl.isResult()){
                    mUrl.setShortUrl(sinaUrl.getUrl_short());
                }else {
                    mUrl.setShortUrl("");
                    mUrl.setStatus(-1);
                    mUrl.setErr_message(mContext.getResources().getString(R.string.msg_url_unavailable));
                }
                break;
            case "2"://baidu
                BaiduUrl baiduUrl=new BaiduUrlShortener().getUrlObject(long_url);
                if (baiduUrl.getStatus()==0){
                    mUrl.setShortUrl(baiduUrl.getTinyurl());
                }else {
                    mUrl.setShortUrl("");
                    mUrl.setStatus(-1);
                    //TODO 替换为具体的错误信息
                    mUrl.setErr_message(baiduUrl.getErr_msg());
                }
                break;
            case "3"://985so
                _985soUrl soUrl=new _985soUrlShortener().getUrlObject(long_url);
                if (soUrl.getError()==0){
                    mUrl.setShortUrl(soUrl.getUrl());
                }else if (soUrl.getError()==-1){
                    mUrl.setShortUrl("");
                    mUrl.setStatus(-1);
                    mUrl.setErr_message(mContext.getResources().getString(R.string.msg_url_unsafe_illegal));
                }else {
                    mUrl.setShortUrl("");
                    mUrl.setStatus(-1);
                    mUrl.setErr_message(mContext.getResources().getString(R.string.msg_url_other_reason));
                }
                break;
            case "4"://isgd
                IsgdUrl isgdUrl=new isgdUrlShortener().getUrlObject(long_url);
                if (isgdUrl.getErrorcode()==1){
                    mUrl.setShortUrl("");
                    mUrl.setStatus(-1);
                    mUrl.setErr_message(mContext.getResources().getString(R.string.msg_url_unsafe_illegal));
                }else if (isgdUrl.getErrorcode()==2||isgdUrl.getErrorcode()==3||isgdUrl.getErrorcode()==4){
                    mUrl.setShortUrl("");
                    mUrl.setStatus(-1);
                    mUrl.setErr_message(mContext.getResources().getString(R.string.msg_url_server_busy));
                }else {
                    mUrl.setShortUrl(isgdUrl.getShorturl());
                }
                break;
        }
        return mUrl;
    }
}
