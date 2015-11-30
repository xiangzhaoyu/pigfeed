package com.surekam.pigfeed.ui.activity;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarLineChartBase.BorderPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
import com.surekam.pigfeed.bean.FeedFormulaRecommandVo;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.bean.NutritionVo;
import com.surekam.pigfeed.bean.SearchPriceVo;
import com.surekam.pigfeed.tools.JsonUtil;
import com.surekam.pigfeed.ui.adapter.AgencyListAdapter;
import com.surekam.pigfeed.ui.adapter.FeedListAdapter;
import com.surekam.pigfeed.ui.adapter.NutritionListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * 饲料的明细信息*/
public class ActivityFeedDetail extends Activity implements UncaughtExceptionHandler {

	private View customLiveIndexTitleView;
	private TextView txtTitle;
	private ImageView ivBack;
	private ScrollView sv;

	// 当前的饲料的信息
	private FeedFormulaRecommandVo currentFormulaRecommand;

	private TextView fName;
	private TextView fContent;

	// 营养素列表
	private ListView nuListView;
	private List<NutritionVo> listNus;
	private NutritionListAdapter nuAdapter;

	// 经销商列表
	private ListView agencyListView;
	private List<AgencyVo> listAgencys;
	private AgencyListAdapter agencyAdapter;

	// 饲料平均价格走势图
	private LinearLayout pdViewFeedPrice;
	private List<SearchPriceVo> listPrices;
	private int pdViewFlag = 1;// 表示按年0，月1，日2
	
