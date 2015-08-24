package com.anpoz.cutitdown.Beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anpoz on 2015/8/7.
 */
public class SinaUrl {
    private String url_short;
    private String url_long;
    private String object_type;
    private String object_id;
    private int type;
    private boolean result;

    public String getUrl_short() {
        return url_short;
    }

    public String getObject_type() {
        return object_type;
    }

    public void setObject_type(String object_type) {
        this.object_type = object_type;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUrl_short(String url_short) {
        this.url_short = url_short;
    }

    public String getUrl_long() {
        return url_long;
    }

    public void setUrl_long(String url_long) {
        this.url_long = url_long;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
