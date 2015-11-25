package com.surekam.pigfeed.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.lidroid.xutils.util.LogUtils;
import com.surekam.pigfeed.tools.ACache;

public class AppContext extends Application {
	private static AppContext instance;
	public static boolean debug = true;

	public int _screenWidth = 480;
	public int _screenHeight = 800;

	public ACache _aCache;

	public int _userId;

	SharedPreferences preferences;
	public String _userName, _userPsd;
	public Boolean _autoLogin;

	public Boolean _isLogin = false;

	public static AppContext getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		instance = this;
		_aCache = ACache.get(this);
		getLoginInfo();

		if (!debug) {
			// 注册App异常崩溃处理器
			Thread.setDefaultUncaughtExceptionHandler(AppException
					.getAppExceptionHandler());
		}

		LogUtils.d("wyouflf");

		super.onCreate();
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	public String getVersion() {
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		String version = "";
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			version = "";
		}

		return version;
	}

	public void getLoginInfo() {
		preferences = getSharedPreferences("login", MODE_PRIVATE);

		// 密码(未加密)
		_userName = preferences.getString(AppConfig.USERNAME, "");
		_userPsd = preferences.getString(AppConfig.PASSWORD, "");

		// 用户账户
		_autoLogin = preferences.getBoolean(AppConfig.AUTOLOGIN, false);
	}
	
	public void saveLoginInfo(String userName,String psw,boolean autoLogin){
		Editor editor = preferences.edit();
		editor.putString(AppConfig.USERNAME, userName + "");

		editor.putBoolean(AppConfig.AUTOLOGIN, autoLogin);

		if (autoLogin) {
			editor.putString(AppConfig.PASSWORD, psw + "");
		} else {
			editor.putString(AppConfig.PASSWORD, "");
		}

		editor.commit();
	}

}
