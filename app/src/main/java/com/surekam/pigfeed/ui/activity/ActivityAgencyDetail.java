package com.surekam.pigfeed.ui.activity;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.http.conn.ConnectTimeoutException;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.AgencyVo;
import com.surekam.pigfeed.bean.NutritionVo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class ActivityAgencyDetail extends Activity implements UncaughtExceptionHandler {

	private View customLiveIndexTitleView;
	private TextView txtTitle;
	private ImageView ivBack;
	private ScrollView sv;
	
	private AgencyVo cAgency;//当前展示经销商
	
	private TextView aName;
	private TextView aType;
	private TextView aArea;
	private TextView aPhone;
	private TextView aAddress;
	private TextView aContactMan;
	private TextView aContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_agency_detail_main);

		try{
			cAgency=(AgencyVo)getIntent().getExtras().get("agency");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		initialTitle();
		initialView();
		initialData();
	}
	
	private void initialTitle(){
		customLiveIndexTitleView = findViewById(R.id.title_nutritiondetail);
		txtTitle = (TextView) customLiveIndexTitleView
				.findViewById(R.id.title_text_nav);
		txtTitle.setText("经销商明细");

		ivBack = (ImageView) findViewById(R.id.title_back);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();

			}

		});
	}
	
	private void initialView(){
		aName=(TextView)findViewById(R.id.tv_agency_name);
		aType=(TextView)findViewById(R.id.tv_agency_type);
		aArea=(TextView)findViewById(R.id.tv_agency_area);
		aPhone=(TextView)findViewById(R.id.tv_agency_phone);
		aAddress=(TextView)findViewById(R.id.tv_agency_address);
		aContactMan=(TextView)findViewById(R.id.tv_agency_man);
		aContent=(TextView)findViewById(R.id.tv_agency_content);
		
		sv = (ScrollView) findViewById(R.id.scroll_nutrition_detail);
	}
	
	private void initialData(){
		if(cAgency!=null){
			try{
				aName.setText(cAgency.name);
				aType.setText(cAgency.agencyTypeName);
				aArea.setText(cAgency.areaName);
				aPhone.setText(cAgency.phone1);
				aAddress.setText(cAgency.address);
				aContactMan.setText(cAgency.contactMan);
				aContent.setText(cAgency.remark);
				
			}catch(Exception e){
				e.printStackTrace();
			}		
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		try{
			UIHelper.ToastMessage(getApplicationContext(), "当前程序使用环境不正常！");
		}catch(Exception e){e.printStackTrace();}
	}
	
}
