package com.surekam.pigfeed.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.api.HttpExecuteJson;
import com.surekam.pigfeed.api.ServiceHelper;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.AreaVo;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.bean.FeedType;
import com.surekam.pigfeed.bean.FeedVo;
import com.surekam.pigfeed.bean.FormulaArtificialVo;
import com.surekam.pigfeed.bean.NutritionVo;
import com.surekam.pigfeed.tools.JsonUtil;
import com.surekam.pigfeed.ui.adapter.FeedAdapter;
import com.surekam.pigfeed.ui.adapter.NutritionListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangzy_1 on 2015/11/27.
 */
public class ActivityFormulaArtificial extends Activity {

    private View customLiveIndexTitleView;
    private TextView txtTitle;
    private ImageView ivBack;

    private FeedFormulaVo ff;

    private TextView tvFname,tvFContent;
    private ListView lvFormulaAr;

    private List<NutritionVo> nutris;
    private List<FeedVo> feeds;
    private List<FeedVo> feednutrs;//这里creatorid,creatorname存的是系统标志,lastModifierId存的是量


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_formula_artificial_main);
        try {
            ff = (FeedFormulaVo) getIntent().getExtras().get("formula");
        } catch (Exception e) {
            e.printStackTrace();
        }
        initalTitle();
        initialView();
        initialData();
    }

    private void initalTitle() {
        customLiveIndexTitleView = findViewById(R.id.title_formulaquery);
        txtTitle = (TextView) customLiveIndexTitleView
                .findViewById(R.id.title_text_nav);
        txtTitle.setText("智能推荐");

        ivBack = (ImageView) findViewById(R.id.title_back);
        ivBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();

            }

        });

    }

    private void initialView() {
        tvFname=(TextView)findViewById(R.id.tv_formula_name);
        tvFContent=(TextView)findViewById(R.id.tv_formula_content);
        lvFormulaAr=(ListView)findViewById(R.id.lv_formula_artifi);

        if(ff!=null){
            tvFname.setText(ff.name);
            tvFContent.setText(ff.content);
        }
    }

    private void initialData(){
        nutris=new ArrayList<NutritionVo>();
        feeds=new ArrayList<FeedVo>();
        feednutrs=new ArrayList<FeedVo>();

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
            HttpExecuteJson http = new HttpExecuteJson(this, mFeedsListener);
            Map<String, Object> rps = new HashMap<String, Object>();
            rps.put("method", "getFeed");
            rps.put("pageNo", 1);
            rps.put("pageSize", 10000);
            http.get(new ServiceHelper().GETFORMULATYPE, rps);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    智能推荐方法
     */
    private FormulaArtificialVo ArtificialFunction(FeedFormulaVo model){
        FormulaArtificialVo result = new FormulaArtificialVo();
        try{

        }catch (Exception e){}
        return result;
    }

    private HttpExecuteJson.httpReturnJson mFormulaNutritionListener = new HttpExecuteJson.httpReturnJson() {
        @Override
        public void onFailure(int error, String msg) {
            UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取配方营养素失败" + msg);
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取配方营养素退出");
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

                    nutris = JsonUtil.fromJsonArray(
                                JsonUtil.toJson(edv.data),
                                NutritionVo.class);

                }else{
                    UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取配方营养素失败" + edv.getErrorMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取配方营养素失败,请联系管理员：" + e.getMessage());
            }
        }
    };

    private HttpExecuteJson.httpReturnJson mFeedsListener = new HttpExecuteJson.httpReturnJson() {

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

                    feeds = JsonUtil.fromJsonArray(
                                JsonUtil.toJson(edv.data),
                                FeedVo.class);

                    if(feeds!=null&&feeds.size()>0){
                        for (FeedVo n:feeds){

                            try {
                                HttpExecuteJson http = new HttpExecuteJson(ActivityFormulaArtificial.this,
                                        mFeedNutritionListener);
                                Map<String, Object> rps = new HashMap<String, Object>();
                                rps.put("method", "getFeedNutrition");
                                rps.put("feedId", n.id);
                                http.get(new ServiceHelper().GETFORMULATYPE, rps);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                }else{
                    UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取饲料失败" + edv.getErrorMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取饲料失败，请联系管理员：" + e.getMessage());
            }
        }

        @Override
        public void onFailure(int error, String msg) {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取饲料失败" + msg);
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取饲料退出" );
        }
    };

    private HttpExecuteJson.httpReturnJson mFeedNutritionListener = new HttpExecuteJson.httpReturnJson() {
        @Override
        public void onFailure(int error, String msg) {
            UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取饲料营养素失败" + msg);
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取饲料营养素退出");
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
                    for(NutritionVo n:temps1){

                    }
                }else{
                    UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取营养素失败" + edv.getErrorMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                UIHelper.ToastMessage(ActivityFormulaArtificial.this, "获取营养素失败，请联系管理员：" + e.getMessage());
            }
        }
    };


}
