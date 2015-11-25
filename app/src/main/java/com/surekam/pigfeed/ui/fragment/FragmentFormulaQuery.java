package com.surekam.pigfeed.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.common.BaseFragment;
import com.surekam.pigfeed.ui.activity.ActivityMain;
import com.surekam.pigfeed.ui.activity.ActivityQuery;
import com.surekam.pigfeed.ui.adapter.MyPagerAdapter;
import com.surekam.pigfeed.ui.view.PullDownView;
import com.surekam.pigfeed.ui.view.PullDownView.OnPullDownListener;

public class FragmentFormulaQuery  extends BaseFragment implements OnClickListener {

	private View customLiveIndexTitleView;
	private TextView txtTitle;
	private ImageView ivBack;
	
	private Button submit;
	private EditText area;
	
	private static final int WHAT_DID_LOAD_DATA = 0;
    private static final int WHAT_DID_REFRESH = 1;
    private static final int WHAT_DID_MORE = 2;
 
    private PullDownView mPullDownView;
    private List<String> mStrings = new ArrayList<String>();
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    
 // 模拟数据
    private String[] mStringArray = {
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
            "Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese"
    };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		_view = inflater.inflate(R.layout.fragment_formula_query_main, null);
		_context = getActivity();
		initalTitle();
		initialView();
		return _view;
	}
	
	private Handler mUIHandler = new Handler(){
		 
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DID_LOAD_DATA:{
                    if(msg.obj != null){
                        List<String> strings = (List<String>) msg.obj;
                        if(!strings.isEmpty()){
                            mStrings.addAll(strings);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    // 诉它数据加载完毕;
                    mPullDownView.notifyDidLoad();
                    break;
                }
                case WHAT_DID_REFRESH :{
                    String body = (String) msg.obj;
                    mStrings.add(0, body);
                    mAdapter.notifyDataSetChanged();
                    // 告诉它更新完毕
                    mPullDownView.notifyDidRefresh();
                    break;
                }
 
                case WHAT_DID_MORE:{
                    String body = (String) msg.obj;
                    mStrings.add(body);
                    mAdapter.notifyDataSetChanged();
                    // 告诉它获取更多完毕
                    mPullDownView.notifyDidMore();
                    break;
                }
            }
 
        }
 
    };
	
	private void initalTitle(){
		customLiveIndexTitleView = _view.findViewById(R.id.title_formulaquery);
		txtTitle = (TextView) customLiveIndexTitleView
				.findViewById(R.id.title_text_nav);
		txtTitle.setText("配方查询");
		
		ivBack = (ImageView) _view.findViewById(R.id.title_back);
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((ActivityQuery)_context).finish();

			}

		});

		/*
         * 1.使用PullDownView
         * 2.设置OnPullDownListener
         * 3.从mPullDownView里面获取ListView
         */
        mPullDownView = (PullDownView) _view.findViewById(R.id.formula_pull_down_view);
        //mPullDownView.setOnPullDownListener(this);
        mPullDownView.setOnPullDownListener(new OnPullDownListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					 
		            @Override
		            public void run() {
		                try {
		                    Thread.sleep(2000);
		                } catch (InterruptedException e) {
		                    e.printStackTrace();
		                }
		                Message msg = mUIHandler.obtainMessage(WHAT_DID_REFRESH);
		                msg.obj = "After refresh " + System.currentTimeMillis();
		                msg.sendToTarget();
		            }
		        }).start();
			}

			@Override
			public void onMore() {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					 
		            @Override
		            public void run() {
		                try {
		                    Thread.sleep(2000);
		                } catch (InterruptedException e) {
		                    e.printStackTrace();
		                }
		                Message msg = mUIHandler.obtainMessage(WHAT_DID_MORE);
		                msg.obj = "After more " + System.currentTimeMillis();
		                msg.sendToTarget();
		            }
		        }).start();
			}});
        mListView = mPullDownView.getListView();
 
        //mListView.setOnItemClickListener(this);
        mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText((ActivityQuery)_context, "啊，你点中我了 " + position, Toast.LENGTH_SHORT).show();
			}});
        mAdapter = new ArrayAdapter<String>((ActivityQuery)_context, R.layout.view_pulldown_item, mStrings);
        mListView.setAdapter(mAdapter);
 
        mPullDownView.enableAutoFetchMore(true, 1);
 
        loadData();

	}
		
	private void initialView(){
		area=(EditText)_view.findViewById(R.id.edittext_formula_area);
		area.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				
			}});
		
		submit=(Button)_view.findViewById(R.id.button_formula_submit);
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}});
	}
	
	private void loadData(){
        new Thread(new Runnable() {
 
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<String> strings = new ArrayList<String>();
                for (String body : mStringArray) {
                    strings.add(body);
                }
                Message msg = mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA);
                msg.obj = strings;
                msg.sendToTarget();
            }
        }).start();
    }

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
