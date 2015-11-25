package com.surekam.pigfeed.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.app.AppContext;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.common.BaseActivity;
import com.surekam.pigfeed.tools.StringUtils;

public class ActivityLogin extends BaseActivity implements OnClickListener {
	String mUserName, mPassword;
	boolean mAutoLogin;

	EditText metAccount, metPassword;
	CheckBox mcbAutoLogin;
	Button mbtLogin;

	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		setContentView(R.layout.activity_login);

		preferences = getSharedPreferences("login", MODE_PRIVATE);

		initView();
		initData();

		super.onCreate(savedInstanceState);

	}

	private void initTitleBar() {

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (metAccount != null)
			initData();
		super.onResume();
	}

	private void initView() {

		mbtLogin = (Button) findViewById(R.id.login);
		metAccount = (EditText) findViewById(R.id.account);
		metPassword = (EditText) findViewById(R.id.password);

		findViewById(R.id.login).setOnClickListener(this);
		mbtLogin.setOnClickListener(this);
		// findViewById(R.id.regsiter).setOnClickListener(this);

		mcbAutoLogin = (CheckBox) findViewById(R.id.autologin);
		mcbAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				mAutoLogin = isChecked;

			}
		});

		((CheckBox) findViewById(R.id.login_switchBtn))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							metPassword
									.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
							metPassword.setSelection(metPassword.getText()
									.length());
						} else {
							metPassword.setInputType(InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							metPassword.setSelection(metPassword.getText()
									.length());
						}

					}
				});

	}

	private void initData() {

		// 密码(未加密)
		mUserName = AppContext.getInstance()._userName;
		mPassword = AppContext.getInstance()._userPsd;
		boolean isLogin = AppContext.getInstance()._autoLogin;

		metAccount.setText(StringUtils.isEmpty(mUserName) ? "" : mUserName);
		metPassword.setText(StringUtils.isEmpty(mPassword) ? "" : mPassword);

		mcbAutoLogin.setChecked(isLogin);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login:
			login();
			break;

		// case R.id.regsiter:
		// // UIHelper.StartUserRegisterActivity(ActivityLogin.this);
		// break;
		}

	}

	private void login() {
		mUserName = metAccount.getText().toString();
		mPassword = metPassword.getText().toString();
		Boolean autoLogin = mcbAutoLogin.isChecked();

		if (!checkValue())
			return;

		// md5加密
		/*
		 * byte[] pas = DigestUtils.md5(mPassword) ; mPassword = pas.toString();
		 */
		/*
		 * byte[] pas = mPassword.getBytes(); mPassword
		 * =DigestUtils.md5Hex(pas);
		 */

		// String url = URLs.LOGIN;
		//
		// Map<String, Object> params = new HashMap<String, Object>();
		// params.put("username", mUserName);
		// params.put("password", mPassword);
		// params.put("rememberMe", 1);
		//
		// mbtLogin.setText("正在登录...");
		// BeginAquery(url, params, true);
		AppContext.getInstance()._isLogin = true;

		AppContext.getInstance().saveLoginInfo(mUserName + "", mPassword + "",
				autoLogin);

		Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
		startActivity(intent);
		finish();
	}

	private boolean checkValue() {
		String msg = "";

		if (StringUtils.isEmpty(mUserName)) {
			msg = getString(R.string.login_check_account);
			// metAccount.setError("");
			metAccount.requestFocus();
			UIHelper.ToastMessage(getApplicationContext(), msg);
			return false;
		}

		if (StringUtils.isEmpty(mPassword)) {
			msg = getString(R.string.login_check_password);

			// metPassword.setError("");
			metPassword.requestFocus();
			UIHelper.ToastMessage(getApplicationContext(), msg);
			return false;
		}

		return true;
	}

}
