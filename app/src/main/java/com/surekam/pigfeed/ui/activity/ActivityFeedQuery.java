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
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.api.HttpExecuteJson;
import com.surekam.pigfeed.api.ServiceHelper;
import com.surekam.pigfeed.api.HttpExecuteJson.httpReturnJson;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.AreaVo;
import com.surekam.pigfeed.bean.City;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.bean.FeedFormulaRecommandVo;
import com.surekam.pigfeed.bean.FeedFormulaType;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.bean.FeedType;
import com.surekam.pigfeed.bean.FeedVo;
import com.surekam.pigfeed.tools.JsonUtil;
import com.surekam.pigfeed.ui.adapter.FeedAdapter;
import com.surekam.pigfeed.ui.adapter.FormulaAdapter;
import com.surekam.pigfeed.ui.view.PullDownView;
import com.surekam.pigfeed.ui.view.PullDownView.OnPullDownListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
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

/*
 * 饲料查询界面*/
public class ActivityFeedQuery extends Activity implements UncaughtExceptionHandler {
	
	private int showWho=0;//展示的是饲料页面还是价格查询页面，这个区别只有点击查询结果的时候，跳转不同0是饲料查询页面，1是价格趋势页面

	private View customLiveIndexTitleView;
	private TextView txtTitle;
	private ImageView ivBack;
	private TextView tvOpe;

	private Button submit;
	private EditText et_keyword;
	private String typeid;
	private String areaid;

	private PullToRefreshListView mFormulasView;
	private RelativeLayout mNoms;//没有飼料的提示
	/** 是否刷新 */
	private boolean isRefreshing = false;
	// private List<String> mStrings = new ArrayList<String>();
	// 配方已经显示的配方
	private List<FeedVo> mfs = new ArrayList<FeedVo>();
	private FeedAdapter mAdapter;

	// 分页的标志
	private int pageno = 1;
	private int pagesize = 5;
	// 记录总数
	private int pagecount = 0;
	//总页数
	private int pages=1;

	// 饲料配方类型
	private List<FeedType> feedTypes;
	// 饲料配方的spinner
	private Spinner feedType;
	// 饲料配方类型的spinner数据源
	private ArrayAdapter<FeedType> adFs;

