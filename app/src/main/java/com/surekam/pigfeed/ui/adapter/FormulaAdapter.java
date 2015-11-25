package com.surekam.pigfeed.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.surekam.pigfeed.bean.FeedFormulaVo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FormulaAdapter extends BaseAdapter {
	List<FeedFormulaVo> mDatas = new ArrayList<FeedFormulaVo>();
	LayoutInflater mInflater;
	int mTextRow = 1; // 文字自动换行造成最后一列显示不全
	Context mContext;
	int _style = 1;

	public FormulaAdapter(Context ctx) {
		mInflater = LayoutInflater.from(ctx);
		mContext = ctx;
	}

	public void setDatas(List<FeedFormulaVo> datas) {
		mDatas = datas;
	}

	public void setStyle(int style) {
		_style = style;
	}

	@Override
	public int getCount() {
		return mDatas.size();

	}

	@Override
	public FeedFormulaVo getItem(int position) {

		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

//		if (convertView == null) {
//			convertView = mInflater.inflate(R.layout.list_item_register, null);
//		}
//
//		TextView tv = (TextView) convertView.findViewById(R.id.item_register_text);
//		TextView tv2 = (TextView) convertView.findViewById(R.id.item_register_text_2);
//
//		FeedFormulaVo data = getItem(position);
//
//		if (_style == 1) {
//			String text = data.getSimple();
//			UIHelper.setTvTextHtml(tv, text);
//			tv2.setVisibility(View.GONE);
//
//		}

//		return convertView;
		return null;
	}
}

