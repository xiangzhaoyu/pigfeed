package com.surekam.pigfeed.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private LinearLayout llFeeds;

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
                try{
                    List<FeedVo> targetFeeds=new ArrayList<FeedVo>();
                    for(int i=0;i<llFeeds.getChildCount();i++){
                        try{
                            ImageView select=(ImageView)llFeeds.getChildAt(i).findViewById(R.id.column_select);
                            EditText edit=(EditText)llFeeds.getChildAt(i).findViewById(R.id.et_select_feed_w);
                            FeedVo f=(FeedVo)select.getTag();
                            if(f.sysFlag.equals("xuan")){
                                f.creatorId=Long.parseLong(edit.getText().toString());
                                targetFeeds.add(f);
                            }
                        }catch (Exception e){}
                    }

                    Intent intent=getIntent();
                    intent.putExtra("targetFeeds", (ArrayList<FeedVo>)targetFeeds);
                    setResult(9, intent);
                    finish();
                }catch (Exception e){}
            }
        });

    }

    private void initialView(){
        //mFormulasView=(PullToRefreshListView) findViewById(R.id.formula_pull_down_view);
        //mFormulasView.setMode(PullToRefreshBase.Mode.DISABLED);

        llFeeds=(LinearLayout)findViewById(R.id.ll_select_feeds);
    }

    private View addView(FeedVo f){
        View result=null;
        try{
            if(f!=null){
                LayoutInflater inflater3 = LayoutInflater.from(ActivitySelectSelfFeed.this);
                View view = inflater3.inflate(R.layout.select_feed_item, null);
                TextView feedName=(TextView)view.findViewById(R.id.tv_select_feed_name);
                EditText feedW=(EditText)view.findViewById(R.id.et_select_feed_w);
                final ImageView feedSelect=(ImageView)view.findViewById(R.id.column_select);
                feedSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            FeedVo f=(FeedVo)feedSelect.getTag();
                            if(f!=null&&f.sysFlag.equals("xuan")){
                                f.sysFlag="";
                                feedSelect.setTag(f);
                                feedSelect.setImageResource(R.drawable.phone_no_select);
                            }else{
                                f.sysFlag="xuan";
                                feedSelect.setTag(f);
                                feedSelect.setImageResource(R.drawable.phone_select);
                            }
                        }catch (Exception e){}
                    }
                });
                feedSelect.setTag(f);
                feedName.setText(f.name);
                if(f!=null&&f.sysFlag.equals("xuan")){
                    feedSelect.setImageResource(R.drawable.phone_select);
                }else{
                    feedSelect.setImageResource(R.drawable.phone_no_select);
                }
                feedW.setHint(f.creatorId + "(单位：千克)");//createid存重量

                result=view;
            }
        }catch (Exception e){}
        return result;
    }

    private void initialData(){
        //feedAdapter=new SelectFeedAdapter(ActivitySelectSelfFeed.this,feeds);
        //mFormulasView.setAdapter(feedAdapter);

        if(feeds!=null){
            for(FeedVo f:feeds){
                View v=addView(f);
                if(v!=null){
                    llFeeds.addView(v);
                }
            }
        }
    }
}
