package com.surekam.pigfeed.ui.activity;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.surekam.pigfeed.ui.view.PullDownView;
import com.surekam.pigfeed.ui.view.PullDownView.OnPullDownListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityFormulaQuery extends Activity implements UncaughtExceptionHandler {

	private View customLiveIndexTitleView;
	private TextView txtTitle;
	private ImageView ivBack;

	private Button submit;
	private EditText et_area;
	private EditText et_keyword;

	private static final int WHAT_DID_LOAD_DATA = 0;
	private static final int WHAT_DID_REFRESH = 1;
	private static final int WHAT_DID_MORE = 2;

	private PullDownView mPullDownView;
	// private List<String> mStrings = new ArrayList<String>();
	// 配方已经显示的配方
	private List<FeedFormulaVo> mFfs = new ArrayList<FeedFormulaVo>();
	private ListView mListView;
	private ArrayAdapter<FeedFormulaVo> mAdapter;

	// 分页的标志
	private int pageno = 1;
	private int pagesize = 5;
	// 记录总数
	private int pagecount = 0;

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
					loadFormulas(mRefreshFormulaListener, typeid, areaid,
							keyword, pageno + "", pagesize + "");

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onMore() {
				// TODO Auto-generated method stub
				if ((pageno + 1) * pagesize > pagecount) {
					Toast.makeText(getApplicationContext(), "已经没有更多推荐",
							Toast.LENGTH_SHORT);
				} else {
					pageno++;
					try {
						String typeid = null;
						if (formuType != null) {
							FeedFormulaType ft = (FeedFormulaType) formuType
									.getSelectedItem();
							if (ft != null &&ft.id!=null&& ft.id > 0) {
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
						loadFormulas(mMoreFormulaListener, typeid, areaid,
								keyword, pageno + "", pagesize + "");

					} catch (Exception e) {
						e.printStackTrace();
					}

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
				//Toast.makeText(getApplicationContext(), "啊，你点中我了 " + position,
				//		Toast.LENGTH_SHORT).show();
				try{
					FeedFormulaVo ff=(FeedFormulaVo)mListView.getItemAtPosition(position);
					if(ff!=null&&ff.id!=null){
						Intent intent=new Intent(ActivityFormulaQuery.this,ActivityFormulaDetail.class);
						intent.putExtra("formula", ff);
						startActivity(intent);	
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		mAdapter = new ArrayAdapter<FeedFormulaVo>(this,
				R.layout.view_pulldown_item, mFfs);
		mListView.setAdapter(mAdapter);

		//mPullDownView.enableAutoFetchMore(true, 1);

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
					String typeid = null;
					if (formuType != null) {
						FeedFormulaType ft = (FeedFormulaType) formuType
								.getSelectedItem();
						if (ft != null &&ft.id!=null&& ft.id > 0) {
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
						if(av!=null&& av.id!=null && av.id>0){
							areaid=av.id+"";
						}
					}
					String keyword = null;
					if (et_keyword != null) {
						keyword = et_keyword.getText().toString();
					}
					loadFormulas(mFormulaListener, typeid, areaid, keyword,
							pageno + "", pagesize + "");
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

		// 在控件初始化完成是之后执行初始化数据
		initialData();
	}

	private void initialData() {
		loadFormulaTypes();
		loadAreas();
		loadFormulas(mFormulaListener, null, null, null,
				pageno + "", pagesize + "");
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
					adArs = new ArrayAdapter<AreaVo>(ActivityFormulaQuery.this,
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
					List<FeedFormulaType> temps1 = (ArrayList<FeedFormulaType>) edv.data;
					// List<String> temps=new ArrayList<String>();
					if(formulaTypes!=null){
						FeedFormulaType ft=new FeedFormulaType();
						ft.code=null;
						ft.id=null;
						ft.name="全部";
						formulaTypes.add(ft);
					}
					for (Object f : temps1) {
						try {
							FeedFormulaType ft = new Gson().fromJson(
									new Gson().toJson(f),
									new TypeToken<FeedFormulaType>() {
									}.getType());
							if (ft != null && ft.name != null) {
								// temps.add(ft.name);
								formulaTypes.add(ft);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					adFys = new ArrayAdapter<FeedFormulaType>(
							ActivityFormulaQuery.this,
							android.R.layout.simple_spinner_item, formulaTypes);
					formuType.setAdapter(adFys);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// UIHelper.ToastMessage(_context,"总条数=" + result + "");
		}
	};

	/*
	 * 加载配方
	 */
	private void loadFormulas(httpReturnJson listener, String typeid,
			String areaid, String keyword, String pno, String psize) {
		try {
			HttpExecuteJson http = new HttpExecuteJson(this, listener);
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
			rps.put("pageNo", pno);
			rps.put("pageSize", psize);
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
					mFfs.clear();
					if(edv.page!=null){
						pagecount = (int) edv.page.totalItems;	
					}else{
						pagecount=0;
					}
					List<FeedFormulaVo> temps1 = (ArrayList<FeedFormulaVo>) edv.data;
					// List<String> temps=new ArrayList<String>();
					if(temps1!=null){
						for (Object f : temps1) {
							try {
								FeedFormulaVo ft = new Gson().fromJson(
										new Gson().toJson(f),
										new TypeToken<FeedFormulaVo>() {
										}.getType());
								if (ft != null && ft.name != null) {
									// temps.add(ft.name);
									mFfs.add(ft);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}	
					}
					if(mFfs!=null&&mFfs.size()==0){
						FeedFormulaVo ft=new FeedFormulaVo();
						ft.id=null;
						ft.name="无推荐";
						mFfs.add(ft);
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
			// UIHelper.ToastMessage(_context,"总条数=" + result + "");
		}
	};

	private httpReturnJson mRefreshFormulaListener = new httpReturnJson() {
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
					if(edv.page!=null){
						pagecount = (int) edv.page.totalItems;	
					}else{
						pagecount=0;
					}
					
					mFfs.clear();
					List<FeedFormulaVo> temps1 = (ArrayList<FeedFormulaVo>) edv.data;
					// List<String> temps=new ArrayList<String>();
					if(temps1!=null){
						for (Object f : temps1) {
							try {
								FeedFormulaVo ft = new Gson().fromJson(
										new Gson().toJson(f),
										new TypeToken<FeedFormulaVo>() {
										}.getType());
								if (ft != null && ft.name != null) {
									// temps.add(ft.name);
									mFfs.add(ft);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					if(mFfs!=null&&mFfs.size()==0){
						FeedFormulaVo ft=new FeedFormulaVo();
						ft.id=null;
						ft.name="无推荐";
						mFfs.add(ft);
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
				// 
				// 诉它数据加载完毕;
				mPullDownView.notifyDidRefresh();
			} catch (Exception e) {
				e.printStackTrace();
				// mStrings.addAll(temps);
				try{
				mAdapter.notifyDataSetChanged();
				// 
				// 诉它数据加载完毕;
				mPullDownView.notifyDidRefresh();}catch(Exception e1){e1.printStackTrace();}
			}
			// UIHelper.ToastMessage(_context,"总条数=" + result + "");
		}

	};

	private httpReturnJson mMoreFormulaListener = new httpReturnJson() {
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
					if(edv.page!=null){
						pagecount = (int) edv.page.totalItems;	
					}else{
						pagecount=0;
					}
					
					List<FeedFormulaVo> temps1 = (ArrayList<FeedFormulaVo>) edv.data;
					// List<String> temps=new ArrayList<String>();
					if(temps1!=null){
						for (Object f : temps1) {
							try {
								FeedFormulaVo ft = new Gson().fromJson(
										new Gson().toJson(f),
										new TypeToken<FeedFormulaVo>() {
										}.getType());
								if (ft != null && ft.name != null) {
									// temps.add(ft.name);
									mFfs.add(ft);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}	
					}
					if(mFfs!=null&&mFfs.size()==0){
						FeedFormulaVo ft=new FeedFormulaVo();
						ft.id=null;
						ft.name="无推荐";
						mFfs.add(ft);
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
				// 告诉它获取更多完毕
				mPullDownView.notifyDidMore();
			} catch (Exception e) {
				e.printStackTrace();
				// mStrings.addAll(temps);
				try{
				mAdapter.notifyDataSetChanged();
				// 告诉它获取更多完毕
				mPullDownView.notifyDidMore();}catch(Exception e1){e1.printStackTrace();}
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
