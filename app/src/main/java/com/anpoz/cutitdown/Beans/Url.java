package com.anpoz.cutitdown.Beans;

import java.util.Date;

/**
 * Created by anpoz on 2015/8/10.
 */
public class Url {
    private String shortUrl;
    private String longUrl;
    private long date;
    private int id;
    private int stared;
    private int status;
    private String err_message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErr_message() {
        return err_message;
    }

    public void setErr_message(String err_message) {
        this.err_message = err_message;
    }

    public int getStared() {
        return stared;
    }

    public void setStared(int stared) {
        this.stared = stared;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
