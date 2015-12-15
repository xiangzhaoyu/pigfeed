package com.surekam.pigfeed.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.ArtificialNur;
import com.surekam.pigfeed.bean.FeedFormulaVo;
import com.surekam.pigfeed.bean.FeedVo;
import com.surekam.pigfeed.ui.adapter.SelectFeedAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangzy_1 on 2015/12/15.
 */
public class ActivitySelectSelfFeed extends Activity {

    private View customLiveIndexTitleView;
    private TextView txtTitle;
    private ImageView ivBack;
    private TextView tvOpe;

    private List<FeedVo> feeds=new ArrayList<FeedVo>();
    SelectFeedAdapter feedAdapter;
    private PullToRefreshListView mFormulasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_feed);

        try{
            feeds=(List<FeedVo>)getIntent().getExtras().get("feeds");
            if(feeds!=null){
                for(FeedVo f:feeds){
                    f.creatorId=0L;
                }
            }
        }catch (Exception e){}

        initalTitle();
        initialView();
        initialData();
    }

    private void initalTitle() {
        customLiveIndexTitleView = findViewById(R.id.title_formulaquery);
        txtTitle = (TextView) customLiveIndexTitleView
                .findViewById(R.id.title_text_nav);
        txtTitle.setText("选择饲料");

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
        tvOpe.setText("确定");
        tvOpe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(9);
                finish();
            }
        });

    }

    private void initialView(){
        mFormulasView=(PullToRefreshListView) findViewById(R.id.formula_pull_down_view);
        mFormulasView.setMode(PullToRefreshBase.Mode.DISABLED);


    }

    private void initialData(){
        feedAdapter=new SelectFeedAdapter(ActivitySelectSelfFeed.this,feeds);
        mFormulasView.setAdapter(feedAdapter);
    }
}
