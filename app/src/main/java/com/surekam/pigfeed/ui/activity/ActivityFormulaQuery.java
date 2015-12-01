package com.surekam.pigfeed.ui.activity;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.api.HttpExecuteJson;
import com.surekam.pigfeed.api.HttpExecuteJson.httpReturnJson;
import com.surekam.pigfeed.api.ServiceHelper;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.AreaVo;
import com.surekam.pigfeed.bean.City;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.bean.FeedFormulaType;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.tools.JsonUtil;
import com.surekam.pigfeed.ui.adapter.FormulaAdapter;
import com.surekam.pigfeed.ui.view.PullDownView;
import com.surekam.pigfeed.ui.view.PullDownView.OnPullDownListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityFormulaQuery extends Activity implements UncaughtExceptionHandler {

	private int showWho=0;//0是饲料查询，1是跳转到显示智能推荐

	private View customLiveIndexTitleView;
	private TextView txtTitle;
	private ImageView ivBack;

	private Button submit;
	private EditText et_area;
	private EditText et_keyword;

	private PullToRefreshListView mFormulasView;
	private RelativeLayout mNoms;//没有配方的提示
	/** 是否刷新 */
	private boolean isRefreshing = false;
	// private List<String> mStrings = new ArrayList<String>();
	// 配方已经显示的配方
	private List<FeedFormulaVo> mFfs = new ArrayList<FeedFormulaVo>();
	private FormulaAdapter mAdapter;

	// 分页的标志
	private int pageno = 1;
	private int pagesize = 5;

	// 用来查询的过滤变量
	private City _currentCity;//不使用，因为是自己数据库传来的，现在改用下一种，从网络获取的
	//接口返回的区域id
	private String _currentCityCode;
	// 饲料配方类型
	private List<FeedFormulaType> formulaTypes;
	// 饲料配方的spinner
	private Spinner formuType;
	// 饲料配方类型的spinner数据源
	private ArrayAdapter<FeedFormulaType> adFys;
	
	// areavo类来显示
	private Spinner area;
	private List<AreaVo> areas;
	private ArrayAdapter<AreaVo> adArs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_formula_query_main);
		try{
			showWho = getIntent().getIntExtra("showWho", 0);//判断展示那个页面

		}catch(Exception e){
			e.printStackTrace();
		}
		initalTitle();
		initialView();
	}

	private void initalTitle() {
		customLiveIndexTitleView = findViewById(R.id.title_formulaquery);
		txtTitle = (TextView) customLiveIndexTitleView
				.findViewById(R.id.title_text_nav);
		txtTitle.setText("配方查询");

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
		formulaTypes = new ArrayList<FeedFormulaType>();
		areas=new ArrayList<AreaVo>();
		
		et_area = (EditText) findViewById(R.id.edittext_formula_area);
		et_area.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ActivityFormulaQuery.this,
						ActivitySelectCitys.class);
				startActivityForResult(intent, 1);
			}
		});

		et_keyword = (EditText) findViewById(R.id.edittext_formula_keyword);

		submit = (Button) findViewById(R.id.button_formula_submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					pageno=1;
					mFfs.clear();
					isRefreshing=false;
					loadFormulas();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		formuType = (Spinner) findViewById(R.id.spinner_formula_type);
		area=(Spinner)findViewById(R.id.spinner_formula_area);
		area.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				try {
					AreaVo av = (AreaVo) area.getItemAtPosition(position);
					_currentCityCode=av.id+"";
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}});

		mFormulasView=(PullToRefreshListView) findViewById(R.id.formula_pull_down_view);
		mFormulasView.setMode(PullToRefreshBase.Mode.BOTH);
		mNoms=(RelativeLayout)findViewById(R.id.rl_match_noms);
		mFfs=new ArrayList<FeedFormulaVo>();

		mFormulasView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!isRefreshing) {
					isRefreshing = true;

					mFfs.clear();
					pageno = 1;
					String label = DateUtils.formatDateTime(getApplicationContext(),
							System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
									| DateUtils.FORMAT_SHOW_DATE
									| DateUtils.FORMAT_ABBREV_ALL);

					// Update the LastUpdatedLabel
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
					refreshView.getLoadingLayoutProxy(false, true).setPullLabel(
							getString(R.string.pull_to_refresh_pull_label));
					refreshView.getLoadingLayoutProxy(false, true)
							.setRefreshingLabel(
									getString(R.string.pull_to_refresh_refreshing_label));
					refreshView.getLoadingLayoutProxy(false, true)
							.setReleaseLabel(
									getString(R.string.pull_to_refresh_release_label));

					loadFormulas();
				} else {
					mFormulasView.onRefreshComplete();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!isRefreshing) {
					isRefreshing = true;

					pageno++;
					String label = DateUtils.formatDateTime(getApplicationContext(),
							System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
									| DateUtils.FORMAT_SHOW_DATE
									| DateUtils.FORMAT_ABBREV_ALL);

					// Update the LastUpdatedLabel
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
					refreshView.getLoadingLayoutProxy(false, true).setPullLabel(
							getString(R.string.pull_to_refresh_from_bottom_pull_label));
					refreshView.getLoadingLayoutProxy(false, true)
							.setRefreshingLabel(
									getString(R.string.pull_to_refresh_from_bottom_refreshing_label));
					refreshView.getLoadingLayoutProxy(false, true)
							.setReleaseLabel(
									getString(R.string.pull_to_refresh_from_bottom_release_label));

					loadFormulas();
				} else {
					mFormulasView.onRefreshComplete();
				}
			}
		});

		mFormulasView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try{
					FeedFormulaVo ff=(FeedFormulaVo)parent.getAdapter().getItem(position);
					if(ff!=null&&ff.id!=null){
						if(showWho==1){
							Intent intent=new Intent(ActivityFormulaQuery.this,ActivityFormulaArtificial2.class);
							intent.putExtra("formula", ff);
							startActivity(intent);
						}else{
							Intent intent=new Intent(ActivityFormulaQuery.this,ActivityFormulaDetail.class);
							intent.putExtra("formula", ff);
							startActivity(intent);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		// 在控件初始化完成是之后执行初始化数据
		initialData();
	}

	private void initialData() {
		loadFormulaTypes();
		loadAreas();
		//loadFormulas();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:
			try {
				Bundle b = data.getExtras(); // data为B中回传的Intent
				City city = (City) b.get("city");
				_currentCity = city;
				et_area.setText(city.getCity());
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
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

			submit = null;
			et_area = null;
			mFormulasView = null;
			mAdapter = null;
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载配方类型
	 */
	private void loadFormulaTypes() {
		try {
			HttpExecuteJson http = new HttpExecuteJson(this,
					mFormulaTypesListener);
			Map<String, Object> rps = new HashMap<String, Object>();
			rps.put("method", "getType");
			rps.put("typeFlag", new ServiceHelper().TYPEFORMULA);
			rps.put("pageNo", "1");
			rps.put("pageSize", "10000");
			http.get(new ServiceHelper().GETFORMULATYPE, rps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 初始化区域*/
	private void loadAreas() {
		try {
			HttpExecuteJson http = new HttpExecuteJson(this, mAreasListener);
			Map<String, Object> rps = new HashMap<String, Object>();
			rps.put("method", "getArea");
			rps.put("pageNo", "1");
			rps.put("pageSize", "10000");
			http.get(new ServiceHelper().GETFORMULATYPE, rps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private httpReturnJson mAreasListener = new httpReturnJson() {

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

					List<AreaVo> temps1 = new ArrayList<AreaVo>();//(ArrayList<AreaVo>) edv.data;
					try{
					temps1 = JsonUtil.fromJsonArray(
							JsonUtil.toJson(edv.data),
							AreaVo.class);}catch (Exception e){}
					// List<String> temps=new ArrayList<String>();
					if(areas!=null){
						AreaVo av=new AreaVo();
						av.code=null;
						av.id=null;
						av.name="全部";
						areas.add(av);
					}
					if(temps1!=null&&temps1.size()>0){
						areas.addAll(temps1);
					}
					adArs = new ArrayAdapter<AreaVo>(ActivityFormulaQuery.this,
							android.R.layout.simple_spinner_item, areas);
					area.setAdapter(adArs);
				}else{
					UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取区域失败" + edv.getErrorMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取区域失败，请联系管理员：" + e.getMessage());
			}
		}

		@Override
		public void onFailure(int error, String msg) {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取区域失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取区域退出");
		}
	};

	private httpReturnJson mFormulaTypesListener = new httpReturnJson() {
		@Override
		public void onFailure(int error, String msg) {
			UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取配方类型失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取配方类型退出");
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
					List<FeedFormulaType> temps1 = new ArrayList<FeedFormulaType>();//(ArrayList<FeedFormulaType>) edv.data;
					try{
					temps1 = JsonUtil.fromJsonArray(
							JsonUtil.toJson(edv.data),
							FeedFormulaType.class);}catch (Exception e){}

					// List<String> temps=new ArrayList<String>();
					if(formulaTypes!=null){
						FeedFormulaType ft=new FeedFormulaType();
						ft.code=null;
						ft.id=null;
						ft.name="全部";
						formulaTypes.add(ft);
					}
					if(temps1!=null&&temps1.size()>0){
						formulaTypes.addAll(temps1);
					}
					adFys = new ArrayAdapter<FeedFormulaType>(
							ActivityFormulaQuery.this,
							android.R.layout.simple_spinner_item, formulaTypes);
					formuType.setAdapter(adFys);
				}else{
					UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取配方类型失败" + edv.getErrorMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取配方类型失败，请联系管理员：" + e.getMessage());
			}
			// UIHelper.ToastMessage(_context,"总条数=" + result + "");
		}
	};

	/*
	 * 加载配方
	 */
	private void loadFormulas() {
		try {

			String typeid = null;
			if (formuType != null) {
				FeedFormulaType ft = (FeedFormulaType) formuType
						.getSelectedItem();
				if (ft != null && ft.id!=null&& ft.id > 0) {
					typeid = ft.id + "";
				}
			}
			String areaid = null;
			if (_currentCity != null
					&& _currentCity.getNumber() != null) {
				areaid = _currentCity.getNumber();
			}
			if(area!=null){
				AreaVo av=(AreaVo)area.getSelectedItem();
				if(av!=null&&av.id!=null &&av.id>0){
					areaid=av.id+"";
				}
			}
			String keyword = null;
			if (et_keyword != null) {
				keyword = et_keyword.getText().toString();
			}

			HttpExecuteJson http = new HttpExecuteJson(this, mFormulaListener);
			Map<String, Object> rps = new HashMap<String, Object>();
			rps.put("method", "getFeedFormula");
			if (typeid != null) {
				rps.put("typeId", typeid);
			}
			if (areaid != null) {
				rps.put("areaId", areaid);
			}
			if (keyword != null) {
				rps.put("keyword", keyword);
			}
			rps.put("pageNo", pageno);
			rps.put("pageSize", pagesize);
			http.get(new ServiceHelper().GETFORMULATYPE, rps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private httpReturnJson mFormulaListener = new httpReturnJson() {
		@Override
		public void onFailure(int error, String msg) {
			UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取配方失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取配方退出");
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

					List<FeedFormulaVo> temps1=new ArrayList<FeedFormulaVo>();
					try{
					temps1 = JsonUtil.fromJsonArray(
							JsonUtil.toJson(edv.data),
							FeedFormulaVo.class);}catch (Exception e){}

					if(temps1==null||temps1.size()==0){        // mAdapterMyCreate.notifyDataSetChanged();
						Toast.makeText(ActivityFormulaQuery.this, "没有更多数据了",
								Toast.LENGTH_SHORT).show();
					}else{
						mFfs.addAll(temps1);
					}

					if(isRefreshing){
						mAdapter.notifyDataSetChanged();
					}else{
						mAdapter = new FormulaAdapter(ActivityFormulaQuery.this, mFfs);
						mFormulasView.setAdapter(mAdapter);
					}

					if(mFfs!=null&&mFfs.size()>0){
						mFormulasView.setVisibility(View.VISIBLE);
						mNoms.setVisibility(View.GONE);
					}else{
						mFormulasView.setVisibility(View.GONE);
						mNoms.setVisibility(View.VISIBLE);
					}

					mFormulasView.onRefreshComplete();
					isRefreshing = false;
				}else{
					UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取配方失败" + edv.getErrorMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				// mStrings.addAll(temps);
				UIHelper.ToastMessage(ActivityFormulaQuery.this, "获取配方失败，请联系管理员：" + e.getMessage());
			}
			// UIHelper.ToastMessage(_context,"总条数=" + result + "");
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
