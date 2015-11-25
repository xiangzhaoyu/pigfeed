package com.surekam.pigfeed.ui.activity;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarLineChartBase.BorderPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.api.HttpExecuteJson;
import com.surekam.pigfeed.api.ServiceHelper;
import com.surekam.pigfeed.api.HttpExecuteJson.httpReturnJson;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.AgencyVo;
import com.surekam.pigfeed.bean.AreaVo;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.bean.FeedVo;
import com.surekam.pigfeed.bean.SearchPriceVo;
import com.surekam.pigfeed.ui.adapter.AgencyListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/*
 * 供应商提供的饲料的价格趋势*/
public class ActivityPriceAgency extends Activity implements UncaughtExceptionHandler {

	private View customLiveIndexTitleView;
	private TextView txtTitle;
	private ImageView ivBack;
	private ScrollView sv;

	private TextView feedname;
	private TextView agencyname;

	// 图标的类型，这里借用areavo类来显示
	private Spinner showflag;
	private List<AreaVo> flags;
	private ArrayAdapter<AreaVo> adFlags;

	private com.github.mikephil.charting.charts.LineChart mChart;

	private FeedVo currentFeed;
	private AgencyVo currentAgency;
	// 平均价格
	private List<SearchPriceVo> listPrices;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_agency_price_main);
		try {
			currentFeed = (FeedVo) getIntent().getExtras().get("feedvo");
			currentAgency=(AgencyVo)getIntent().getExtras().get("agency");
		} catch (Exception e) {
			e.printStackTrace();
		}
		initalTitle();
		initialView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 主动释放内存
		try {
			customLiveIndexTitleView = null;
			txtTitle = null;
			ivBack = null;

			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void initalTitle() {
		customLiveIndexTitleView = findViewById(R.id.title_formulaquery);
		txtTitle = (TextView) customLiveIndexTitleView
				.findViewById(R.id.title_text_nav);
		txtTitle.setText("供应商价格趋势");

		ivBack = (ImageView) findViewById(R.id.title_back);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();

			}

		});

	}

	private void initialView() {
		feedname = (TextView) findViewById(R.id.tv_feed_name);
		agencyname=(TextView)findViewById(R.id.tv_agency_name);
		showflag = (Spinner) findViewById(R.id.spinner_chart_flag);
		flags = new ArrayList<AreaVo>();
		AreaVo av1 = new AreaVo();
		av1.id = 0l;
		av1.name = "年趋势";
		flags.add(av1);
		AreaVo av2 = new AreaVo();
		av2.id = 1l;
		av2.name = "月趋势";
		flags.add(av2);
		AreaVo av3 = new AreaVo();
		av3.id = 2l;
		av3.name = "日趋势";
		flags.add(av3);
		adFlags = new ArrayAdapter<AreaVo>(this,
				android.R.layout.simple_spinner_item, flags);
		showflag.setAdapter(adFlags);
		showflag.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				try {
					AreaVo av = (AreaVo) showflag.getItemAtPosition(position);
					HttpExecuteJson http = new HttpExecuteJson(
							ActivityPriceAgency.this, mFeedPriceListener);
					Map<String, Object> rps = new HashMap<String, Object>();
					rps.put("method", "getFeedPrice");
					rps.put("feedId", currentFeed.id);
					rps.put("agencyId", currentAgency.id);
					rps.put("flag", av.id);
					http.get(new ServiceHelper().GETFORMULATYPE, rps);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		
		
		sv = (ScrollView) findViewById(R.id.scroll_feed_nutrition);

		listPrices = new ArrayList<SearchPriceVo>();

		mChart = (LineChart) findViewById(R.id.mpchart_feed_average_price);
		// mChart.setOnChartValueSelectedListener(this);

		// if enabled, the chart will always start at zero on the y-axis
		mChart.setStartAtZero(true);

		// enable the drawing of values into the chart
		mChart.setDrawYValues(true);

		mChart.setDrawBorder(true);
		mChart.setBorderPositions(new BorderPosition[] { BorderPosition.BOTTOM });

		// no description text
		mChart.setDescription("饲料平均价格走势");

		// invert the y-axis
		mChart.setInvertYAxisEnabled(true);

		// enable value highlighting
		mChart.setHighlightEnabled(true);

		// enable touch gestures
		mChart.setTouchEnabled(true);

		// enable scaling and dragging
		mChart.setDragEnabled(true);
		mChart.setScaleEnabled(true);

		// if disabled, scaling can be done on x- and y-axis separately
		mChart.setPinchZoom(true);

		// enable/disable highlight indicators (the lines that indicate the
		// highlighted Entry)
		mChart.setHighlightIndicatorEnabled(false);

		// add data
		// setData(25, 50);
		// setData1();

		try {
			feedname.setText(currentFeed.name);
			agencyname.setText(currentAgency.name);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void initialMpChart(){
		mChart = (LineChart) findViewById(R.id.mpchart_feed_average_price);
		// mChart.setOnChartValueSelectedListener(this);

		// if enabled, the chart will always start at zero on the y-axis
		mChart.setStartAtZero(true);

		// enable the drawing of values into the chart
		mChart.setDrawYValues(true);

		mChart.setDrawBorder(true);
		mChart.setBorderPositions(new BorderPosition[] { BorderPosition.BOTTOM });

		// no description text
		mChart.setDescription("饲料平均价格走势");

		// invert the y-axis
		mChart.setInvertYAxisEnabled(true);

		// enable value highlighting
		mChart.setHighlightEnabled(true);

		// enable touch gestures
		mChart.setTouchEnabled(true);

		// enable scaling and dragging
		mChart.setDragEnabled(true);
		mChart.setScaleEnabled(true);

		// if disabled, scaling can be done on x- and y-axis separately
		mChart.setPinchZoom(true);

		// enable/disable highlight indicators (the lines that indicate the
		// highlighted Entry)
		mChart.setHighlightIndicatorEnabled(false);

	}

	private httpReturnJson mFeedPriceListener = new httpReturnJson() {

		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			EntityDataPageVo edv = null;
			try {
//				initialMpChart();
				edv = new Gson().fromJson(result,
						new TypeToken<EntityDataPageVo>() {
						}.getType());
				if ((edv != null)
						&& (edv.getErrorCode().equals(edv.ERROR_CODE_SUCCESS))) {
					
					List<SearchPriceVo> temps1 = (List<SearchPriceVo>) edv.data;
					// List<String> temps=new ArrayList<String>();
					listPrices.clear();
					try{
						for (Object f : temps1) {
							try {
								String temp = new Gson().toJson(f);// dropBlank(f.toString())
								SearchPriceVo ft = new Gson().fromJson(temp,
										new TypeToken<SearchPriceVo>() {
										}.getType());
								if (ft != null && ft.agencyName != null) {
									listPrices.add(ft);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}	
					}catch(Exception e){e.printStackTrace();}
//					if(listPrices.size()>0){

						ArrayList<String> xVals = new ArrayList<String>();
						for (int i = 0; i < listPrices.size(); i++) {
							xVals.add(listPrices.get(i).ymd);
						}

						ArrayList<Entry> yVals = new ArrayList<Entry>();

						for (int i = 0; i < listPrices.size(); i++) {
							yVals.add(new Entry(listPrices.get(i).systemUnitPrice,
									i));
						}

						// create a dataset and give it a type
						String des = "";
						try {
							des = "饲料平均价格，单位：" + listPrices.get(0).systemUnitName;
						} catch (Exception e) {
							e.printStackTrace();
						}

						LineDataSet set1 = new LineDataSet(yVals, des);

						set1.setLineWidth(1.5f);
						set1.setCircleSize(4f);

						// create a data object with the datasets
						LineData data = new LineData(xVals, set1);

						// set data
						mChart.setData(data);

						mChart.invalidate();

						// 数据加载完成改变一下scrollview的显示位置
						sv.scrollTo(0, 0);

//					}
				}else{
					mChart.setData(null);
					mChart.invalidate();
					
				}
				if(listPrices.size()==0){
					UIHelper.ToastMessage(getApplicationContext(), "没有趋势数据");
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


	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		try{
			UIHelper.ToastMessage(getApplicationContext(), "当前程序使用环境不正常！");
		}catch(Exception e){e.printStackTrace();}
	}

	
}
