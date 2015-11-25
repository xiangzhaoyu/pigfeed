package com.surekam.pigfeed.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.common.BaseActivity;
import com.surekam.pigfeed.ui.activity.GuideActivity;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 * 
 * @author liukai
 */
public class AppStart extends BaseActivity {
	AppContext mApp;
	String json = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.app_start, null);

		setContentView(view);
		_context = this;

		if (AppContext.debug) {
			//UIHelper.ToastMessage(this, "您现在进入的是调试版本!");
		}

		TextView tv = (TextView) findViewById(R.id.app_start_version);
		tv.setText(AppContext.getInstance().getVersion());

//		ImageView iv = (ImageView) findViewById(R.id.app_start_iv_logo);
//		iv.setVisibility(View.VISIBLE);

		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
		aa.setDuration(5000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});

	}

	/**
	 * 跳转到主界面
	 */
	private void redirectTo() {

		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		AppContext.getInstance()._screenWidth = dm.widthPixels;
		AppContext.getInstance()._screenHeight = dm.heightPixels;

		
		Intent intent = new Intent(_context, GuideActivity.class);
		startActivity(intent);
		finish();
		
//		
//		UIHelper.startMain(_context);
//		finish();

	}

}
