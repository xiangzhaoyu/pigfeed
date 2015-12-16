package com.surekam.pigfeed.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.api.HttpExecuteJson;
import com.surekam.pigfeed.api.ServiceHelper;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.ArtificialNur;
import com.surekam.pigfeed.bean.CommonResultVo;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.bean.FeedVo;
import com.surekam.pigfeed.bean.NutritionVo;
import com.surekam.pigfeed.db.PfDb;
import com.surekam.pigfeed.tools.JsonUtil;
import com.surekam.pigfeed.ui.adapter.ArtificialAdapter;
import com.surekam.pigfeed.ui.adapter.ArtificialAdapter3;
import com.surekam.pigfeed.ui.adapter.NutritionListAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangzy_1 on 2015/11/27.
 */
public class ActivityFormulaArtificial3 extends Activity {

    private View customLiveIndexTitleView;
    private TextView txtTitle;
    private ImageView ivBack;
    private TextView tvOpe;

    private static PfDb mPfDb;

    private FeedFormulaVo ff;

    private List<FeedVo> mfsRight=new ArrayList<FeedVo>();
    private List<FeedVo> mfs = new ArrayList<FeedVo>();
    private List<FeedVo> targets=new ArrayList<FeedVo>();
    //private String[] mfsa;
    //private boolean[] mfsb;
    //private ArrayList<Integer> MultiChoiceID = new ArrayList<Integer>();
    private String feedids;

    private Button mSelect,mSum;
    private ListView lvFormulaAr;
    List<ArtificialNur> perFeedNur=new ArrayList<ArtificialNur>();
    List<ArtificialNur> arFeedNur=new ArrayList<ArtificialNur>();
    private ArtificialAdapter3 arFeedAdapter;

    //营养素列表
    private ListView nuListView;
    private List<NutritionVo> listNus=new ArrayList<NutritionVo>();
    private NutritionListAdapter nuAdapter;

