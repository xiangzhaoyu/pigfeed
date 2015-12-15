package com.surekam.pigfeed.ui.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.FeedVo;

import java.util.List;

/**
 * Created by xiangzy_1 on 2015/12/15.
 */
public class SelectFeedAdapter extends BaseAdapter {
    private Context mContext;
    private List<FeedVo> mBoutiques;
    private LayoutInflater mInflater;

    public SelectFeedAdapter(Context context, List<FeedVo> boutiques) {
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
            view = mInflater.inflate(R.layout.select_feed_item,
                    viewGroup, false);

            viewHolder.feedName=(TextView)view.findViewById(R.id.tv_select_feed_name);
            viewHolder.feedW=(EditText)view.findViewById(R.id.et_select_feed_w);
            viewHolder.feedSelect=(ImageView)view.findViewById(R.id.column_select);
            viewHolder.feed=mBoutiques.get(i);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        viewHolder.feedSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FeedVo ff = viewHolder.feed;
                if (ff != null && ff.id != null) {
                    for (FeedVo f : mBoutiques) {
                        if (f.id == ff.id) {
                            if (f.sysFlag.equals("xuan")) {
                                f.sysFlag = "";//xuan为选中，其它为没选中
                            } else {
                                f.sysFlag = "xuan";//xuan为选中，其它为没选中
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        });

        viewHolder.feedName.setText(mBoutiques.get(i).name);
        if(mBoutiques.get(i)!=null&&mBoutiques.get(i).sysFlag.equals("xuan")){
            viewHolder.feedSelect.setImageResource(R.drawable.phone_select);
        }else{
            viewHolder.feedSelect.setImageResource(R.drawable.phone_no_select);
        }
        viewHolder.feedW.setText(mBoutiques.get(i).creatorId+"");//createid存重量
        viewHolder.feedW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                FeedVo ff = viewHolder.feed;
                if (ff != null && ff.id != null) {
                    for (FeedVo f : mBoutiques) {
                        if (f.id == ff.id) {
                            f.creatorId=Long.parseLong(editable.toString());
                        }
                    }
                    //notifyDataSetChanged();
                }}catch (Exception e){
                    //UIHelper.ToastLongMessage(mContext,"请输入数字！");
                }
            }
        });
        return view;
    }

    class ViewHolder {
        private FeedVo feed;
        private TextView feedName;
        private EditText feedW;
        private ImageView feedSelect;
    }
}