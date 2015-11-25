package com.surekam.pigfeed.ui.activity;

import java.lang.Thread.UncaughtExceptionHandler;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.NutritionVo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

/*
 * 营养素明细查看页面*/
public class ActivityNutritionDetail extends Activity implements UncaughtExceptionHandler {
	
	private View customLiveIndexTitleView;
	private TextView txtTitle;
	private ImageView ivBack;
	private ScrollView sv;
	
	private NutritionVo currentNutri;//当前展示的营养素
	
	private TextView nName;
	private TextView nContent;
	private TextView nUnit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_nutrition_detail_main);

		try{
			currentNutri=(NutritionVo)getIntent().getExtras().get("nutrition");
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
		txtTitle.setText("营养素明细");

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
		nName=(TextView)findViewById(R.id.tv_nutrition_name);
		nContent=(TextView)findViewById(R.id.tv_nutrition_content);
		nUnit=(TextView)findViewById(R.id.tv_nutrition_unit);
		sv = (ScrollView) findViewById(R.id.scroll_nutrition_detail);
	}
	
	private void initialData(){
		if(currentNutri!=null){
			nName.setText(currentNutri.name);
			nContent.setText(currentNutri.remark);
			nUnit.setText(currentNutri.systemUnitName);
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
