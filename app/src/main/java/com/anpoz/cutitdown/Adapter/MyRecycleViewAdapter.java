package com.anpoz.cutitdown.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anpoz.cutitdown.Beans.Url;
import com.anpoz.cutitdown.R;
import com.anpoz.cutitdown.Utils.Logger;
import com.anpoz.cutitdown.Utils.Provider;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created by anpoz on 2015/8/10.
 */
public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.ViewHolder> {

    private List<Url> mDatas;
    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;

    private Context mContext;

    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(View v, int position);
    }

    public MyRecycleViewAdapter(Context context, List<Url> mDatas) {
        this.mContext = context;
        this.mDatas = mDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_md_cardview, viewGroup, false);
        ViewHolder vh = new ViewHolder(view, mItemClickListener, mItemLongClickListener);
        return vh;
    }

    public void setItemClickListener(ItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        SimpleDateFormat format = new SimpleDateFormat(mContext.getResources().getString(R.string.date_format));

        final Url temp = mDatas.get(i);

        viewHolder.mShortUrl.setText(mDatas.get(i).getShortUrl());
        viewHolder.mLongUrl.setText(mDatas.get(i).getLongUrl());
        viewHolder.mButtonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.onClick(v);
            }
        });
        viewHolder.mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = mContext.getContentResolver()
                        .delete(Provider.UrlColumns.CONTENT_URI, Provider.UrlColumns._ID + "=?", new String[]{temp.getId() + ""});
                Logger.i("TAG", "delete changed count=" + count);
                notifyItemRemoved(mDatas.indexOf(temp));
                mDatas.remove(temp);
            }
        });

        viewHolder.mDate.setText(format.format(temp.getDate()));

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView mDate;
        public TextView mShortUrl;
        public TextView mLongUrl;
        public Button mButtonCopy;
        public Button mButtonDelete;

        private ItemClickListener mItemClickListener;
        private ItemLongClickListener mItemLongClickListener;

        public ViewHolder(View itemView, ItemClickListener mItemClickListener,
                          ItemLongClickListener mItemLongClickListener) {
            super(itemView);
            mDate = (TextView) itemView.findViewById(R.id.item_date);

            mShortUrl = (TextView) itemView.findViewById(R.id.card_short_url);
            mLongUrl = (TextView) itemView.findViewById(R.id.card_long_url);

            mButtonCopy = (Button) itemView.findViewById(R.id.card_copy_btn);
            mButtonDelete = (Button) itemView.findViewById(R.id.card_delete_btn);

            this.mItemClickListener = mItemClickListener;
            this.mItemLongClickListener = mItemLongClickListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(v, getPosition());
            }
            return true;
        }
    }
}
