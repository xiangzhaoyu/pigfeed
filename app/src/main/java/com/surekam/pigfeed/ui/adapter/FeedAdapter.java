package com.surekam.pigfeed.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.bean.FeedVo;

import java.util.List;

/**
 * Created by xiangzy_1 on 2015/11/26.
 */
public class FeedAdapter extends BaseAdapter {
    private Context mContext;
    private List<FeedVo> mBoutiques;
    private LayoutInflater mInflater;

    public FeedAdapter(Context context, List<FeedVo> boutiques) {
        mContext = context;
        mBoutiques = boutiques;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mBoutiques.size();
    }

    @Override
    public Object getItem(int i) {
        return mBoutiques.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_list_feed,
                    viewGroup, false);

            viewHolder.mFeed=(RelativeLayout)view.findViewById(R.id.rl_feed);
            viewHolder.mFeedName=(TextView)view.findViewById(R.id.tv_feed_name);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mFeedName.setText(mBoutiques.get(i).name);
        return view;
    }

    class ViewHolder {
        private RelativeLayout mFeed;
        private TextView mFeedName;
    }
}
