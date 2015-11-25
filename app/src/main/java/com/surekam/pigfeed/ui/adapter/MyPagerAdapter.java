package com.surekam.pigfeed.ui.adapter;

import java.util.ArrayList;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.ImageInfo;
import com.surekam.pigfeed.ui.activity.ActivityFeedQuery;
import com.surekam.pigfeed.ui.activity.ActivityFormulaQuery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义适配器
 * 
 * 
 * 
 */
public class MyPagerAdapter extends PagerAdapter {
	Vibrator vibrator;
	ArrayList<ImageInfo> data;
	Activity activity;
	LayoutParams params;

	public MyPagerAdapter(Activity activity, ArrayList<ImageInfo> data) {
		this.activity = activity;
		this.data = data;
		vibrator = (Vibrator) activity
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int index) {
		Log.v("test", index + "index");

		View view = activity.getLayoutInflater().inflate(
				R.layout.fragment_main1_grid, null);
		GridView gridView = (GridView) view.findViewById(R.id.gridView1);
		gridView.setNumColumns(2);
		gridView.setVerticalSpacing(5);
		gridView.setHorizontalSpacing(5);
		gridView.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return 4;
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
				View item = LayoutInflater.from(activity).inflate(
						R.layout.fragment_main1_grid_item, null);
				try{
					ImageView iv = (ImageView) item.findViewById(R.id.imageView1);
					RelativeLayout relativeLayout = (RelativeLayout) item
							.findViewById(R.id.relativeLayout);
					iv.setImageResource((data.get(index * 8 + position)).imageId);
					relativeLayout.setBackgroundResource((data.get(index * 8
							+ position)).bgId);
					relativeLayout.getBackground().setAlpha(225);
					TextView tv = (TextView) item.findViewById(R.id.msg);
					tv.setText((data.get(index * 8 + position)).imageMsg);
	
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
					Intent aquery=new Intent(activity,ActivityFormulaQuery.class);
					activity.startActivity(aquery);
				}break;
				case 1:{
					Intent aquery=new Intent(activity,ActivityFeedQuery.class);
					activity.startActivity(aquery);
				}break;
				case 2:{
					Intent aquery=new Intent(activity,ActivityFeedQuery.class);
					aquery.putExtra("showWho", 1);
					activity.startActivity(aquery);
				}break;
				case 3:{

				}break;
				default: {
				}
					break;
				}
				// Toast用于向用户显示一些帮助/提示
			}
		});

		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				View view = arg1;
				arg1.setVisibility(View.INVISIBLE);
				params = new WindowManager.LayoutParams();
				vibrator.vibrate(2500);
				return true;
			}
		});
		((ViewPager) container).addView(view);
		return view;
	}
}
