package com.surekam.pigfeed.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.FeedFormulaVo;

/**
 * Created by xiangzy_1 on 2015/11/27.
 */
public class ActivityFormulaArtificial extends Activity {

    private View customLiveIndexTitleView;
    private TextView txtTitle;
    private ImageView ivBack;

    private FeedFormulaVo ff;

    private TextView tvFname;
    private ListView lvFormulaAr;


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
        lvFormulaAr=(ListView)findViewById(R.id.lv_formula_artifi);

        if(ff!=null){
            tvFname.setText(ff.name);
        }
    }

}
