package com.surekam.pigfeed.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.surekam.pigfeed.R;

public class MyProgressDialog {
	Dialog _loadingDialog;
	Context _context;
	OnCancelListener _cancelListener;
	TextView tipTextView;
	View view;

	public MyProgressDialog(Context context) {
		// TODO Auto-generated constructor stub
		_context = context;

		view = LayoutInflater.from(context).inflate(R.layout.view_dialog, null);
		tipTextView = (TextView) view.findViewById(R.id.progress_text);

		_loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		_loadingDialog.setContentView(view);

	}

	public boolean isShowing() {
		return _loadingDialog.isShowing();
	}

	/**
	 * 
	 * 显示一个等待框
	 * 
	 * 
	 * 
	 * @param context上下文环境
	 * 
	 * @param isCancel是否能用返回取消
	 */

	public void show(String text, boolean isCancel,
			OnCancelListener cancelListener) {
		_cancelListener = cancelListener;
		creatDialog(_context, text, isCancel);
	}

	public void show(boolean isCancel, OnCancelListener cancelListener) {
		_cancelListener = cancelListener;
		creatDialog(_context, "", isCancel);
	}

	public void show(boolean isCancel) {
		creatDialog(_context, "", isCancel);
	}

	/**
	 * 
	 * 显示一个等待框
	 * 
	 * 
	 * 
	 * @param context上下文环境
	 * 
	 * @param msg等待框的文字
	 * 
	 * @param isCancel是否能用返回取消
	 * 
	 * @param isRighttrue文字在右边false在下面
	 */

	public void show(String msg, boolean isCancel) {
		if (_loadingDialog == null) {
			creatDialog(_context, msg, isCancel);
		} else {
			if (msg != null && !"".equals(msg)) {
				// 设置加载信息,否则加载默认值
				tipTextView.setText(msg);
			}

			_loadingDialog.show();
		}

	}

	public void dimiss() {
		if (_loadingDialog != null) {
			_loadingDialog.dismiss();
		}
	}

	private void creatDialog(Context context, String msg, boolean isCancel) {

		if (msg != null && !"".equals(msg)) {
			// 设置加载信息,否则加载默认值
			tipTextView.setText(msg);
		}

		_loadingDialog.setCanceledOnTouchOutside(false);
		_loadingDialog.setCancelable(isCancel);// 是否可以用返回键取消

		if (_cancelListener != null) {
			_loadingDialog.setOnCancelListener(_cancelListener);
		}

		if (!_loadingDialog.isShowing()) {
			_loadingDialog.show();
		}

	}

}