	private com.github.mikephil.charting.charts.LineChart mChart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_feed_detail_main);
		try {
			currentFormulaRecommand = (FeedFormulaRecommandVo) getIntent()
					.getExtras().get("formularecommand");
		} catch (Exception e) {
			e.printStackTrace();
		}

		initalTitle();
		initialView();
		initalData();
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
		txtTitle.setText("饲料明细");

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
		fName = (TextView) findViewById(R.id.tv_feed_name);
		fContent = (TextView) findViewById(R.id.tv_feed_content);

		nuListView = (ListView) findViewById(R.id.listview_feed_nutrition);
		nuListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				try{
					NutritionVo nv=(NutritionVo)nuListView.getItemAtPosition(position);
					if(nv!=null&&nv.id!=null){
						Intent intent=new Intent(ActivityFeedDetail.this,ActivityNutritionDetail.class);
						intent.putExtra("nutrition", nv);
						startActivity(intent);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}});

		agencyListView = (ListView) findViewById(R.id.listview_feed_agency);
		agencyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				try {
					AgencyVo nv=(AgencyVo)agencyListView.getItemAtPosition(position);
					if(nv!=null&&nv.id!=null){
						Intent intent=new Intent(ActivityFeedDetail.this,ActivityAgencyDetail.class);
						intent.putExtra("agency", nv);
						startActivity(intent);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		initialMpChart();

		sv = (ScrollView) findViewById(R.id.scroll_feed_nutrition);
		pdViewFeedPrice = (LinearLayout) findViewById(R.id.pdview_feed_price);
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
	
	private void initalData() {
		try {
			listNus = new ArrayList<NutritionVo>();
			listAgencys = new ArrayList<AgencyVo>();
			listPrices = new ArrayList<SearchPriceVo>();
			if (currentFormulaRecommand != null) {
				fName.setText(currentFormulaRecommand.feedName);
				fContent.setText(currentFormulaRecommand.remark);

				try {
					HttpExecuteJson http = new HttpExecuteJson(this,
							mFeedAgencyListener);
					Map<String, Object> rps = new HashMap<String, Object>();
					rps.put("method", "getFeedAgency");
					rps.put("feedId", currentFormulaRecommand.feedId);
					http.get(new ServiceHelper().GETFORMULATYPE, rps);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					HttpExecuteJson http = new HttpExecuteJson(this,
							mFeedNutritionListener);
					Map<String, Object> rps = new HashMap<String, Object>();
					rps.put("method", "getFeedNutrition");
					rps.put("feedId", currentFormulaRecommand.feedId);
					http.get(new ServiceHelper().GETFORMULATYPE, rps);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					HttpExecuteJson http = new HttpExecuteJson(this,
							mFeedPriceListener);
					Map<String, Object> rps = new HashMap<String, Object>();
					rps.put("method", "getFeedPrice");
					rps.put("feedId", currentFormulaRecommand.feedId);
					// rps.put("agencyId", currentFormulaRecommand.feedId);
					rps.put("flag", pdViewFlag);
					http.get(new ServiceHelper().GETFORMULATYPE, rps);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private httpReturnJson mFeedNutritionListener = new httpReturnJson() {
		@Override
		public void onFailure(int error, String msg) {
			UIHelper.ToastMessage(ActivityFeedDetail.this, "获取饲料营养素失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFeedDetail.this, "获取饲料营养素退出");
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

					List<NutritionVo> temps1 = new ArrayList<NutritionVo>();//(ArrayList<NutritionVo>) edv.data;
					// List<String> temps=new ArrayList<String>();
					try{
					temps1 = JsonUtil.fromJsonArray(
							JsonUtil.toJson(edv.data),
							NutritionVo.class);}catch (Exception e){}

					if(temps1!=null&&temps1.size()>0){
						listNus.addAll(temps1);
					}else{
						NutritionVo nv=new NutritionVo();
						nv.id=null;
						nv.name="无";
						nv.systemUnitNum="无";
						nv.systemUnitName="无";
						listNus.add(nv);
					}
					nuAdapter = new NutritionListAdapter(listNus,
							getApplicationContext(),
							R.layout.fragment_fromula_nutrition_list_item);
					nuListView.setAdapter(nuAdapter);
					fixListViewHeight(nuListView);
					// 数据加载完成改变一下scrollview的显示位置
					sv.scrollTo(0, 0);
				}else{
					UIHelper.ToastMessage(ActivityFeedDetail.this, "获取营养素失败" + edv.getErrorMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				UIHelper.ToastMessage(ActivityFeedDetail.this, "获取营养素失败，请联系管理员：" + e.getMessage());
			}
			// UIHelper.ToastMessage(_context,"总条数=" + result + "");
		}
	};

	private httpReturnJson mFeedAgencyListener = new httpReturnJson() {
		@Override
		public void onFailure(int error, String msg) {
			UIHelper.ToastMessage(ActivityFeedDetail.this, "获取饲料供应商失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFeedDetail.this, "获取饲料供应商退出");
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
					List<AgencyVo> temps1 = new ArrayList<AgencyVo>();//(List<AgencyVo>) edv.data;
					// List<String> temps=new ArrayList<String>();
					try{
					temps1 = JsonUtil.fromJsonArray(
							JsonUtil.toJson(edv.data),
							AgencyVo.class);
					}catch (Exception e){}

					if(temps1!=null&&temps1.size()>0){
						listAgencys.addAll(temps1);
					}else{
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
				}else{
					UIHelper.ToastMessage(ActivityFeedDetail.this, "获取饲料供应商失败" + edv.getErrorMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				UIHelper.ToastMessage(ActivityFeedDetail.this, "获取饲料供应商失败,请联系管理员：" + e.getMessage());
			}
			// UIHelper.ToastMessage(_context,"总条数=" + result + "");
		}
	};

	private httpReturnJson mFeedPriceListener = new httpReturnJson() {

		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			EntityDataPageVo edv = null;
			try {
				edv = new Gson().fromJson(result,
						new TypeToken<EntityDataPageVo>() {
						}.getType());
				if ((edv != null)
						&& (edv.getErrorCode().equals(edv.ERROR_CODE_SUCCESS))) {
					try{
						List<SearchPriceVo> temps1 = new ArrayList<SearchPriceVo>();//(List<SearchPriceVo>) edv.data;
						temps1 = JsonUtil.fromJsonArray(
								JsonUtil.toJson(edv.data),
								SearchPriceVo.class);
						if(temps1!=null&&temps1.size()>0){
							listPrices.addAll(temps1);
						}

					}catch(Exception e){
						e.printStackTrace();
					}
					
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
					
					/*
					int num = listPrices.size();
					String[] HorLabels = new String[num];
					double maxNum = 0;
					double minNum = 0;
					GraphViewData[] data = new GraphViewData[num];
					for (int i = 0; i < num; i++) {
						HorLabels[i] = listPrices.get(i).ymd;
						// data[i] = new GraphViewData(i+1, i+1);
						data[i] = new GraphViewData(i + 1,
								listPrices.get(i).systemUnitPrice);
						try {
							double temp = listPrices.get(i).systemUnitPrice;
							// double temp=i+1;
							if (temp > maxNum) {
								maxNum = temp;
							}
							if (minNum > temp) {
								minNum = temp;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					GraphView graphView;
					// graph with dynamically genereated horizontal and vertical
					// labels
					graphView = new LineGraphView(ActivityFeedDetail.this, "");
					((LineGraphView) graphView).setAutoHorLabels(HorLabels);
					((LineGraphView) graphView).setMyHorLabels(HorLabels);
					((LineGraphView) graphView).setBackgroundColor(0x4fffffff);
					((LineGraphView) graphView).setDrawBackground(true);
					((LineGraphView) graphView).setDrawText(true);
					((LineGraphView) graphView).setDrawTextDiff(10);
					// add data
					graphView.addSeries(new GraphViewSeries("价格趋势",
							new GraphViewSeriesStyle(0xffffffff, 3), data));
					// set view port, start=2, size=10
					graphView.getGraphViewStyle().setNumHorizontalLabels(5);
					graphView.setViewPort(1, 4);
					graphView.setScrollable(true);
					// graphView.setScalable(true);
					// set manual Y axis bounds
					maxNum = maxNum * 1.5;
					graphView.setManualYAxisBounds(maxNum, minNum);
					pdViewFeedPrice.addView(graphView);
					// 数据加载完成改变一下scrollview的显示位置
					 * 
					 * 20150115使用mpchart代替pdview
					 */
					sv.scrollTo(0, 0);
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
			UIHelper.ToastMessage(ActivityFeedDetail.this, "获取价格趋势失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFeedDetail.this, "获取价格趋势退出");
		}
	};

	/*
	 * 去掉source中的空格*
	 */
	public String dropBlank(String source) {
		String result = "";
		try {
			if (source != null) {
				// Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				// Matcher m = p.matcher(source);
				// result=m.replaceAll("");
				result = source.trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
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
