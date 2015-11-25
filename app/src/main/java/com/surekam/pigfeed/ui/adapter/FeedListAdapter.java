package com.surekam.pigfeed.ui.adapter;

import java.util.List;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.FeedFormulaRecommandVo;
import com.surekam.pigfeed.bean.NutritionVo;
import com.surekam.pigfeed.ui.adapter.NutritionListAdapter.ViewStation;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * 现在是推荐的配方饲料组成*/
public class FeedListAdapter  extends BaseAdapter {

	private List<FeedFormulaRecommandVo> list;
	private Context context;
	private int layout;

	private LayoutInflater inflater;

	public FeedListAdapter(List<FeedFormulaRecommandVo> list, Context context,
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
						.findViewById(R.id.formula_recommand_list_name);
				cache.nuNum = (TextView) convertView
						.findViewById(R.id.formula_recommand_list_num);
				cache.nuUnit = (TextView) convertView
						.findViewById(R.id.formula_recommand_list_unit);
				cache.nuValue=(TextView) convertView
						.findViewById(R.id.formula_recommand_list_value);
				convertView.setTag(cache);
			} else {
				cache = (ViewStation) convertView.getTag();
			}

			FeedFormulaRecommandVo t = list.get(position);
			cache.nuName.setText(t.name);
			cache.nuNum.setText(t.systemUnitNum);
			cache.nuUnit.setText(t.systemUnitName);
			cache.nuValue.setText(t.value+"");
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
		public TextView nuValue;
	}
}
