package com.surekam.pigfeed.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.api.HttpExecuteJson;
import com.surekam.pigfeed.api.ServiceHelper;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.AreaVo;
import com.surekam.pigfeed.bean.ArtificialNur;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.bean.FeedType;
import com.surekam.pigfeed.bean.FeedVo;
import com.surekam.pigfeed.bean.FormulaArtificialVo;
import com.surekam.pigfeed.db.PfDb;
import com.surekam.pigfeed.tools.JsonUtil;
import com.surekam.pigfeed.ui.adapter.FeedAdapter;

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
public class ActivityFormulaArtificial2 extends Activity {

    private View customLiveIndexTitleView;
    private TextView txtTitle;
    private ImageView ivBack;

    private static PfDb mPfDb;

    private FeedFormulaVo ff;

    private List<FeedVo> mfs = new ArrayList<FeedVo>();
    private String[] mfsa;
    private boolean[] mfsb;
    private ArrayList<Integer> MultiChoiceID = new ArrayList<Integer>();
    private String feedids;

    private Button mSelect,mSum;
    private ListView lvFormulaAr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_formula_artificial_main1);
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
        mSelect=(Button)findViewById(R.id.bt_ar_select);
        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFormulaArtificial2.this);

                MultiChoiceID.clear();
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("多项选择");
                //  设置多选项
                builder.setMultiChoiceItems(mfsa,
                        mfsb,
                        new DialogInterface.OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
                                // TODO Auto-generated method stub
                                if (arg2) {
                                    MultiChoiceID.add(arg1);
                                    //String tip = "你选择的ID为"+arg1+",值为"+mfsa[arg1];
                                    //Toast toast = Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_SHORT);
                                    //toast.show();
                                }
                                else {
                                    MultiChoiceID.remove((Integer)arg1);
                                }
                            }
                        });
                //  设置确定按钮
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        String str = "";
                        int size = MultiChoiceID.size();
                        for(int i = 0; i < size; i++) {
                            str += (MultiChoiceID.get(i)+",");
                        }
                        //Toast toast = Toast.makeText(getApplicationContext(), "你选择了"+str, Toast.LENGTH_LONG);
                        //toast.show();
                        feedids=str;
                    }
                });
                //  设置取消按钮
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                });

                builder.create().show();
            }
        });

        mSum=(Button)findViewById(R.id.bt_ar_sum);
        mSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excute();
            }
        });

        lvFormulaAr =(ListView)findViewById(R.id.lv_formula_artifi);

    }

    private void initialData(){
        try{
            HttpExecuteJson http = new HttpExecuteJson(this, mFeedsListener);
            Map<String, Object> rps = new HashMap<String, Object>();
            rps.put("method", "getFeed");
            rps.put("pageNo", 1);
            rps.put("pageSize", 10000);
            http.get(new ServiceHelper().GETFORMULATYPE, rps);
        }catch (Exception e){}
    }

    private void excute(){
        try{
            List<ArtificialNur> formulaNur=mPfDb.getFormulaNurs(ff.id+"");
            for(ArtificialNur fn:formulaNur){
                try{
                    if(feedids!=null&&feedids.length()>0){
                        List<Long> maxFs=new ArrayList<Long>();
                        List<Long> minFs=new ArrayList<Long>();
                        List<ArtificialNur> maxfeedNur=mPfDb.getFeedMaxNurs(feedids.substring(0,feedids.length()-1), fn.Cid, fn.UnitNumber);
                        List<ArtificialNur> minfeedNur=mPfDb.getFeedMinNurs(feedids.substring(0,feedids.length()-1),fn.Cid,fn.UnitNumber);
                        if(maxfeedNur!=null&&minfeedNur!=null&&maxfeedNur.size()>0&&minfeedNur.size()>0){
                            for(ArtificialNur a:maxfeedNur){
                                if(!maxFs.contains(a.Mid)){
                                    maxFs.add(a.Mid);
                                }
                            }
                            for(ArtificialNur a:minfeedNur){
                                if(!minFs.contains(a.Mid)){
                                    minFs.add(a.Mid);
                                }
                            }
                            if(maxFs.size()>minFs.size()){
                                Long temp=minFs.get(0);
                                for(int i=0;i<(maxFs.size()-minFs.size());i++){
                                    maxFs.add(temp);
                                }
                            }else{
                                Long temp=maxFs.get(0);
                                for(int i=0;i<(minFs.size()-maxFs.size());i++){
                                    minFs.add(temp);
                                }
                            }
                            int count=maxFs.size();
                            for(int i=0;i<count;i++){

                            }
                        }else{
                            List<ArtificialNur> xx=mPfDb.getMaxFeedNurs(fn.Cid,fn.UnitNumber);
                            String str="";
                            for(ArtificialNur x:xx){
                                str=str+x.Cname+",";
                            }
                            UIHelper.ToastMessage(ActivityFormulaArtificial2.this,"当前选择的饲料不能生成，"+fn.Cname+"含量不够，请选择："+str+"中的一种或几种！");
                        }
                    }else{
                        UIHelper.ToastMessage(ActivityFormulaArtificial2.this,"请选择2种以上饲料");
                    }
                }catch (Exception e){}
            }
            List<FeedVo> selectFeeds=new ArrayList<FeedVo>();
            for(FeedVo f:mfs){
                if(MultiChoiceID.contains(f.id)){
                    try{
                        selectFeeds.add(f);
                        for(ArtificialNur fn:formulaNur){
                            try{
                                List<ArtificialNur> feedNur=mPfDb.getFeedNurs(f.id);

                            }catch (Exception e){}
                        }
                    }catch (Exception e){}
                }
            }

        }catch (Exception e){}
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
                        && (edv.getErrorCode().equals(edv.ERROR_CODE_SUCCESS))) {

                    //List<FeedVo> temps2 = (ArrayList<FeedVo>) edv.data;
                    List<FeedVo> temps1=new ArrayList<FeedVo>();
                    try{
                        temps1 = JsonUtil.fromJsonArray(
                                JsonUtil.toJson(edv.data),
                                FeedVo.class);}catch (Exception e){}

                    if(temps1==null||temps1.size()==0){        // mAdapterMyCreate.notifyDataSetChanged();
                        Toast.makeText(ActivityFormulaArtificial2.this, "没有更多数据了",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        mfs.addAll(temps1);
                        mfsa=new String[mfs.size()];
                        mfsb=new boolean[mfs.size()];
                        for(int i=0;i<mfs.size();i++){
                            mfsa[i]=mfs.get(i).name;
                            mfsb[i]=false;
                        }
                    }

                }else{
                    UIHelper.ToastMessage(ActivityFormulaArtificial2.this, "获取饲料失败" + edv.getErrorMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
                // mStrings.addAll(temps);
                UIHelper.ToastMessage(ActivityFormulaArtificial2.this, "获取饲料失败，请联系管理员：" + e.getMessage());
            }
        }

        @Override
        public void onFailure(int error, String msg) {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityFormulaArtificial2.this, "获取饲料失败" + msg);
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            UIHelper.ToastMessage(ActivityFormulaArtificial2.this, "获取饲料退出" );
        }
    };

}
