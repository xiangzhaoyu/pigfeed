package com.surekam.pigfeed.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.ui.activity.ActivityLogin;
import com.surekam.pigfeed.ui.activity.ActivityMain;


/**
 * 
 * @{# ViewPagerAdapter.java Create on 2013-5-2 下午11:03:39
 * 
 *     class desc: 引导页面适配器
 * 
 *     <p>
 *     Copyright: Copyright(c) 2013
 *     </p>
 * @Version 1.0
 * @Author <a href="mailto:gaolei_xj@163.com">Leo</a>
 * 
 * 
 */
public class ViewPagerAdapter extends PagerAdapter {

	// 界面列表
	private List<View> views;
	private Activity activity;
    private boolean showNextTime=false;
    private Context _context;
	
	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
	    this._context=activity.getBaseContext();
	}

	// 销毁arg1位置的界面
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(views.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	// 获得当前界面数
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	// 初始化arg1位置的界面
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(views.get(arg1), 0);
		if (arg1 == views.size() - 1) {
			
			
			
		

			//获取CheckBox实例
			CheckBox cb = (CheckBox)arg0.findViewById(R.id.chkNextTime);
			//绑定监听器
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			            
			            @Override
			            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			                // TODO Auto-generated method stub
			              
			            	
			            	if(arg1)
			    			{
			    				showNextTime=false;
			    				
			    			}
			    			else
			    			{
			    				
			    				showNextTime=true;
			    			}
			            }
			        });
			
			
				
			
			ImageView mStartWeiboImageButton = (ImageView) arg0
					.findViewById(R.id.iv_start_weibo);
			mStartWeiboImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				// 设置已经引导
					
					goHome(showNextTime);

				}

			});
			
			
		
		}
		return views.get(arg1);
	}

	private void goHome(boolean next) {
		
		SharedPreferences preferences = _context.getSharedPreferences(
				"nexttime",_context.MODE_PRIVATE);
		
		Editor editor = preferences.edit();

		if(next){
			
			// 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
			editor.putBoolean("shownexttime", true);
			
		}
		else
		{
			editor.putBoolean("shownexttime", false);
		}
		
		editor.commit();
		// 跳转
		//Intent intent = new Intent(activity, ActivityLogin.class);
		Intent intent = new Intent(activity, ActivityMain.class);
		activity.startActivity(intent);
		activity.finish();
	}

	

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
