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
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.anpoz.cutitdown.Adapter.MyRecycleViewAdapter;
import com.anpoz.cutitdown.Beans.Url;
import com.anpoz.cutitdown.R;
import com.anpoz.cutitdown.Utils.Logger;
import com.anpoz.cutitdown.Utils.Provider;
import com.anpoz.cutitdown.Utils.UrlShortenerManager;


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

    private FloatingActionButton mButtonFloat;

//    private ProgressBarCircularIndeterminate mTopProgressBar;

    private Dialog mDialog;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            mTopProgressBar.setVisibility(ProgressBarIndeterminate.GONE);

            if (msg.arg1 == 0) {
                Url item = (Url) msg.obj;
                item.setStared(0);
                item.setId(addData(item));
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


    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

//        mTopProgressBar = (ProgressBarCircularIndeterminate) rootView.findViewById(R.id.progressBarCircularIndeterminate);
//        mTopProgressBar.setVisibility(ProgressBarIndeterminate.GONE);

        mRecycleView = (RecyclerView) rootView.findViewById(R.id.tab1_recycler_view);

        mButtonFloat = (FloatingActionButton) getActivity().findViewById(R.id.fab_main_fragment);

        mDialog=new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_new_url);
        mDialog.findViewById(R.id.dialog_input_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpenNetwork()) {
                    makeText(getResources().getString(R.string.msg_network_unavailable));
                    return;
                }

                EditText editText = (EditText) mDialog.findViewById(R.id.dialog_input);

                final String url = editText.getText().toString();
                if (TextUtils.isEmpty(url)) {
                    makeText(getResources().getString(R.string.msg_url_cant_null));
                    return;
                }
                editText.setText("");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                mTopProgressBar.setVisibility(ProgressBarIndeterminate.VISIBLE);
                addItemThread(url, preferences.getString("list_api_preference", "1"));
                mDialog.dismiss();
            }
        });

        mButtonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
            }
        });

    }

    private void addItemThread(final String url, String list_api_preference) {
        final UrlShortenerManager manager = new UrlShortenerManager(list_api_preference, getActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                String raw_url = url;
                if (!url.contains("http://")) {
                    raw_url = "http://" + raw_url;
                }
                Url item;
                try {
                    item = manager.getUrlByLongUrl(raw_url);
                    /**
                     * 不在子线程中进行UI操作
                     */
                    if (item.getStatus() != 0) {
                        message.arg1 = 1;
                        message.obj = item.getErr_message();
                        Log.d("tag", item.getErr_message());
                    } else {
                        message.arg1 = 0;
                        message.obj = item;
                    }
                    handler.sendMessage(message);
                } catch (Exception e) {
                    message.arg1 = 1;
                    message.obj = getResources().getString(R.string.msg_network_error);
                }
            }
        }).start();
    }

    /**
     * 对网络连接状态进行判断
     *
     * @return true, 可用； false， 不可用
     */
    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }

        return false;
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
        Logger.i("TAG", "insert uri=" + uri);
        String lastPath = uri.getLastPathSegment();
        if (TextUtils.isEmpty(lastPath)) {
            Logger.i("TAG", "insert failure!");
        } else {
            Logger.i("TAG", "insert success! the id is " + lastPath);
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
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Url item = new Url();
                item.setLongUrl(cursor.getString(url_long));
                item.setShortUrl(cursor.getString(url_short));
                item.setDate(cursor.getLong(url_date));
                item.setStared(cursor.getInt(url_stared));
                item.setId(cursor.getInt(id));
                Logger.i("TAG", item.toString());
                mDatas.add(item);
            }
        }
        cursor.close();
        mAdapter.notifyDataSetChanged();
    }

    private void updateStared(int id) {
        ContentValues cv = new ContentValues();
        cv.put(Provider.UrlColumns.URL_STARED, 1);
        int count = getActivity().getContentResolver()
                .update(Provider.UrlColumns.CONTENT_URI, cv, Provider.UrlColumns._ID + "=?", new String[]{id + ""});
        Logger.i("TAG", "update changed count=" + count);
    }

    private void deleteData(int position) {
        int count = getActivity().getContentResolver()
                .delete(Provider.UrlColumns.CONTENT_URI, Provider.UrlColumns._ID + "=?", new String[]{mDatas.get(position).getId() + ""});
        Logger.i("TAG", "delete changed count=" + count);
    }


    @Override
    public void onItemClick(View v, int position) {
        Url item = mDatas.get(position);
        ClipboardManager clipboardManager = (ClipboardManager) getActivity()
                .getSystemService(getActivity().CLIPBOARD_SERVICE);
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
                    case 0:
                        onItemClick(v, position);
                        break;
                    case 1:
                        updateStared(mDatas.get(position).getId());
                        mDatas.remove(position);
                        mAdapter.notifyDataSetChanged();
                        staredListener.OnItemStared();
                        break;
                    case 2:
                        deleteData(position);
                        mDatas.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        break;
                    case 3:
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(mDatas.get(position).getShortUrl()));
                        startActivity(intent);
                        break;
                }
            }
        });
        builder.show();
    }
}
