package com.surekam.pigfeed.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.api.HttpExecuteJson;
import com.surekam.pigfeed.api.ServiceHelper;
import com.surekam.pigfeed.app.UIHelper;
import com.surekam.pigfeed.bean.ArtificialNur;
import com.surekam.pigfeed.bean.EntityDataPageVo;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.bean.FeedVo;
import com.surekam.pigfeed.bean.FormulaArtificialVo;
import com.surekam.pigfeed.bean.NutritionVo;
import com.surekam.pigfeed.db.CityDb;
import com.surekam.pigfeed.db.PfDb;
import com.surekam.pigfeed.tools.JsonUtil;

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
public class ActivityFormulaArtificial1 extends Activity {

    private View customLiveIndexTitleView;
    private TextView txtTitle;
    private ImageView ivBack;

    private static PfDb mPfDb;

    private FeedFormulaVo ff;

    private TextView tvFname,tvFContent;
    private ListView lvFormulaAr;
    private LinearLayout mListShow,mPbShow;
    private ProgressBar mpb;


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
        mListShow=(LinearLayout)findViewById(R.id.ll_list_ar);
        mPbShow=(LinearLayout)findViewById(R.id.ll_pb_ar);
        mpb=(ProgressBar)findViewById(R.id.pb_sum_ar);

        if(ff!=null){
            tvFname.setText(ff.name);
            tvFContent.setText(ff.content);
        }
    }

    private void initialData(){

    }

    /*
    智能推荐方法
     */
    private List<FormulaArtificialVo> ArtificialFunction(FeedFormulaVo model){
        List<FormulaArtificialVo> result = new ArrayList<FormulaArtificialVo>();
        try{

            mPfDb=getmPfDb();
            List<ArtificialNur> formulaNri=mPfDb.getFormulaNurs(ff.id+"");
            if(formulaNri!=null){
                mpb.setMax(formulaNri.size());
                for(int i=0;i>formulaNri.size();i++){
                    mpb.setProgress(i);
                    try{
                        List<ArtificialNur> maxFeedNri=mPfDb.getMaxFeedNurs(formulaNri.get(i).Cid,formulaNri.get(i).UnitNumber);
                        List<ArtificialNur> minFeedNri=mPfDb.getMinFeedNurs(formulaNri.get(i).Cid,formulaNri.get(i).UnitNumber);
                        if(maxFeedNri!=null&&minFeedNri!=null&&maxFeedNri.size()>=1&&minFeedNri.size()>=1){
                            int maxCount=1,minCount=1,total=1;
                            if(maxFeedNri.size()>maxCount){
                                maxCount=maxFeedNri.size();
                            }
                            if(minFeedNri.size()>minCount){
                                minCount=minFeedNri.size();
                            }
                            if(minCount>maxCount){
                                total=minCount;
                            }else {
                                total=maxCount;
                            }
                            //计算上下各一个
                            if(total>0){
                                ArtificialNur maNu;
                                ArtificialNur miNu;
                                for(int ma=1;ma<=maxCount;ma++){
                                    maNu=maxFeedNri.get(ma);
                                    for (int mi=1;mi<minCount;mi++){
                                        miNu=minFeedNri.get(mi);
                                        double upvalue=maNu.UnitNumber-formulaNri.get(i).UnitNumber;
                                        double downvalue=formulaNri.get(i).UnitNumber-miNu.UnitNumber;
                                        double maPer=upvalue/(upvalue+downvalue);
                                        double miPer=downvalue/(upvalue+downvalue);
                                        if(maPer>0&&miPer>0){
                                            FormulaArtificialVo fav=new FormulaArtificialVo();
                                            fav.ArtiFeeds=new ArrayList<FeedVo>();
                                            fav.ArtiName=formulaNri.get(i).Mname;
                                            FeedVo maf=new FeedVo();
                                            maf.id=maNu.Mid;
                                            maf.name=maNu.Mname;
                                            maf.lastModifierId=(long)maPer;
                                            fav.ArtiFeeds.add(maf);
                                            FeedVo mif=new FeedVo();
                                            mif.id=miNu.Mid;
                                            mif.name=miNu.Mname;
                                            mif.lastModifierId=(long)miPer;

                                        }
                                    }
                                }
                            }
                            //计算上下各2个
                            if(total>1){}
                            //计算上下各3个
                            if(total>2){}
                            //计算上下各4个
                            if(total>3){}
                        }
                    }catch (Exception e){}
                }
            }
        }catch (Exception e){}
        return result;
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


}
