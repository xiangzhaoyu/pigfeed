package com.surekam.pigfeed.ui.activity;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.api.HttpExecuteJson;
import com.surekam.pigfeed.api.ServiceHelper;
import com.surekam.pigfeed.api.HttpExecuteJson.httpReturnJson;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.AreaVo;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.bean.FeedFormulaRecommandVo;
import com.surekam.pigfeed.bean.FeedFormulaType;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.bean.NutritionVo;
import com.surekam.pigfeed.tools.JsonUtil;
import com.surekam.pigfeed.ui.adapter.FeedListAdapter;
import com.surekam.pigfeed.ui.adapter.NutritionListAdapter;
import com.surekam.pigfeed.ui.view.PullDownView;
import com.surekam.pigfeed.ui.view.PullDownView.OnPullDownListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * 配方的详细信息展示*/
public class ActivityFormulaDetail extends Activity implements UncaughtExceptionHandler {

	private View customLiveIndexTitleView;
	private TextView txtTitle;
	private ImageView ivBack;
	private ScrollView sv;

	// 当前展示的配方实体类
	private FeedFormulaVo ff;

	// 明细信息
	private TextView ffName;
	private TextView ffUseNum;
	private TextView ffContent;
	
	
	//营养素列表
	private ListView nuListView;
	private List<NutritionVo> listNus;
	private NutritionListAdapter nuAdapter;
	
	//饲料列表
	private ListView feedListView;
	private List<FeedFormulaRecommandVo> listFeeds;
	private FeedListAdapter feedAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_formula_detail_main);
		try {
			ff = (FeedFormulaVo) getIntent().getExtras().get("formula");
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
		txtTitle.setText("配方明细");

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
		ffName = (TextView) findViewById(R.id.tv_formula_name);
		ffUseNum = (TextView) findViewById(R.id.tv_formula_usenum);
		ffContent = (TextView) findViewById(R.id.tv_formula_content);
		
		nuListView = (ListView) findViewById(R.id.listview_formula_nutrition);
		nuListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				try{
					NutritionVo nv=(NutritionVo)nuListView.getItemAtPosition(position);
					if(nv!=null&&nv.id!=null){
						Intent intent=new Intent(ActivityFormulaDetail.this,ActivityNutritionDetail.class);
						intent.putExtra("nutrition", nv);
						startActivity(intent);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}});
		
		feedListView=(ListView)findViewById(R.id.listview_formula_feed);
		feedListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				try{
					FeedFormulaRecommandVo frv=(FeedFormulaRecommandVo)feedListView.getItemAtPosition(position);
					if(frv!=null&&frv.id!=null){
						Intent intent=new Intent(ActivityFormulaDetail.this,ActivityFeedDetail.class);
						intent.putExtra("formularecommand", frv);
						startActivity(intent);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}});
		
		
		sv=(ScrollView)findViewById(R.id.scroll_formula_nutrition);
	}

	private void initalData() {
		try{
			listNus=new ArrayList<NutritionVo>();
			listFeeds=new ArrayList<FeedFormulaRecommandVo>();
			if(ff!=null){
				ffName.setText(ff.name);
				ffUseNum.setText(ff.useNum+ff.systemUnitName);
				ffContent.setText(ff.content);
				
				try {
					HttpExecuteJson http = new HttpExecuteJson(this,
							mFormulaNutritionListener);
					Map<String, Object> rps = new HashMap<String, Object>();
					rps.put("method", "getFeedFormulaNutrition");
					rps.put("formulaId", ff.id);
					http.get(new ServiceHelper().GETFORMULATYPE, rps);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					HttpExecuteJson http = new HttpExecuteJson(this,
							mFormulaRecommandListener);
					Map<String, Object> rps = new HashMap<String, Object>();
					rps.put("method", "getFeedFormulaRecommand");
					rps.put("formulaId", ff.id);
					rps.put("flag", 0);//按什么推荐 0是按价格排序，1是按长得快速与否排序
					rps.put("pageNo", 1);
					rps.put("pageSize", Integer.MAX_VALUE);
					http.get(new ServiceHelper().GETFORMULATYPE, rps);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private httpReturnJson mFormulaNutritionListener = new httpReturnJson() {
		@Override
		public void onFailure(int error, String msg) {
			UIHelper.ToastMessage(ActivityFormulaDetail.this, "获取配方营养素失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFormulaDetail.this, "获取配方营养素退出");
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
					}else {
						NutritionVo nv=new NutritionVo();
						nv.id=null;
						nv.name="无";
						nv.systemUnitNum="无";
						nv.systemUnitName="无";
						listNus.add(nv);
					}

					nuAdapter = new NutritionListAdapter(listNus, getApplicationContext(),
							R.layout.fragment_fromula_nutrition_list_item);
					nuListView.setAdapter(nuAdapter);
					fixListViewHeight(nuListView);
					//数据加载完成改变一下scrollview的显示位置
					sv.scrollTo(0, 0);
				}else{
					UIHelper.ToastMessage(ActivityFormulaDetail.this, "获取配方营养素失败" + edv.getErrorMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				UIHelper.ToastMessage(ActivityFormulaDetail.this, "获取配方营养素失败,请联系管理员：" + e.getMessage());
			}
			// UIHelper.ToastMessage(_context,"总条数=" + result + "");
		}
	};

	private httpReturnJson mFormulaRecommandListener = new httpReturnJson() {
		@Override
		public void onFailure(int error, String msg) {
			UIHelper.ToastMessage(ActivityFormulaDetail.this, "获取配方组成推荐失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFormulaDetail.this, "获取配方组成推荐退出");
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
					List<FeedFormulaRecommandVo> temps1 = new ArrayList<FeedFormulaRecommandVo>();//(ArrayList<FeedFormulaRecommandVo>) edv.data;
					// List<String> temps=new ArrayList<String>();
					try{
						temps1 = JsonUtil.fromJsonArray(
								JsonUtil.toJson(edv.data),
								FeedFormulaRecommandVo.class);}catch (Exception e){}
					if(temps1!=null&&temps1.size()>0){
						listFeeds.addAll(temps1);
					}else {
						FeedFormulaRecommandVo ft=new FeedFormulaRecommandVo();
						ft.id=null;
						ft.name="无";
						ft.systemUnitNum="无";
						ft.systemUnitName="无";
						ft.value=0l;
						listFeeds.add(ft);
					}

					feedAdapter = new FeedListAdapter(listFeeds, getApplicationContext(),
							R.layout.fragment_formula_recommand_list_item);
					feedListView.setAdapter(feedAdapter);
					
					fixListViewHeight(feedListView);
					//数据加载完成改变一下scrollview的显示位置
					sv.scrollTo(0, 0);
				}else {
					UIHelper.ToastMessage(ActivityFormulaDetail.this, "获取配方组成推荐失败" + edv.getErrorMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				UIHelper.ToastMessage(ActivityFormulaDetail.this, "获取配方组成推荐失败,请联系管理员：" + e.getMessage());
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