    private LinearLayout mf,mfeeds,mnurs,mIntro;
    private TextView mFUsage;
    private ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_formula_artificial_main3);
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
        txtTitle.setText("精准推荐");

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
        tvOpe.setText("计算");
        tvOpe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mf.setVisibility(View.VISIBLE);
                mfeeds.setVisibility(View.VISIBLE);
                mnurs.setVisibility(View.VISIBLE);
                mIntro.setVisibility(View.GONE);

                perFeedNur=new ArrayList<ArtificialNur>();
                arFeedNur=new ArrayList<ArtificialNur>();

                Intent intent=new Intent(ActivityFormulaArtificial3.this,ActivitySelectSelfFeed.class);
                intent.putExtra("feeds", (ArrayList<FeedVo>) mfs);
                startActivityForResult(intent,9);
            }
        });

    }

    private void initialView() {
        mSelect=(Button)findViewById(R.id.bt_ar_select);
        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                perFeedNur=new ArrayList<ArtificialNur>();
                arFeedNur=new ArrayList<ArtificialNur>();

                Intent intent=new Intent(ActivityFormulaArtificial3.this,ActivitySelectSelfFeed.class);
                intent.putExtra("feeds", (ArrayList<FeedVo>) mfs);
                startActivityForResult(intent,9);
            }
        });

        mSum=(Button)findViewById(R.id.bt_ar_sum);
        mSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        lvFormulaAr =(ListView)findViewById(R.id.lv_formula_artifi);

        mf=(LinearLayout)findViewById(R.id.ll_arti_f);
        mfeeds=(LinearLayout)findViewById(R.id.ll_arti_feeds);
        mnurs=(LinearLayout)findViewById(R.id.ll_arti_nutris);
        mIntro=(LinearLayout)findViewById(R.id.ll_arti_intro);

        mFUsage=(TextView)findViewById(R.id.tv_arti_usage);
        if(ff!=null){
            mFUsage.setText(ff.useNum+ff.systemUnitName);
        }

        nuListView=(ListView)findViewById(R.id.listview_feed_nutrition);

        sv=(ScrollView)findViewById(R.id.scroll_arti);
    }

    private void initialData(){
        mPfDb=getmPfDb();
        try{
            HttpExecuteJson http = new HttpExecuteJson(this, mFeedsListener);
            Map<String, Object> rps = new HashMap<String, Object>();
            rps.put("method", "getFeed");
            rps.put("pageNo", 1);
            rps.put("pageSize", 10000);
            http.get(new ServiceHelper().GETFORMULATYPE, rps);
        }catch (Exception e){}

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
    }

    private void excute(){
        try{
            //获取配方所含营养素组成
            List<ArtificialNur> formulaNur=mPfDb.getFormulaNurs(ff.id+"");
            if(formulaNur!=null&&formulaNur.size()>0){
                if(feedids!=null&&feedids.length()>0){
                    //遍历配方含的营养素组成，这里只取第一个
                    for(int f=0;f<1;f++){
                        ArtificialNur fn=formulaNur.get(f);
                        try{
                            //获取在指定饲料中的，营养素大于number跟小于number的，饲料id的列表
                            List<Long> maxFeedIds=mPfDb.getMaxFeedIds(feedids.substring(0,feedids.length()-1),fn.Cid,fn.UnitNumber);
                            List<Long> minFeedIds=mPfDb.getMinFeedIds(feedids.substring(0,feedids.length()-1),fn.Cid,fn.UnitNumber);
                            //存distinct的feedid
                            List<Long> distinctfeedids=new ArrayList<Long>();
                            for(Long id:maxFeedIds){
                                if(!distinctfeedids.contains(id)){
                                    distinctfeedids.add(id);
                                }
                            }
                            for(Long id:minFeedIds){
                                if(!distinctfeedids.contains(id)){
                                    distinctfeedids.add(id);
                                }
                            }
                            //将大于跟小于的饲料列表数目对齐
                            if(maxFeedIds!=null&&maxFeedIds.size()>0){
                                if(minFeedIds!=null&&minFeedIds.size()>0){
                                    if(maxFeedIds.size()>minFeedIds.size()){
                                        Long temp=minFeedIds.get(0);
                                        for(int i=0;i<(maxFeedIds.size()-minFeedIds.size());i++){
                                            minFeedIds.add(temp);
                                        }
                                    }else{
                                        Long temp=maxFeedIds.get(0);
                                        for(int i=0;i<(minFeedIds.size()-maxFeedIds.size());i++){
                                            maxFeedIds.add(temp);
                                        }
                                    }
                                    //拿一个总的feedid列表
                                    List<Long> feeds=new ArrayList<Long>();
                                    for(Long id:maxFeedIds){
                                        feeds.add(id);
                                    }
                                    for (Long id:minFeedIds){
                                        feeds.add(id);
                                    }
                                    //计算比例中间过程，获取差值
                                    //List<ArtificialNur> maxFeedNur=new ArrayList<ArtificialNur>();
                                    //List<ArtificialNur> minFeedNur=new ArrayList<ArtificialNur>();
                                    List<ArtificialNur> feedNur=new ArrayList<ArtificialNur>();
                                    double totalNumber=0;
                                    for(Long id:feeds){
                                        try{
                                        ArtificialNur temp=mPfDb.getAri(id,fn.Cid);
                                        if(temp!=null){
                                            temp.UnitNumber=Math.abs(fn.UnitNumber-temp.UnitNumber);
                                            totalNumber+=temp.UnitNumber;
                                            feedNur.add(temp);
                                        }}catch (Exception e){}
                                    }
                                    //计算比例
                                    perFeedNur=new ArrayList<ArtificialNur>();
                                    for(int i=0;i<feedNur.size();i++){
                                        try{
                                            ArtificialNur temp=(ArtificialNur)feedNur.get(i).clone();
                                            ArtificialNur temp1= feedNur.get(feedNur.size()-i-1);
                                            temp.UnitNumber=temp1.UnitNumber/totalNumber;
                                            perFeedNur.add(temp);
                                        }catch (Exception e){}
                                    }
                                    //合并重复饲料
                                    for(Long id:distinctfeedids){
                                        try{
                                            ArtificialNur tempav=new ArtificialNur();
                                            double temp=0;
                                            for(ArtificialNur av:perFeedNur){
                                                try{
                                                    if(id.equals(av.Mid)){
                                                        tempav=(ArtificialNur)av.clone();
                                                        temp+=av.UnitNumber;
                                                    }
                                                }catch (Exception e){}
                                            }
                                            tempav.UnitNumber=temp;
                                            if(tempav!=null&&tempav.Mid>0){
                                                arFeedNur.add(tempav);
                                            }
                                        }catch (Exception e){}
                                    }
                                    if(arFeedNur!=null&&arFeedNur.size()==0){
                                        ArtificialNur temp=new ArtificialNur();
                                        temp.Mname="无";
                                        temp.UnitNumber=0;
                                        arFeedNur.add(temp);
                                    }
                                    //计算差额
                                    if(arFeedNur!=null){
                                        for(ArtificialNur n:arFeedNur){
                                            try{
                                                double weight=0;
                                                if(targets!=null){
                                                    for(FeedVo t:targets){
                                                        if(t.id.equals(n.Mid)){
                                                            weight=Double.parseDouble(t.creatorId+"");
                                                        }
                                                    }
                                                }
                                                if(weight>0){
                                                    double fw=Double.parseDouble(ff.useNum);
                                                    double d=weight/(fw*n.UnitNumber);
                                                    String ds=new java.text.DecimalFormat("0.000").format(d);
                                                    String i=ds.substring(0, ds.indexOf('.'));
                                                    String id=ds.substring(ds.indexOf('.')+1);
                                                    n.Cname=i+"份，余0."+id+"千克";
                                                }
                                            }catch (Exception e){}
                                        }
                                    }
                                    //绑定数据展示控件
                                    arFeedAdapter = new ArtificialAdapter3(arFeedNur,
                                            getApplicationContext(),
                                            R.layout.item_list_artificial3);
                                    lvFormulaAr.setAdapter(arFeedAdapter);
                                    fixListViewHeight(lvFormulaAr);
                                    // 数据加载完成改变一下scrollview的显示位置
                                    sv.scrollTo(0, 0);
                                    if(mfsRight!=null){
                                        mfs.clear();
                                        for(FeedVo fr:mfsRight){
                                            FeedVo fr1=(FeedVo)fr.clone();
                                            mfs.add(fr1);
                                        }
                                    }
                                }else{
                                    List<ArtificialNur> xx=mPfDb.getMinFeedNurs(fn.Cid,fn.UnitNumber);
                                    if(xx!=null&&xx.size()>0){
                                        String str="";
                                        for(ArtificialNur x:xx){
                                            str=str+x.Mname+",";
                                        }
                                        UIHelper.ToastLongMessage(ActivityFormulaArtificial3.this, "当前选择的饲料：" + fn.Cname + "含量过高，请将" + str + "一种或者几种选入！");
                                    }else{
                                        UIHelper.ToastLongMessage(ActivityFormulaArtificial3.this, "由于配方的" + fn.Cname + "含量过低，数据库无法生成合适的饲料配比！");
                                    }
                                    historySelect();
                                }
                            }else{
                                List<ArtificialNur> xx=mPfDb.getMaxFeedNurs(fn.Cid,fn.UnitNumber);
                                if(xx!=null&&xx.size()>0){
                                    String str="";
                                    for(ArtificialNur x:xx){
                                        str=str+x.Mname+",";
                                    }
                                    UIHelper.ToastLongMessage(ActivityFormulaArtificial3.this, "当前选择的饲料：" + fn.Cname + "含量过低，请将" + str + "一种或者几种选入！");
                                }else{
                                    UIHelper.ToastLongMessage(ActivityFormulaArtificial3.this, "由于配方的" + fn.Cname + "含量过高，数据库无法生成合适的饲料配比！");
                                }
                                historySelect();
                            }
                        }catch (Exception e){}
                    }
                }else{
                    historySelect();
                    UIHelper.ToastLongMessage(ActivityFormulaArtificial3.this, "请选择两种以上的饲料！");
                }
            }else{
                UIHelper.ToastLongMessage(ActivityFormulaArtificial3.this, "没有获取到该配方的营养素组成情况，请重新选择配方！");
            }
        }catch (Exception e){
            UIHelper.ToastLongMessage(ActivityFormulaArtificial3.this, "智能计算出现问题，请联系管理员：" + e.getMessage());
        }
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
            listViewItem.measure(View.MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().widthPixels, View.MeasureSpec.EXACTLY), 0);
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

    public synchronized PfDb getmPfDb() {
        if (mPfDb == null)
            mPfDb = openPfDB();
        return mPfDb;
    }

    private PfDb openPfDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + "com.surekam.pigfeed" + File.separator
                + PfDb.PIGFEED_DB_NAME;
        File db = new File(path);
        if (!db.exists()) {
            // L.i("db is not exists");
            try {
                InputStream is = getApplication().getAssets().open("pigfeed.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();

                System.exit(0);
            }
        }
        return new PfDb(this, path);
    }

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
                        && (edv.getErrorCode().equals(CommonResultVo.ERROR_CODE_SUCCESS))) {

                    //List<FeedVo> temps2 = (ArrayList<FeedVo>) edv.data;
                    List<FeedVo> temps1=new ArrayList<FeedVo>();
                    try{
                        temps1 = JsonUtil.fromJsonArray(
                                JsonUtil.toJson(edv.data),
                                FeedVo.class);}catch (Exception e){}

                    if(temps1==null||temps1.size()==0){        // mAdapterMyCreate.notifyDataSetChanged();
                        Toast.makeText(ActivityFormulaArtificial3.this, "没有更多数据了",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        mfs.addAll(temps1);
                        if(mfs!=null){
                            mfsRight.clear();
                            for(FeedVo f:mfs){
                                FeedVo f1=(FeedVo)f.clone();
                                mfsRight.add(f1);
                            }
                        }
                    }

                }else{
                    UIHelper.ToastLongMessage(ActivityFormulaArtificial3.this, "获取饲料失败" + edv.getErrorMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                // mStrings.addAll(temps);
                UIHelper.ToastLongMessage(ActivityFormulaArtificial3.this, "获取饲料失败，请联系管理员：" + e.getMessage());
            }
        }

        @Override
        public void onFailure(int error, String msg) {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityFormulaArtificial3.this, "获取饲料失败" + msg);
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityFormulaArtificial3.this, "获取饲料退出" );
        }
    };

    private HttpExecuteJson.httpReturnJson mFormulaNutritionListener = new HttpExecuteJson.httpReturnJson() {
        @Override
        public void onFailure(int error, String msg) {
            UIHelper.ToastMessage(ActivityFormulaArtificial3.this, "获取配方营养素失败" + msg);
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityFormulaArtificial3.this, "获取配方营养素退出");
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
                    UIHelper.ToastMessage(ActivityFormulaArtificial3.this, "获取配方营养素失败" + edv.getErrorMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                UIHelper.ToastMessage(ActivityFormulaArtificial3.this, "获取配方营养素失败,请联系管理员：" + e.getMessage());
            }
            // UIHelper.ToastMessage(_context,"总条数=" + result + "");
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 9:{
                try{
                    targets=(List<FeedVo>)data.getExtras().get("targetFeeds");
                    feedids="";
                    if(targets!=null){
                        for(FeedVo f:targets){
                            try{
                                if(f!=null&&f.id>0){
                                    feedids += (f.id+",");
                                }
                            }catch (Exception e){}
                        }
                        excute();
                    }
                }catch (Exception e){}}
                break;

        }
    }

    public void historySelect(){
        try{
            if(mfs!=null&&targets!=null){
                for(FeedVo f:targets){
                    for(FeedVo n:mfs){
                        try{
                            if(n.id.equals(f.id)){
                                n.creatorId=f.creatorId;
                                n.sysFlag=f.sysFlag;
                            }
                        }catch (Exception e){}
                    }
                }
            }
        }catch (Exception e){}
    }

}
