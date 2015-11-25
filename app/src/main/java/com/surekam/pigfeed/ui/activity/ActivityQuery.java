package com.surekam.pigfeed.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.app.AppManager;
import com.surekam.pigfeed.common.BaseFragmentActivity;
import com.surekam.pigfeed.ui.fragment.FragmentFormulaQuery;
import com.surekam.pigfeed.ui.fragment.FragmentMain1;
import com.surekam.pigfeed.ui.fragment.FragmentSelectCitys;
import com.surekam.pigfeed.widge.CustomDialog;

public class ActivityQuery extends BaseFragmentActivity {

	Fragment _nowFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		_context = this;

		getSupportFragmentManager().beginTransaction()
		.replace(R.id.main_frame, new FragmentFormulaQuery()).commit();

	}

	public void addFragment(Fragment fragment, String name) {

		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.push_right_in,
						R.anim.push_right_out).add(R.id.main_frame, fragment)
				.addToBackStack(name).commit();

		_nowFragment = fragment;
	}
	
}