	// 区域
	private List<AreaVo> areas;
	private Spinner area;
	private ArrayAdapter<AreaVo> adArs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_feed_query_main);
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
		if(showWho>0){
			txtTitle.setText("价格趋势");	
		}else{
		txtTitle.setText("饲料查询");}

		ivBack = (ImageView) findViewById(R.id.title_back);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();

			}

		});

		tvOpe=(TextView)findViewById(R.id.title_operate);
		tvOpe.setVisibility(View.VISIBLE);
		tvOpe.setText("查询");
		tvOpe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					pageno=1;
					mfs.clear();
					isRefreshing=false;
					loadFeeds();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	private void initialView() {
		feedTypes = new ArrayList<FeedType>();
		feedType = (Spinner) findViewById(R.id.spinner_formula_type);

		areas = new ArrayList<AreaVo>();
		area = (Spinner) findViewById(R.id.spinner_formula_area);

		et_keyword = (EditText) findViewById(R.id.edittext_formula_keyword);

		submit = (Button) findViewById(R.id.button_formula_submit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					pageno=1;
					mfs.clear();
					isRefreshing=false;
					loadFeeds();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		mFormulasView=(PullToRefreshListView) findViewById(R.id.formula_pull_down_view);
		mFormulasView.setMode(PullToRefreshBase.Mode.BOTH);
		mNoms=(RelativeLayout)findViewById(R.id.rl_match_noms);
		mfs=new ArrayList<FeedVo>();

		mFormulasView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!isRefreshing) {
					isRefreshing = true;

					mfs.clear();
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

					loadFeeds();
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

					loadFeeds();
				} else {
					mFormulasView.onRefreshComplete();
				}
			}
		});

		mFormulasView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					FeedVo
							ff=(FeedVo)parent.getAdapter().getItem(position);
					if(ff!=null&&ff.id!=null){
						FeedFormulaRecommandVo fv=new FeedFormulaRecommandVo();
						fv.feedId=ff.id;
						fv.feedName=ff.name;
						fv.remark=ff.remark;

						if(showWho>0){
							Intent intent=new
									Intent(ActivityFeedQuery.this,ActivityPriceMain.class);
							intent.putExtra("feedvo", ff);
							startActivity(intent);
						}else{
							Intent intent=new
									Intent(ActivityFeedQuery.this,ActivityFeedDetail.class);
							intent.putExtra("formularecommand", fv);
							startActivity(intent);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// 在控件初始化完成是之后执行初始化数据
		initialData();
	}

	private void initialData() {
		loadFeedTypes();
		loadAreas();
		//loadFeeds();
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
			
			mFormulasView=null;
			mAdapter = null;
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载配方类型
	 */
	private void loadFeedTypes() {
		try {
			HttpExecuteJson http = new HttpExecuteJson(this, mFeedTypesListener);
			Map<String, Object> rps = new HashMap<String, Object>();
			rps.put("method", "getType");
			rps.put("typeFlag", new ServiceHelper().TYPEFEED);
			rps.put("pageNo", "1");
			rps.put("pageSize", "10000");
			http.get(new ServiceHelper().GETFORMULATYPE, rps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 加载区域
	 */
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

	private void loadFeeds() {
		try {
			HttpExecuteJson http = new HttpExecuteJson(this, mFeedsListener);
			Map<String, Object> rps = new HashMap<String, Object>();
			rps.put("method", "getFeed");
			try {
				typeid=null;
				FeedType ft = (FeedType) feedType.getSelectedItem();
				if (ft != null && ft.id > 0) {
					typeid = ft.id + "";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				areaid=null;
				AreaVo av = (AreaVo) area.getSelectedItem();
				if (av != null &&av.id!=null && av.id > 0) {
					areaid = av.id + "";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (typeid != null && (!typeid.equals(""))) {
				rps.put("typeId", typeid);
			}
			if (areaid != null && (!areaid.equals(""))) {
				rps.put("areaId", areaid);
			}
			if (et_keyword.getText() != null
					&& (!et_keyword.getText().equals(""))) {
				rps.put("keyword", et_keyword.getText());
			}

			rps.put("pageNo", pageno);
			rps.put("pageSize", pagesize);
			http.get(new ServiceHelper().GETFORMULATYPE, rps);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private httpReturnJson mFeedTypesListener = new httpReturnJson() {
		@Override
		public void onFailure(int error, String msg) {
			UIHelper.ToastMessage(ActivityFeedQuery.this, "获取饲料类型失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFeedQuery.this, "获取饲料类型退出");
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
					List<FeedType> temps1 = new ArrayList<FeedType>();//(ArrayList<FeedType>) edv.data;
					try{
					temps1 = JsonUtil.fromJsonArray(
							JsonUtil.toJson(edv.data),
							FeedType.class);}catch (Exception e){}

					// List<String> temps=new ArrayList<String>();
					if(feedTypes!=null){
						FeedType ft=new FeedType();
						ft.code=null;
						ft.id=null;
						ft.name="全部";
						feedTypes.add(ft);
					}
					if(temps1!=null&&temps1.size()>0){
						feedTypes.addAll(temps1);
					}
					adFs = new ArrayAdapter<FeedType>(ActivityFeedQuery.this,
							android.R.layout.simple_spinner_item, feedTypes);
					feedType.setAdapter(adFs);
				}else{
					UIHelper.ToastMessage(ActivityFeedQuery.this, "获取饲料类型失败"+edv.getErrorMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				UIHelper.ToastMessage(ActivityFeedQuery.this, "获取饲料类型失败，请联系管理员：" + e.getMessage());
			}
			// UIHelper.ToastMessage(_context,"总条数=" + result + "");
		}
	};

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
					adArs = new ArrayAdapter<AreaVo>(ActivityFeedQuery.this,
							android.R.layout.simple_spinner_item, areas);
					area.setAdapter(adArs);
				}else{
					UIHelper.ToastMessage(ActivityFeedQuery.this, "获取区域失败" + edv.getErrorMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				UIHelper.ToastMessage(ActivityFeedQuery.this, "获取区域失败,请联系管理员：" + e.getMessage());
			}
		}

		@Override
		public void onFailure(int error, String msg) {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFeedQuery.this, "获取区域失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFeedQuery.this, "获取区域退出");
		}
	};

	private httpReturnJson mFeedsListener = new httpReturnJson() {

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

					//List<FeedVo> temps2 = (ArrayList<FeedVo>) edv.data;
					List<FeedVo> temps1=new ArrayList<FeedVo>();
					try{
					temps1 = JsonUtil.fromJsonArray(
							JsonUtil.toJson(edv.data),
							FeedVo.class);}catch (Exception e){}

					if(temps1==null||temps1.size()==0){        // mAdapterMyCreate.notifyDataSetChanged();
						Toast.makeText(ActivityFeedQuery.this, "没有更多数据了",
								Toast.LENGTH_SHORT).show();
					}else{
						mfs.addAll(temps1);
					}

					if(isRefreshing){
						mAdapter.notifyDataSetChanged();
					}else{
						mAdapter = new FeedAdapter(ActivityFeedQuery.this, mfs);
						mFormulasView.setAdapter(mAdapter);
					}

					if(mfs!=null&&mfs.size()>0){
						mFormulasView.setVisibility(View.VISIBLE);
						mNoms.setVisibility(View.GONE);
					}else{
						mFormulasView.setVisibility(View.GONE);
						mNoms.setVisibility(View.VISIBLE);
					}

					mFormulasView.onRefreshComplete();
					isRefreshing = false;
					
				}else{
					UIHelper.ToastMessage(ActivityFeedQuery.this, "获取饲料失败" + edv.getErrorMsg());
				}
			} catch (Exception e) {
				e.printStackTrace();
				// mStrings.addAll(temps);
				UIHelper.ToastMessage(ActivityFeedQuery.this, "获取饲料失败，请联系管理员：" + e.getMessage());
			}
		}

		@Override
		public void onFailure(int error, String msg) {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFeedQuery.this, "获取饲料失败" + msg);
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			UIHelper.ToastMessage(ActivityFeedQuery.this, "获取饲料退出" );
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
