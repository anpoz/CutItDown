package com.anpoz.cutitdown.Utils;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 存放跟数据库有关的常量
 * Created by anpoz on 2015/8/17.
 */
public class Provider {
    //这是每个Provider的标识.在Manifest中使用

    public static final String AUTHORITY = "com.anpoz.cutitdown";

    public static final String CONTENT_TYPE = "vnd.android.cursor.item/vnd.anpoz.url";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.anpoz.url";

    /**
     * 跟url_convert表相关的常量
     */
    public static final class UrlColumns implements BaseColumns {
        //CONTENT_URI跟数据库的表关联,最后根据CONTENT_URI来查询对应的表
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/urls");
        public static final String TABLE_NAME = "url_convert";
        public static final String DEFAULT_ORDER = "_id desc";

        public static final String URL_LONG = "url_long";
        public static final String URL_SHORT = "url_short";
        public static final String URL_DATE = "url_date";
        public static final String URL_STARED = "url_stared";
    }
}
