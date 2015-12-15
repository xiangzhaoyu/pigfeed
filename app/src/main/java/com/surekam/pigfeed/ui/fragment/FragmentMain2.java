package com.surekam.pigfeed.ui.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.ImageInfo;
import com.surekam.pigfeed.common.BaseFragment;
import com.surekam.pigfeed.ui.activity.ActivityAgencyQuery;
import com.surekam.pigfeed.ui.activity.ActivityFeedQuery;
import com.surekam.pigfeed.ui.activity.ActivityFormulaQuery;

/*
 * 第二种主界面风格*/
public class FragmentMain2 extends BaseFragment implements OnClickListener {

	ArrayList<ImageInfo> data; // 菜单数据
	private GridView gridView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		_view = inflater.inflate(R.layout.fragment_main2, null);
		_context = getActivity();
		initialView();
		return _view;
	}
	
	private void initialView(){
		gridView=(GridView) _view.findViewById(R.id.gridView1);

		// 初始化数据
        initData();
		
		gridView.setNumColumns(2);
		gridView.setVerticalSpacing(20);
		gridView.setHorizontalSpacing(20);
		gridView.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return data.size();
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View item = LayoutInflater.from(_context).inflate(
						R.layout.fragment_main1_grid_item, null);
				try{
					ImageView iv = (ImageView) item.findViewById(R.id.imageView1);
					RelativeLayout relativeLayout = (RelativeLayout) item
							.findViewById(R.id.relativeLayout);
					iv.setImageResource((data.get(position)).imageId);
					relativeLayout.setBackgroundResource((data.get(position)).bgId);
					relativeLayout.getBackground().setAlpha(225);
					TextView tv = (TextView) item.findViewById(R.id.msg);
					tv.setText((data.get(position)).imageMsg);
	
				}catch(Exception e){}
				return item;
			}
		});

		// 添加点击事件
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int index = arg2;// id是从0开始的
				// Toast.makeText(getApplicationContext(), "你按下了选项：" + index, 0)
				// .show();
				switch (index) {
				case 0:{
					Intent aquery=new Intent(_context,ActivityFormulaQuery.class);
					_context.startActivity(aquery);
				}break;
				case 1:{
					Intent aquery=new Intent(_context,ActivityFeedQuery.class);
					_context.startActivity(aquery);
				}break;
				case 2:{
					Intent aquery=new Intent(_context,ActivityFeedQuery.class);
					aquery.putExtra("showWho", 1);
					_context.startActivity(aquery);
				}break;
				case 3:{
					Intent aquery=new Intent(_context,ActivityAgencyQuery.class);
					_context.startActivity(aquery);
				}break;
				case 4:{
					Intent aquery=new Intent(_context,ActivityFormulaQuery.class);
					aquery.putExtra("showWho", 1);
					_context.startActivity(aquery);
				}break;
				default: {
				}
					break;
				}
				// Toast用于向用户显示一些帮助/提示
			}
		});

    }
	
	private void initData() {
        data = new ArrayList<ImageInfo>();
        
        data.add(new ImageInfo("配方检索", R.drawable.icon1, R.drawable.icon_bg01));
        data.add(new ImageInfo("饲料检索", R.drawable.icon2, R.drawable.icon_bg01));
        data.add(new ImageInfo("价格趋势", R.drawable.icon3, R.drawable.icon_bg01));
		data.add(new ImageInfo("联系经销商", R.drawable.icon3, R.drawable.icon_bg01));
        data.add(new ImageInfo("智能配方计算", R.drawable.icon4, R.drawable.icon_bg01));
        
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//Toast.makeText(_context.getApplicationContext(), v.getId()+"--"+v.toString(), 3000);
	}

}
