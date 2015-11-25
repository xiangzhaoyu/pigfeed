package com.surekam.pigfeed.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.surekam.pigfeed.event.BusProvider;

public class BaseFragment extends Fragment {
	public Context _context;
	public View _view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		BusProvider.getInstance().register(this);

		if (_context == null) {
			_context = getActivity();
		}

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// 事件总线移除
		BusProvider.getInstance().unregister(this);

		super.onDestroy();
	}
	

}
