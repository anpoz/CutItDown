package com.anpoz.cutitdown.UrlShortener;

import com.anpoz.cutitdown.Beans.IsgdUrl;
import com.anpoz.cutitdown.Utils.Logger;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by anpoz on 2015/8/20.
 */
public class isgdUrlShortener {

    private static final String API_URL="http://is.gd/create.php?format=json&url=";

    /**
     * 将URL对应的json格式数据转化为封装的bean
     * @param long_url
     * @return
     */
    public IsgdUrl getUrlObject(String long_url) throws Exception{
        IsgdUrl isgdUrl = null;

        String url=API_URL+ URLEncoder.encode(long_url, "utf-8");
        URL urlRequest=new URL(url);
        HttpURLConnection connection= (HttpURLConnection) urlRequest.openConnection();
        connection.setReadTimeout(5 * 1000);
        connection.setConnectTimeout(5 * 1000);
        connection.setRequestMethod("GET");

        String jsonString=readStream(connection.getInputStream());
        Logger.d("isgdUrlShortener", "jsonString:" + jsonString);
        Gson gson=new Gson();

        isgdUrl=gson.fromJson(jsonString, IsgdUrl.class);

        return isgdUrl;
    }

    /**
     * 通过inputStream解析网页返回的数据
     * @param is
     * @return
     */
    private String readStream(InputStream is){
        String result="";
        BufferedReader br=null;
        try{
            String line="";
            br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
            while((line=br.readLine())!=null){
                result+=line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
