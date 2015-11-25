package com.surekam.pigfeed.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.app.AppContext;
import com.surekam.pigfeed.app.AppManager;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.tools.MenuUtil;

public class ActivitySetting extends PreferenceActivity {
	SharedPreferences mPreferences;
	AppContext ac;
	Context _context;

	CheckBoxPreference loadimage;
	CheckBoxPreference scroll;
	CheckBoxPreference voice;

	Preference account;
	Preference backcolor;
	Preference cache;
	Preference update;
	Preference about;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);

		// 设置显示Preferences
		addPreferencesFromResource(R.xml.preferences);

		ListView localListView = getListView();
		localListView.setBackgroundColor(0);
		localListView.setCacheColorHint(0);
		((ViewGroup) localListView.getParent()).removeView(localListView);
		ViewGroup localViewGroup = (ViewGroup) getLayoutInflater().inflate(
				R.layout.activity_setting, null);
		((ViewGroup) localViewGroup.findViewById(R.id.setting_content))
				.addView(localListView, -1, -1);
		setContentView(localViewGroup);

		ac = (AppContext) getApplication();
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		_context = this;

		initTitle();
		initView();
	}

	private void initTitle() {
		// MenuUtil.setMenuBackTransparent(_view);
		View v = findViewById(R.id.new_account_navigate_bar);
		MenuUtil.setMenuBack(this, v, true);
		MenuUtil.setMenuMore(this, v, false);
		MenuUtil.setMenuTitle(v, "系统设置");
	}

	public void back(View paramView) {
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		checkLogin();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		checkLogin();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

	private void checkLogin() {
		if (ac._isLogin) {
			account.setTitle("用户注销");
		} else {
			account.setTitle("用户登录");
		}
	}

	private void initView() {
		// 登录、注销
		account = (Preference) findPreference("account");
		checkLogin();
		account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				UIHelper.loginOrLogout(ActivitySetting.this);
				account.setTitle("用户登录");
				return true;
			}
		});

		/*
		 * //背景颜色 backcolor = (Preference)findPreference("backcolor");
		 * 
		 * backcolor.setSummary(ac.getBackColor());
		 * 
		 * backcolor.setOnPreferenceClickListener(new
		 * Preference.OnPreferenceClickListener() { public boolean
		 * onPreferenceClick(Preference preference) { String nowSum =
		 * backcolor.getSummary().toString(); if
		 * (nowSum.equals(BackColor.COLOR_BLUE)){
		 * ac.setBackColor(BackColor.COLOR_GREEN);
		 * backcolor.setSummary(BackColor.COLOR_GREEN); }else{
		 * ac.setBackColor(BackColor.COLOR_BLUE);
		 * backcolor.setSummary(BackColor.COLOR_BLUE); } return true; } });
		 */

		// 版本更新
		update = (Preference) findPreference("update");
		update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				// UpdateManager.getUpdateManager().checkAppUpdate(Setting.this,
				// true);

				UIHelper.ToastMessage(_context, "已经是最新版本!");

				return true;
			}
		});

		// 关于我们
		about = (Preference) findPreference("about");
		about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				// UIHelper.showAbout(ActivitySetting.this);

				UIHelper.ToastMessage(_context, "安心绿网");

				return true;
			}
		});
	}

}
