package com.surekam.pigfeed.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.RequestParams;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.FeedFormulaVo;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FormulaAdapter extends BaseAdapter {
	private Context mContext;
	private List<FeedFormulaVo> mBoutiques;
	private LayoutInflater mInflater;

	public FormulaAdapter(Context context, List<FeedFormulaVo> boutiques) {
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
			view = mInflater.inflate(R.layout.item_list_formula,
					viewGroup, false);

			viewHolder.mFormula=(RelativeLayout)view.findViewById(R.id.rl_formula);
			viewHolder.mFormulaName=(TextView)view.findViewById(R.id.tv_formula_name);

			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.mFormulaName.setText(mBoutiques.get(i).name);
		return view;
	}

	class ViewHolder {
		private RelativeLayout mFormula;
		private TextView mFormulaName;
	}
}

