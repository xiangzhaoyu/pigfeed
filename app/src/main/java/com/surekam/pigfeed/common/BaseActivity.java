package com.surekam.pigfeed.common;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.surekam.pigfeed.app.AppManager;
import com.surekam.pigfeed.event.BusProvider;

public class BaseActivity extends Activity {
	public Context _context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// 事件总线注册
		BusProvider.getInstance().register(this);

		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);

		if (_context == null) {
			_context = this;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);

		// 事件总线移除
		BusProvider.getInstance().unregister(this);

		super.onDestroy();

	}
}
