package com.anpoz.cutitdown.UrlShortener;

import com.anpoz.cutitdown.Beans.BaiduUrl;
import com.anpoz.cutitdown.Utils.Logger;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
public class BaiduUrlShortener {
    private static final String API_URL="http://dwz.cn/create.php";

    /**
     * 将URL对应的json格式数据转化为封装的bean
     * @param long_url
     * @return
     */
    public BaiduUrl getUrlObject(String long_url) throws Exception{
        BaiduUrl baiduUrl=null;

        URL urlRequest=new URL(API_URL);
        HttpURLConnection connection= (HttpURLConnection) urlRequest.openConnection();
        connection.setReadTimeout(5 * 1000);
        connection.setConnectTimeout(5 * 1000);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.connect();

        DataOutputStream out=new DataOutputStream(connection.getOutputStream());

        String content="url="+URLEncoder.encode(long_url, "utf-8");

        out.writeBytes(content);
        out.flush();
        out.close();

        String jsonString=readStream(connection.getInputStream());
        Logger.d("BaiduUrlShortener", "jsonString:" + jsonString);
        Gson gson=new Gson();
        baiduUrl=gson.fromJson(jsonString, BaiduUrl.class);

        return baiduUrl;
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
