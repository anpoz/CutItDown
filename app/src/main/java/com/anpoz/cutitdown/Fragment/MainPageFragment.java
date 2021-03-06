package com.anpoz.cutitdown.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.anpoz.cutitdown.Adapter.MyRecycleViewAdapter;
import com.anpoz.cutitdown.Beans.Url;
import com.anpoz.cutitdown.R;
import com.anpoz.cutitdown.Utils.LogUtils;
import com.anpoz.cutitdown.Utils.Provider;
import com.anpoz.cutitdown.Utils.UrlShortener;
import com.anpoz.cutitdown.View.ClearableEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPageFragment extends Fragment implements MyRecycleViewAdapter.ItemClickListener, MyRecycleViewAdapter.ItemLongClickListener {

    private View rootView;

    private RecyclerView mRecycleView;
    private List<Url> mDatas;
    private MyRecycleViewAdapter mAdapter;
    private Toast mToast = null;

    //    private ProgressBarCircularIndeterminate mTopProgressBar;

    private Dialog mDialog;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            mTopProgressBar.setVisibility(ProgressBarIndeterminate.GONE);

            if (msg.arg1 == 0) {
                Url item = (Url) msg.obj;
                item.setStared(0);
                item.setDate(System.currentTimeMillis());
                item.setId(addData(item));
                item.setStatus(0);
                mDatas.add(0, item);
                mAdapter.notifyItemInserted(0);
                mRecycleView.scrollToPosition(0);
            } else if (msg.arg1 == 1) {
                makeText((String) msg.obj);
            }

            super.handleMessage(msg);
        }
    };

    private OnItemStaredListener staredListener;


    public MainPageFragment() {
        // Required empty public constructor
    }

    public interface OnItemStaredListener {
        void OnItemStared();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main_page, container, false);
        initViews();
        initData();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        staredListener = (OnItemStaredListener) activity;
    }

    /**
     * 初始化ItemTouchHelper
     *
     * @param recyclerView
     */
    private void initItemTouchHelper(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //不处理滑动事件
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deleteData(position);
                mDatas.remove(position);
                mAdapter.notifyItemRemoved(position);
            }
        }).attachToRecyclerView(recyclerView);
    }


    private void initViews() {
        mRecycleView = (RecyclerView) rootView.findViewById(R.id.tab1_recycler_view);
        initItemTouchHelper(mRecycleView);

        FloatingActionButton buttonFloat = (FloatingActionButton) getActivity().findViewById(R.id.fab_main_fragment);

        mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_new_url);
        mDialog.findViewById(R.id.dialog_input_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpenNetwork()) {
                    makeText(getResources().getString(R.string.msg_network_unavailable));
                    return;
                }

                ClearableEditText editText = (ClearableEditText) mDialog.findViewById(R.id.dialog_input);

                final String url = editText.getText().toString();
                if (TextUtils.isEmpty(url)) {
                    makeText(getResources().getString(R.string.msg_url_cant_null));
                    return;
                }
                editText.setText("");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                addItemThread(url, preferences.getString("list_api_preference", "1"));
                mDialog.dismiss();
            }
        });

        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
            }
        });

    }

    private void addItemThread(String url, String list_api_preference) {
        UrlShortener shortener = new UrlShortener(list_api_preference, getActivity(), handler);

        String raw_url = url;
        if (!url.contains("http://")) {
            raw_url = "http://" + raw_url;
        }
        shortener.makeUrlShortened(raw_url);
    }

    /**
     * 对网络连接状态进行判断
     *
     * @return true, 可用； false， 不可用
     */
    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo().isAvailable();

    }

    private void makeText(String message) {
        if (mToast == null) {
            mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mAdapter = new MyRecycleViewAdapter(getActivity(), mDatas);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setItemClickListener(this);
        mAdapter.setItemLongClickListener(this);
        mRecycleView.setAdapter(mAdapter);

        updateData();
    }

    private int addData(Url item) {
        ContentValues cv = new ContentValues();
        cv.put(Provider.UrlColumns.URL_LONG, item.getLongUrl());
        cv.put(Provider.UrlColumns.URL_SHORT, item.getShortUrl());
        cv.put(Provider.UrlColumns.URL_DATE, item.getDate());
        cv.put(Provider.UrlColumns.URL_STARED, item.getStared());
        Uri uri = getActivity().getContentResolver().insert(Provider.UrlColumns.CONTENT_URI, cv);
        LogUtils.i("insert uri=" + uri);
        String lastPath = uri.getLastPathSegment();
        if (TextUtils.isEmpty(lastPath)) {
            LogUtils.i("insert failure!");
        } else {
            LogUtils.i("insert success! the id is " + lastPath);
        }
        return Integer.parseInt(lastPath);
    }

    public void updateData() {
        mDatas.clear();
        Cursor cursor = getActivity().getContentResolver()
                .query(Provider.UrlColumns.CONTENT_URI, null,
                        Provider.UrlColumns.URL_STARED + "=?", new String[]{0 + ""}, null);

        int url_long = cursor.getColumnIndex(Provider.UrlColumns.URL_LONG);
        int url_short = cursor.getColumnIndex(Provider.UrlColumns.URL_SHORT);
        int url_date = cursor.getColumnIndex(Provider.UrlColumns.URL_DATE);
        int url_stared = cursor.getColumnIndex(Provider.UrlColumns.URL_STARED);
        int id = cursor.getColumnIndex(Provider.UrlColumns._ID);
        while (cursor.moveToNext()) {
            Url item = new Url();
            item.setLongUrl(cursor.getString(url_long));
            item.setShortUrl(cursor.getString(url_short));
            item.setDate(cursor.getLong(url_date));
            item.setStared(cursor.getInt(url_stared));
            item.setId(cursor.getInt(id));
            LogUtils.i(item.toString());
            mDatas.add(item);
        }
        cursor.close();
        mAdapter.notifyDataSetChanged();
    }

    private void updateStared(int id) {
        ContentValues cv = new ContentValues();
        cv.put(Provider.UrlColumns.URL_STARED, 1);
        int count = getActivity().getContentResolver()
                .update(Provider.UrlColumns.CONTENT_URI, cv, Provider.UrlColumns._ID + "=?", new String[]{id + ""});
        LogUtils.i("update changed count=" + count);
    }

    private void deleteData(int position) {
        int count = getActivity().getContentResolver()
                .delete(Provider.UrlColumns.CONTENT_URI, Provider.UrlColumns._ID + "=?", new String[]{mDatas.get(position).getId() + ""});
        LogUtils.i("delete changed count=" + count);
    }


    @Override
    public void onItemClick(View v, int position) {
        Url item = mDatas.get(position);
        ClipboardManager clipboardManager = (ClipboardManager) getActivity()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Shortened Url", item.getShortUrl()));

        makeText(getResources().getString(R.string.msg_url_already_copy));

    }

    @Override
    public void onItemLongClick(final View v, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(getResources().getStringArray(R.array.item_context_menu_tab1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://复制
                        onItemClick(v, position);
                        break;
                    case 1://收藏
                        updateStared(mDatas.get(position).getId());
                        mDatas.remove(position);
                        mAdapter.notifyDataSetChanged();
                        staredListener.OnItemStared();
                        break;
                    case 2://删除
                        deleteData(position);
                        mDatas.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        break;
                    case 3://打开
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(mDatas.get(position).getShortUrl()));
                        startActivity(intent);
                        break;
                    case 4://分享
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mDatas.get(position).getShortUrl());
                        shareIntent.putExtra(Intent.EXTRA_TEXT, mDatas.get(position).getShortUrl());
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(shareIntent,
                                getActivity().getResources().getString(R.string.share_link_to)));
                        break;
                }
            }
        });
        builder.show();
    }
}
