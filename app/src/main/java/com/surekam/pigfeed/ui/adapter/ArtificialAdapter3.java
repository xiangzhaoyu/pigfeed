package com.surekam.pigfeed.ui.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.ArtificialNur;

import java.util.List;

/**
 * Created by xiangzy_1 on 2015/12/2.
 */
public class ArtificialAdapter3 extends BaseAdapter {

    private List<ArtificialNur> list;
    private Context context;
    private int layout;

    private LayoutInflater inflater;

    public ArtificialAdapter3(List<ArtificialNur> list, Context context,
                              int layout) {
        this.list = list;
        this.context = context;
        this.layout = layout;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*
     * 获取总行数
     *
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        return list.size();
    }

    /*
     * 根据索引获取数据
     *
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return list.get(position);
    }

    /*
     * item的id，很少使用到
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewStation cache;
        try {
            if (convertView == null) {
                convertView = inflater.inflate(layout, null);
                cache = new ViewStation();
                cache.name = (TextView) convertView
                        .findViewById(R.id.feed_name);
                cache.per = (TextView) convertView
                        .findViewById(R.id.feed_per);
                cache.chae=(TextView)convertView.findViewById(R.id.feed_chae);
                convertView.setTag(cache);
            } else {
                cache = (ViewStation) convertView.getTag();
            }

            ArtificialNur t = list.get(position);
            cache.name.setText(t.Mname);
            cache.per.setText(new java.text.DecimalFormat("0.000").format(t.UnitNumber*100)+"%");
            cache.chae.setText(t.Cname+"");//用cname存差额
        } catch (Exception e) {
        }
        return convertView;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

    public final class ViewStation {
        public TextView name;
        public TextView per;
        public TextView chae;
    }

    }