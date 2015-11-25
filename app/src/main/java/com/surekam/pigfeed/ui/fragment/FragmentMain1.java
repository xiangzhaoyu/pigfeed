package com.surekam.pigfeed.ui.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.ImageInfo;
import com.surekam.pigfeed.common.BaseFragment;
import com.surekam.pigfeed.ui.activity.ActivityMain;
import com.surekam.pigfeed.ui.adapter.MyPagerAdapter;

/*
 * 猪饲料第一个主界面*/
public class FragmentMain1 extends BaseFragment implements OnClickListener {

	ArrayList<ImageInfo> data; // 菜单数据
    private static TextView mynum; // 页码
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		_view = inflater.inflate(R.layout.fragment_main1, null);
		_context = getActivity();
		initialView();
		return _view;
	}
	
	private void initialView(){
		mynum = (TextView) _view.findViewById(R.id.mynum);
        // 初始化数据
        initData();
        ViewPager vpager = (ViewPager) _view.findViewById(R.id.vPager);
        vpager.setAdapter(new MyPagerAdapter((ActivityMain)_context, data));
        vpager.setPageMargin(50);
        vpager.setOnPageChangeListener(new OnPageChangeListener() {
 
            @Override
            public void onPageSelected(int arg0) {
                mynum.setText("" + (int) (arg0 + 1));
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
 
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
 
            }
        });
	}
	
	private void initData() {
        data = new ArrayList<ImageInfo>();
        mynum.setText("1");
        data.add(new ImageInfo("配方检索", R.drawable.icon1, R.drawable.icon_bg01));
        data.add(new ImageInfo("饲料检索", R.drawable.icon2, R.drawable.icon_bg01));
        data.add(new ImageInfo("价格趋势", R.drawable.icon3, R.drawable.icon_bg01));
        data.add(new ImageInfo("联系专家", R.drawable.icon4, R.drawable.icon_bg02));
        
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//Toast.makeText(_context.getApplicationContext(), v.getId()+"--"+v.toString(), 3000);
	}

}
