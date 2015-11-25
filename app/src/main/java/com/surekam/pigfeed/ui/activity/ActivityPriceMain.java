package com.surekam.pigfeed.ui.activity;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarLineChartBase.BorderPosition;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pdstudio.graphview.GraphView;
import com.pdstudio.graphview.GraphViewSeries;
import com.pdstudio.graphview.LineGraphView;
import com.pdstudio.graphview.GraphView.GraphViewData;
import com.pdstudio.graphview.GraphViewSeries.GraphViewSeriesStyle;
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

/*
 * 价格趋势页面*/
public class ActivityPriceMain extends Activity implements UncaughtExceptionHandler {

	private View customLiveIndexTitleView;
	private TextView txtTitle;
	private ImageView ivBack;
	private ScrollView sv;

	private TextView feedname;

	// 图标的类型，这里借用areavo类来显示
	private Spinner showflag;
	private List<AreaVo> flags;
	private ArrayAdapter<AreaVo> adFlags;

	private com.github.mikephil.charting.charts.LineChart mChart;

	private FeedVo currentFeed;
	// 平均价格
	private List<SearchPriceVo> listPrices;
	
	// 经销商列表
	private ListView agencyListView;
	private List<AgencyVo> listAgencys;
	private AgencyListAdapter agencyAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_feed_price_main);
		try {
			currentFeed = (FeedVo) getIntent().getExtras().get("feedvo");
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
		txtTitle.setText("价格趋势");

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
							ActivityPriceMain.this, mFeedPriceListener);
					Map<String, Object> rps = new HashMap<String, Object>();
					rps.put("method", "getFeedPrice");
					rps.put("feedId", currentFeed.id);
					// rps.put("agencyId", currentFormulaRecommand.feedId);
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
		
		listAgencys = new ArrayList<AgencyVo>();
		agencyListView = (ListView) findViewById(R.id.listview_feed_agency);
		agencyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				try {
					AgencyVo nv=(AgencyVo)agencyListView.getItemAtPosition(position);
					if(nv!=null&&nv.id!=null){
						//Intent intent=new Intent(ActivityPriceMain.this,ActivityAgencyDetail.class);
						Intent intent=new Intent(ActivityPriceMain.this,ActivityPriceAgency.class);
						intent.putExtra("agency", nv);
						intent.putExtra("feedvo", currentFeed);
						startActivity(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		try {
			HttpExecuteJson http = new HttpExecuteJson(this,
					mFeedAgencyListener);
			Map<String, Object> rps = new HashMap<String, Object>();
			rps.put("method", "getFeedAgency");
			rps.put("feedId", currentFeed.id);
			http.get(new ServiceHelper().GETFORMULATYPE, rps);
		} catch (Exception e) {
			e.printStackTrace();
		}


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
		mChart.setNoDataText("当前没有价格走势");

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
		mChart.setNoDataText("当前没有价格走势");

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

	private void setData(int count, float range) {

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			xVals.add((i % 30) + "/" + (i % 12) + "/14");
		}

		ArrayList<Entry> yVals = new ArrayList<Entry>();

		for (int i = 0; i < count; i++) {
			float mult = (range + 1);
			float val = (float) (Math.random() * mult) + 3;// + (float)
															// ((mult *
															// 0.1) / 10);
			yVals.add(new Entry(val, i));
		}

		// create a dataset and give it a type
		LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");

		set1.setLineWidth(1.5f);
		set1.setCircleSize(4f);

		// create a data object with the datasets
		LineData data = new LineData(xVals, set1);

		// set data
		mChart.setData(data);
	}

	private void setData1() {
		try {
			feedname.setText(currentFeed.name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			HttpExecuteJson http = new HttpExecuteJson(this, mFeedPriceListener);
			Map<String, Object> rps = new HashMap<String, Object>();
			rps.put("method", "getFeedPrice");
			rps.put("feedId", currentFeed.id);
			// rps.put("agencyId", currentFormulaRecommand.feedId);
			rps.put("flag", 0);
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
//				initialMpChart();
				edv = new Gson().fromJson(result,
						new TypeToken<EntityDataPageVo>() {
						}.getType());
				if ((edv != null)
						&& (edv.getErrorCode().equals(edv.ERROR_CODE_SUCCESS))) {
					listPrices.clear();
					try{
						List<SearchPriceVo> temps1 = (List<SearchPriceVo>) edv.data;
						// List<String> temps=new ArrayList<String>();
						
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
						UIHelper.ToastMessage(getApplicationContext(), "没有趋势数据");
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

	private httpReturnJson mFeedAgencyListener = new httpReturnJson() {
		@Override
		public void onFailure(int error, String msg) {
			UIHelper.ToastMessage(ActivityPriceMain.this, "获取饲料供应商失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityPriceMain.this, "获取饲料供应商退出");
		}

		// 获取到饲料配方类型成功
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			EntityDataPageVo edv = null;
			try {
				edv = new Gson().fromJson(result,
						new TypeToken<EntityDataPageVo>() {
						}.getType());
				if ((edv != null)
						&& (edv.getErrorCode().equals(edv.ERROR_CODE_SUCCESS))) {
					List<AgencyVo> temps1 = (List<AgencyVo>) edv.data;
					// List<String> temps=new ArrayList<String>();
					if(temps1!=null){
						for (Object f : temps1) {
							try {
								String temp = new Gson().toJson(f);// dropBlank(f.toString())
								AgencyVo ft = new Gson().fromJson(temp,
										new TypeToken<AgencyVo>() {
										}.getType());
								// AgencyVo ft = new Gson().fromJson(
								// "{\"id\":2,\"code\":\"NL\",\"name\":\"能量\",\"systemUnitId\":2,\"systemUnitName\":\"系统单位名称2\",\"systemUnitNum\":\"43\",\"systemUnitMax\":\"333\",\"systemUnitMin\":\"8\",\"remark\":\"是由饲料中的碳水化合物、脂肪和蛋白质，经过猪体内氧化而产生的。动物体为维持生命活动和生产过程中都要消耗能量。饲粮中的含能物质主要为玉米和油脂。\",\"sysFlag\":\"1\",\"creatorId\":-1,\"creatorName\":\"admin\",\"createTime\":\"20150105000000\",\"lastModifierId\":-1,\"lastModifiedTime\":\"20150105000000\"}",
								// new TypeToken<AgencyVo>() {
								// }.getType());
								if (ft != null && ft.name != null) {
									listAgencys.add(ft);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					if(listAgencys!=null&&listAgencys.size()==0){
						AgencyVo av=new AgencyVo();
						av.id=null;
						av.name="无";
						av.systemUnitPrice="无";
						av.phone1="无";
						listAgencys.add(av);
					}
					agencyAdapter = new AgencyListAdapter(listAgencys,
							getApplicationContext(),
							R.layout.fragment_feed_agency_list_item);
					agencyListView.setAdapter(agencyAdapter);
					fixListViewHeight(agencyListView);
					// 数据加载完成改变一下scrollview的显示位置
					sv.scrollTo(0, 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// UIHelper.ToastMessage(_context,"总条数=" + result + "");
		}
	};
	
	public void fixListViewHeight(ListView listView) {   
        // 如果没有设置数据适配器，则ListView没有子项，返回。  
        ListAdapter listAdapter = listView.getAdapter();  
        int totalHeight = 0;   
        if (listAdapter == null) {   
            return;   
        }   
        for (int index = 0, len = listAdapter.getCount(); index < len; index++) {     
            View listViewItem = listAdapter.getView(index , null, listView);  
            // 计算子项View 的宽高   
            //listViewItem.measure(0, 0);    
            listViewItem.measure(MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().widthPixels, MeasureSpec.EXACTLY), 0);
            // 计算所有子项的高度和
            int height=0;
            if(listViewItem.getHeight()>listViewItem.getMeasuredHeight())
            	height=listViewItem.getHeight();
            else
            	height=listViewItem.getMeasuredHeight();
            
            totalHeight += height;    
        }   
   
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        // listView.getDividerHeight()获取子项间分隔符的高度   
        // params.height设置ListView完全显示需要的高度    
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
        listView.setLayoutParams(params);   
    }

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		try{
			UIHelper.ToastMessage(getApplicationContext(), "当前程序使用环境不正常！");
		}catch(Exception e){e.printStackTrace();}
	}

	
}
