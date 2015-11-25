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
import com.surekam.pigfeed.bean.City;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.bean.FeedFormulaRecommandVo;
import com.surekam.pigfeed.bean.FeedFormulaType;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.bean.FeedType;
import com.surekam.pigfeed.bean.FeedVo;
import com.surekam.pigfeed.ui.view.PullDownView;
import com.surekam.pigfeed.ui.view.PullDownView.OnPullDownListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

	private Button submit;
	private EditText et_keyword;
	private String typeid;
	private String areaid;

	private PullDownView mPullDownView;
	// private List<String> mStrings = new ArrayList<String>();
	// 配方已经显示的配方
	private List<FeedVo> mfs = new ArrayList<FeedVo>();
	private ListView mListView;
	private ArrayAdapter<FeedVo> mAdapter;

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

		/*
		 * 1.使用PullDownView 2.设置OnPullDownListener 3.从mPullDownView里面获取ListView
		 */
		mPullDownView = (PullDownView) findViewById(R.id.formula_pull_down_view);
		// mPullDownView.setOnPullDownListener(this);
		mPullDownView.setOnPullDownListener(new OnPullDownListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				pageno = 1;
				pagesize = 5;
				try {

					loadFeeds(mFeedsRefreshListener);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onMore() {
				// TODO Auto-generated method stub
				if ((pagecount-(pageno*pagesize))>0) {
					pageno++;
					try {

						loadFeeds(mFeedsMoreListener);

					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					UIHelper.ToastMessage(getApplicationContext(), "已经没有更多推荐");
					mPullDownView.notifyDidMore();
				}

			}
		});
		mListView = mPullDownView.getListView();

		// mListView.setOnItemClickListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// UIHelper.ToastMessage(getApplicationContext(), "啊，你点中我了 " +
				// position,
				// Toast.LENGTH_SHORT).show();
				try {
					FeedVo
					ff=(FeedVo)mListView.getItemAtPosition(position);
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
		mAdapter = new ArrayAdapter<FeedVo>(this, R.layout.view_pulldown_item,
				mfs);
		mListView.setAdapter(mAdapter);

		//mPullDownView.enableAutoFetchMore(true, 1);

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
					loadFeeds(mFeedsRefreshListener);
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
		loadFeeds(mFeedsListener);
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
			
			mPullDownView = null;
			// mStrings=null;
			mListView = null;
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

	private void loadFeeds(httpReturnJson hrs) {
		try {
			HttpExecuteJson http = new HttpExecuteJson(this, hrs);
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
					List<FeedType> temps1 = (ArrayList<FeedType>) edv.data;
					// List<String> temps=new ArrayList<String>();
					if(feedTypes!=null){
						FeedType ft=new FeedType();
						ft.code=null;
						ft.id=null;
						ft.name="全部";
						feedTypes.add(ft);
					}
					for (Object f : temps1) {
						try {
							FeedType ft = new Gson().fromJson(
									new Gson().toJson(f),
									new TypeToken<FeedType>() {
									}.getType());
							if (ft != null && ft.name != null) {
								// temps.add(ft.name);
								feedTypes.add(ft);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					adFs = new ArrayAdapter<FeedType>(ActivityFeedQuery.this,
							android.R.layout.simple_spinner_item, feedTypes);
					feedType.setAdapter(adFs);
				}
			} catch (Exception e) {
				e.printStackTrace();
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
					List<AreaVo> temps1 = (ArrayList<AreaVo>) edv.data;
					// List<String> temps=new ArrayList<String>();
					if(areas!=null){
						AreaVo av=new AreaVo();
						av.code=null;
						av.id=null;
						av.name="全部";
						areas.add(av);
					}
					for (Object f : temps1) {
						try {
							AreaVo ft = new Gson().fromJson(
									new Gson().toJson(f),
									new TypeToken<AreaVo>() {
									}.getType());
							if (ft != null && ft.name != null) {
								// temps.add(ft.name);
								areas.add(ft);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					adArs = new ArrayAdapter<AreaVo>(ActivityFeedQuery.this,
							android.R.layout.simple_spinner_item, areas);
					area.setAdapter(adArs);
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
					List<FeedVo> temps1 = (ArrayList<FeedVo>) edv.data;
					mfs.clear();
					if(edv.page!=null){
						pagecount = (int) edv.page.totalItems;	
					}else{
						pagecount=0;
					}
					
					// List<String> temps=new ArrayList<String>();
					if(temps1!=null){
						for (Object f : temps1) {
							try {
								FeedVo ft = new Gson().fromJson(
										new Gson().toJson(f),
										new TypeToken<FeedVo>() {
										}.getType());
								if (ft != null && ft.name != null) {
									// temps.add(ft.name);
									mfs.add(ft);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}	
					}
					if(mfs!=null&&mfs.size()==0){
						FeedVo fv=new FeedVo();
						fv.id=null;
						fv.name="无推荐";
						mfs.add(fv);
					}
					
				}
				try{
					if((pagecount-(pageno*pagesize))>0){
						mPullDownView.haveMore=true;
					}else{
						mPullDownView.haveMore=false;
					}
				}catch(Exception e){e.printStackTrace();}
				// mStrings.addAll(temps);
				mAdapter.notifyDataSetChanged();
				// 诉它数据加载完毕;
				mPullDownView.notifyDidLoad();
			} catch (Exception e) {
				e.printStackTrace();
				// mStrings.addAll(temps);
				try{
				mAdapter.notifyDataSetChanged();
				// 诉它数据加载完毕;
				mPullDownView.notifyDidLoad();}catch(Exception e1){e1.printStackTrace();}
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

	private httpReturnJson mFeedsRefreshListener = new httpReturnJson() {

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
					if(edv.page!=null){
						pageno=edv.page.pageNo;
						pagesize=edv.page.pageSize;	
						pagecount = (int) edv.page.totalItems;
					}else{
						pagecount=0;
					}
					mfs.clear();
					List<FeedVo> temps1 = (ArrayList<FeedVo>) edv.data;
					
					// List<String> temps=new ArrayList<String>();
					if(temps1!=null){
						for (Object f : temps1) {
							try {
								FeedVo ft = new Gson().fromJson(
										new Gson().toJson(f),
										new TypeToken<FeedVo>() {
										}.getType());
								if (ft != null && ft.name != null) {
									// temps.add(ft.name);
									mfs.add(ft);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}	
					}
					if(mfs!=null&&mfs.size()==0){
						FeedVo fv=new FeedVo();
						fv.id=null;
						fv.name="无推荐";
						mfs.add(fv);
					}
					
				}
				try{
					if((pagecount-(pageno*pagesize))>0){
						mPullDownView.haveMore=true;
					}else{
						mPullDownView.haveMore=false;
					}
				}catch(Exception e){e.printStackTrace();}
				// mStrings.addAll(temps);
				mAdapter.notifyDataSetChanged();
				// 诉它数据加载完毕;
				mPullDownView.notifyDidRefresh();
			} catch (Exception e) {
				e.printStackTrace();
				// mStrings.addAll(temps);
				try{
				mAdapter.notifyDataSetChanged();
				// 诉它数据加载完毕;
				mPullDownView.notifyDidRefresh();}catch(Exception e1){e1.printStackTrace();}
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

	private httpReturnJson mFeedsMoreListener = new httpReturnJson() {

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
					List<FeedVo> temps1 = (ArrayList<FeedVo>) edv.data;
					if(edv.page!=null){
						pageno=edv.page.pageNo;
						pagesize=edv.page.pageSize;
						pagecount = (int) edv.page.totalItems;	
					}else{
						pagecount=0;
					}
					// List<String> temps=new ArrayList<String>();
					if(temps1!=null){
						for (Object f : temps1) {
							try {
								FeedVo ft = new Gson().fromJson(
										new Gson().toJson(f),
										new TypeToken<FeedVo>() {
										}.getType());
								if (ft != null && ft.name != null) {
									// temps.add(ft.name);
									mfs.add(ft);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}	
					}
					if(mfs!=null&&mfs.size()==0){
						FeedVo fv=new FeedVo();
						fv.id=null;
						fv.name="无推荐";
						mfs.add(fv);
					}
					
					
				}
				try{
					if((pagecount-(pageno*pagesize))>0){
						mPullDownView.haveMore=true;
					}else{
						mPullDownView.haveMore=false;
					}
				}catch(Exception e){e.printStackTrace();}
				// mStrings.addAll(temps);
				mAdapter.notifyDataSetChanged();
				// 诉它数据加载完毕;
				mPullDownView.notifyDidMore();
			} catch (Exception e) {
				e.printStackTrace();
				// mStrings.addAll(temps);
				try{
				mAdapter.notifyDataSetChanged();
				// 诉它数据加载完毕;
				mPullDownView.notifyDidMore();}catch(Exception e1){e1.printStackTrace();}
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
