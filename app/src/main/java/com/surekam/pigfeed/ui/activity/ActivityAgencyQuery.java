package com.surekam.pigfeed.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.api.HttpExecuteJson;
import com.surekam.pigfeed.api.ServiceHelper;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.AgencyVo;
import com.surekam.pigfeed.bean.AreaVo;
import com.surekam.pigfeed.bean.City;
import com.surekam.pigfeed.bean.CommonResultVo;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.bean.FeedFormulaType;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.tools.JsonUtil;
import com.surekam.pigfeed.ui.adapter.AgencySimpleAdapter;
import com.surekam.pigfeed.ui.adapter.FormulaAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangzy_1 on 2015/12/15.
 */
public class ActivityAgencyQuery extends Activity implements Thread.UncaughtExceptionHandler {

    private int showWho=0;//0是饲料查询，1是跳转到显示智能推荐

    private View customLiveIndexTitleView;
    private TextView txtTitle;
    private ImageView ivBack;
    private TextView tvOpe;

    private Button submit;
    private EditText et_area;
    private EditText et_keyword;

    private PullToRefreshListView mFormulasView;
    private RelativeLayout mNoms;//没有配方的提示
    /** 是否刷新 */
    private boolean isRefreshing = false;
    // private List<String> mStrings = new ArrayList<String>();
    // 配方已经显示的配方
    private List<AgencyVo> mFfs = new ArrayList<AgencyVo>();
    private AgencySimpleAdapter mAdapter;

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
        txtTitle.setText("经销商查询");

        ivBack = (ImageView) findViewById(R.id.title_back);
        ivBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();

            }

        });

        tvOpe=(TextView)findViewById(R.id.title_operate);
        tvOpe.setVisibility(View.VISIBLE);
        tvOpe.setText("查询");
        tvOpe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    }

    private void initialView() {
        formulaTypes = new ArrayList<FeedFormulaType>();
        areas=new ArrayList<AreaVo>();

        et_area = (EditText) findViewById(R.id.edittext_formula_area);
        et_area.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(ActivityAgencyQuery.this,
                        ActivitySelectCitys.class);
                startActivityForResult(intent, 1);
            }
        });

        et_keyword = (EditText) findViewById(R.id.edittext_formula_keyword);

        submit = (Button) findViewById(R.id.button_formula_submit);
        submit.setOnClickListener(new View.OnClickListener() {

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
        area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

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
        mFfs=new ArrayList<AgencyVo>();

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
                try {
                    AgencyVo ff = (AgencyVo) parent.getAdapter().getItem(position);
                    if (ff != null && ff.id != null) {
                        Intent intent = new Intent(ActivityAgencyQuery.this, ActivityAgencyDetail.class);
                        intent.putExtra("agency", ff);
                        startActivity(intent);
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
            rps.put("typeFlag", new ServiceHelper().TYPEAGENCY);
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

    private HttpExecuteJson.httpReturnJson mAreasListener = new HttpExecuteJson.httpReturnJson() {

        @Override
        public void onSuccess(String result) {
            // TODO Auto-generated method stub
            EntityDataPageVo edv = null;
            try {
                edv = new Gson().fromJson(result,
                        new TypeToken<EntityDataPageVo>() {
                        }.getType());
                if ((edv != null)
                        && (edv.getErrorCode().equals(CommonResultVo.ERROR_CODE_SUCCESS))) {

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
                    adArs = new ArrayAdapter<AreaVo>(ActivityAgencyQuery.this,
                            android.R.layout.simple_spinner_dropdown_item, areas);
                    area.setAdapter(adArs);
                }else{
                    UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取区域失败" + edv.getErrorMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取区域失败，请联系管理员：" + e.getMessage());
            }
        }

        @Override
        public void onFailure(int error, String msg) {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取区域失败" + msg);
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取区域退出");
        }
    };

    private HttpExecuteJson.httpReturnJson mFormulaTypesListener = new HttpExecuteJson.httpReturnJson() {
        @Override
        public void onFailure(int error, String msg) {
            UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取经销商类型失败" + msg);
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取经销商类型退出");
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
                        && (edv.getErrorCode().equals(CommonResultVo.ERROR_CODE_SUCCESS))) {
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
                            ActivityAgencyQuery.this,
                            android.R.layout.simple_spinner_dropdown_item, formulaTypes);
                    formuType.setAdapter(adFys);
                }else{
                    UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取经销商类型失败" + edv.getErrorMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取经销商类型失败，请联系管理员：" + e.getMessage());
            }
            // UIHelper.ToastMessage(_context,"总条数=" + result + "");
        }
    };

    /*
     * 加载配方
     */
    private void loadFormulas() {
        try {

            HttpExecuteJson http = new HttpExecuteJson(this,
                    mFormulaListener);
            Map<String, Object> rps = new HashMap<String, Object>();
            rps.put("method", "getFeedAgency");
            rps.put("feedId", "1");
            http.get(new ServiceHelper().GETFORMULATYPE, rps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpExecuteJson.httpReturnJson mFormulaListener = new HttpExecuteJson.httpReturnJson() {
        @Override
        public void onFailure(int error, String msg) {
            UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取经销商失败" + msg);
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取经销商退出");
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
                        && (edv.getErrorCode().equals(CommonResultVo.ERROR_CODE_SUCCESS))) {

                    List<AgencyVo> temps1=new ArrayList<AgencyVo>();
                    try{
                        temps1 = JsonUtil.fromJsonArray(
                                JsonUtil.toJson(edv.data),
                                AgencyVo.class);}catch (Exception e){}

                    if(pageno!=1||temps1==null||temps1.size()==0){        // mAdapterMyCreate.notifyDataSetChanged();
                        Toast.makeText(ActivityAgencyQuery.this, "没有更多数据了",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        mFfs.addAll(temps1);
                    }

                    if(isRefreshing){
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mAdapter = new AgencySimpleAdapter(ActivityAgencyQuery.this, mFfs);
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
                    UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取经销商失败" + edv.getErrorMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                // mStrings.addAll(temps);
                UIHelper.ToastMessage(ActivityAgencyQuery.this, "获取经销商失败，请联系管理员：" + e.getMessage());
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
