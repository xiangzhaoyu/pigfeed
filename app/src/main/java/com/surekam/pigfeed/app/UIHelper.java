package com.surekam.pigfeed.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.ui.activity.ActivityLogin;
import com.surekam.pigfeed.ui.activity.ActivityMain;
import com.surekam.pigfeed.ui.activity.SplashActivity;
import com.surekam.pigfeed.widge.CustomToast;

public class UIHelper {

	public static void startMain(Context context) {
		Intent it = new Intent(context, SplashActivity.class);
		context.startActivity(it);
	}

	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		// Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
		CustomToast.showCustomToast(cont, msg, Toast.LENGTH_SHORT);
	}

	/*
	* 弹出时间长点的toast
	* */
	public static void ToastLongMessage(Context cont, String msg) {
		// Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
		CustomToast.showCustomToast(cont, msg, Toast.LENGTH_LONG);
	}

	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, Object msg) {
		// Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
		CustomToast.showCustomToast(cont, msg.toString(), Toast.LENGTH_SHORT);
	}

	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent i = new Intent(Intent.ACTION_SEND);
						i.setType("text/plain"); // 模拟器
						// i.setType("message/rfc822") ; //真机
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "liukai@surekam.com" });
						i.putExtra(Intent.EXTRA_SUBJECT, "错误报告");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "发送错误报告"));
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.show();
	}

	public static OnTouchListener mTouchListner = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return true;
		}
	};

	public static void loginOrLogout(Context context) {
		if (AppContext.getInstance()._isLogin) {
			AppContext.getInstance()._userPsd = "";
			AppContext.getInstance()._autoLogin = false;
			AppContext.getInstance()._isLogin = false;

			AppContext.getInstance().saveLoginInfo(
					AppContext.getInstance()._userName, "", false);
		} else {
			Intent intent = new Intent(context, ActivityLogin.class);
			context.startActivity(intent);
		}

	}

}
