package com.surekam.pigfeed.ui.adapter;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.NutritionVo;
import com.surekam.pigfeed.tools.StringUtils;

public class NutritionListAdapter extends BaseAdapter {

	private List<NutritionVo> list;
	private Context context;
	private int layout;

	private LayoutInflater inflater;

	public NutritionListAdapter(List<NutritionVo> list, Context context,
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
				cache.nuName = (TextView) convertView
						.findViewById(R.id.formula_nutrition_list_name);
				cache.nuNum = (TextView) convertView
						.findViewById(R.id.formula_nutrition_list_num);
				cache.nuUnit = (TextView) convertView
						.findViewById(R.id.formula_nutrition_list_unit);
				convertView.setTag(cache);
			} else {
				cache = (ViewStation) convertView.getTag();
			}

			NutritionVo t = list.get(position);
			cache.nuName.setText(t.name);
			cache.nuNum.setText(t.systemUnitNum);
			cache.nuUnit.setText(t.systemUnitName);
			if(t!=null&&t.systemUnitName.equals("百分比")){
				double x=0;
				try{
					x=Double.parseDouble(t.systemUnitNum)*100;
				}catch (Exception e){}
				cache.nuNum.setText(new java.text.DecimalFormat("0.000").format(x));
			}
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
		public TextView nuNum;
		public TextView nuUnit;

	}

}
