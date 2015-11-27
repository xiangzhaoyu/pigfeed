package com.surekam.pigfeed.ui.activity;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.api.HttpExecuteJson;
import com.surekam.pigfeed.api.ServiceHelper;
import com.surekam.pigfeed.api.HttpExecuteJson.httpReturnJson;
import com.surekam.pigfeed.app.AppManager;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.common.BaseFragmentActivity;
import com.surekam.pigfeed.tools.UpdateManager;
import com.surekam.pigfeed.ui.fragment.FragmentMain2;
import com.surekam.pigfeed.widge.CustomDialog;

public class ActivityMain extends BaseFragmentActivity {
	Fragment _nowFragment;
	
	private UpdateManager mUpdateManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		_context = this;

		getSupportFragmentManager().beginTransaction()
		.replace(R.id.main_frame, new FragmentMain2()).commit();

		//checkVersion();
	}

	public void addFragment(Fragment fragment, String name) {

		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.push_right_in,
						R.anim.push_right_out).add(R.id.main_frame, fragment)
				.addToBackStack(name).commit();

		_nowFragment = fragment;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
			return;
		} else {
			// UIHelper.exitSystem(ActivityMainSlide.this);
			exit();
			// super.onBackPressed();

		}

	}

	private void exit() {
		String appName = getString(R.string.app_name);

		final CustomDialog dialog = new CustomDialog(_context);
		dialog.SetDialog("退出", "是否退出" + appName + "?", new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AppManager.getAppManager().AppExit(_context);
			}
		});

		dialog.show();

	}
	
	/*
	 * 检查版本更新*/
	private void checkVersion() {
		// 去接口获取版本号
		try {
			HttpExecuteJson http = new HttpExecuteJson(
					ActivityMain.this, mFeedPriceListener);
			Map<String, Object> rps = new HashMap<String, Object>();
			rps.put("method", "getVersionCode");
			http.get(new ServiceHelper().GETFORMULATYPE, rps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private httpReturnJson mFeedPriceListener = new httpReturnJson() {

		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			EntityDataPageVo edv = null;
			try {
				edv = new Gson().fromJson(result,
						new TypeToken<EntityDataPageVo>() {
						}.getType());
				if ((edv != null)
						&& (edv.getErrorCode().equals(edv.ERROR_CODE_SUCCESS))) {
					int code=Integer.parseInt(edv.data.toString());
					mUpdateManager = new UpdateManager(
							ActivityMain.this);
					mUpdateManager.checkUpdateInfo(code);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onFailure(int error, String msg) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}
	};

	
}
