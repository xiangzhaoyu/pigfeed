package com.surekam.pigfeed.ui.adapter;

import java.util.List;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.AgencyVo;
import com.surekam.pigfeed.bean.FeedFormulaRecommandVo;
import com.surekam.pigfeed.ui.adapter.FeedListAdapter.ViewStation;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AgencyListAdapter extends BaseAdapter {
	private List<AgencyVo> list;
	private Context context;
	private int layout;

	private LayoutInflater inflater;

	public AgencyListAdapter(List<AgencyVo> list, Context context,
			int layout) {
		this.list = list;
		this.context = context;
		this.layout = layout;

		inflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
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
				cache.nuName = (TextView) convertView
						.findViewById(R.id.feed_agency_list_name);
				cache.nuValue = (TextView) convertView
						.findViewById(R.id.feed_agency_list_value);
				cache.nuPhone = (TextView) convertView
						.findViewById(R.id.feed_agency_list_phone);
				convertView.setTag(cache);
			} else {
				cache = (ViewStation) convertView.getTag();
			}

			AgencyVo t = list.get(position);
			cache.nuName.setText(t.name);
			cache.nuValue.setText(t.systemUnitPrice);
			cache.nuPhone.setText(t.phone1);
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
		public TextView nuName;
		public TextView nuPhone;
		public TextView nuValue;
	}

}
