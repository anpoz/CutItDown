package com.anpoz.cutitdown.Beans;

/**
 * Created by anpoz on 2015/8/20.
 */
public class _985soUrl {
    private String url;
    /**
     * 0:缩短成功。
     * -1:因为安全原因或网址不合法被拦截。
     * -2:其它错误原因。
     */
    private int error;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
