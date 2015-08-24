package com.anpoz.cutitdown.Beans;

/**
 * Created by anpoz on 2015/8/20.
 */
public class IsgdUrl {
    private String shorturl;
    private int errorcode;
    private String errormessage;

    /**
     * Error code 1 - there was a problem with the original long URL provided
     * Error code 2 - there was a problem with the short URL provided (for custom short URLs)
     * Error code 3 - our rate limit was exceeded (your app should wait before trying again)
     * Error code 4 - any other error (includes potential problems with our service such as a maintenance period)
     */

    public String getShorturl() {
        return shorturl;
    }

    public void setShorturl(String shorturl) {
        this.shorturl = shorturl;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }
}
