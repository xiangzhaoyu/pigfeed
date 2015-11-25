package com.surekam.pigfeed.ui.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.surekam.pigfeed.R;
import com.surekam.pigfeed.bean.City;
import com.surekam.pigfeed.common.BaseFragment;
import com.surekam.pigfeed.db.CityDb;
import com.surekam.pigfeed.ui.activity.ActivityQuery;
import com.surekam.pigfeed.ui.adapter.CityAdapter;
import com.surekam.pigfeed.ui.adapter.SearchCityAdapter;
import com.surekam.pigfeed.ui.view.BladeView;
import com.surekam.pigfeed.ui.view.PinnedHeaderListView;

public class FragmentSelectCitys extends BaseFragment implements TextWatcher,
OnClickListener {
	
	private EditText mSearchEditText;
	// private Button mCancelSearchBtn;
	private ImageButton mClearSearchBtn;
	private View mCityContainer;
	private View mSearchContainer;
	private PinnedHeaderListView mCityListView;
	private BladeView mLetter;
	private ListView mSearchListView;
	
	private static final String FORMAT = "^[a-z,A-Z].*$";
	private List<City> mCities;
	private SearchCityAdapter mSearchCityAdapter;
	private CityAdapter mCityAdapter;
	// 首字母集
	private List<String> mSections;
	// 根据首字母存放数据
	private Map<String, List<City>> mMap;
	// 首字母位置集
	private List<Integer> mPositions;
	// 首字母对应的位置
	private Map<String, Integer> mIndexer;
	private CityDb mCityDB;
	private InputMethodManager mInputMethodManager;

	private TextView mTitleTextView;
	private ImageView mBackBtn;
	private ProgressBar mTitleProgressBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		_view = inflater.inflate(R.layout.fragment_select_citys, null);
		_context = getActivity();
		initView();
		initData();
		return _view;
	}
	

	private void initView() {
		mTitleTextView = (TextView) _view.findViewById(R.id.title_name);
		mBackBtn = (ImageView) _view.findViewById(R.id.title_back);
		mBackBtn.setOnClickListener(this);
		mTitleProgressBar = (ProgressBar) _view.findViewById(R.id.title_update_progress);
		mTitleProgressBar.setVisibility(View.VISIBLE);
		
		mSearchEditText = (EditText) _view.findViewById(R.id.search_edit);
		mSearchEditText.addTextChangedListener(this);
		mClearSearchBtn = (ImageButton) _view.findViewById(R.id.ib_clear_text);
		mClearSearchBtn.setOnClickListener(this);

		mCityContainer = _view.findViewById(R.id.city_content_container);
		mSearchContainer = _view.findViewById(R.id.search_content_container);
		mCityListView = (PinnedHeaderListView) _view.findViewById(R.id.citys_list);
		mCityListView.setEmptyView(_view.findViewById(R.id.citys_list_empty));
		mLetter = (BladeView) _view.findViewById(R.id.citys_bladeview);
		mLetter.setOnItemClickListener(new BladeView.OnItemClickListener() {

			@Override
			public void onItemClick(String s) {
				if (mIndexer.get(s) != null) {
					mCityListView.setSelection(mIndexer.get(s));
				}
			}
		});
		mLetter.setVisibility(View.GONE);
		mSearchListView = (ListView) _view.findViewById(R.id.search_list);
		mSearchListView.setEmptyView(_view.findViewById(R.id.search_empty));
		mSearchContainer.setVisibility(View.GONE);
		mSearchListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mInputMethodManager.hideSoftInputFromWindow(
						mSearchEditText.getWindowToken(), 0);
				return false;
			}
		});
		mCityListView
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						
						startActivity(mCityAdapter.getItem(position));
						//finish();
					}
				});

		mSearchListView
				.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						
						startActivity(mSearchCityAdapter.getItem(position));
						//finish();
					}
				});
	}

	private void startActivity(City city) {
		Intent i = new Intent();
		i.putExtra("city", city);
		//setResult(RESULT_OK, i);
		//finish();
	}

	private void initData() {
		mCities = new ArrayList<City>();
		mSections = new ArrayList<String>();
		mMap = new HashMap<String, List<City>>();
		mPositions = new ArrayList<Integer>();
		mIndexer = new HashMap<String, Integer>();
		
		mCityDB =getCityDB();
		mInputMethodManager = (InputMethodManager) ((ActivityQuery)_context).getSystemService("input_method");

		prepareCityList();
		mCityAdapter = new CityAdapter((ActivityQuery)_context, mCities,
					mMap, mSections, mPositions);
			mCityListView.setAdapter(mCityAdapter);
			mCityListView.setOnScrollListener(mCityAdapter);
			mCityListView.setPinnedHeaderView(LayoutInflater.from(
					(ActivityQuery)_context).inflate(
					R.layout.biz_plugin_weather_list_group_item, mCityListView,
					false));
			mTitleProgressBar.setVisibility(View.GONE);
			mLetter.setVisibility(View.VISIBLE);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// do nothing
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mSearchCityAdapter = new SearchCityAdapter((ActivityQuery)_context,
				mCities);
		mSearchListView.setAdapter(mSearchCityAdapter);
		mSearchListView.setTextFilterEnabled(true);
		if (mCities.size() < 1 || TextUtils.isEmpty(s)) {
			mCityContainer.setVisibility(View.VISIBLE);
			mSearchContainer.setVisibility(View.INVISIBLE);
			mClearSearchBtn.setVisibility(View.GONE);
		} else {
			mClearSearchBtn.setVisibility(View.VISIBLE);
			mCityContainer.setVisibility(View.INVISIBLE);
			mSearchContainer.setVisibility(View.VISIBLE);
			mSearchCityAdapter.getFilter().filter(s);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// 如何搜索字符串长度为0，是否隐藏输入法
		// if(TextUtils.isEmpty(s)){
		// mInputMethodManager.hideSoftInputFromWindow(
		// mSearchEditText.getWindowToken(), 0);
		// }

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_clear_text:
			if (!TextUtils.isEmpty(mSearchEditText.getText().toString())) {
				mSearchEditText.setText("");
				mInputMethodManager.hideSoftInputFromWindow(
						mSearchEditText.getWindowToken(), 0);
			}
			break;
		case R.id.title_back:
			//finish();
			break;
		default:
			break;
		}
	}

	public synchronized CityDb getCityDB() {
		if (mCityDB == null)
			mCityDB = openCityDB();
		return mCityDB;
	}
	
	private CityDb openCityDB() {
		String path = "/data"
				+ Environment.getDataDirectory().getAbsolutePath()
				+ File.separator + "com.surekam.pigfeed" + File.separator
				+ CityDb.CITY_DB_NAME;
		File db = new File(path);
		if (!db.exists()) {
			// L.i("db is not exists");
			try {
				InputStream is = ((ActivityQuery)_context).getApplication().getAssets().open("city.db");
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
		return new CityDb((ActivityQuery)_context, path);
	}

	private boolean prepareCityList() {
		mCities = mCityDB.getAllCity();// 获取数据库中所有城市
		for (City city : mCities) {
			String firstName = city.getFirstPY();// 第一个字拼音的第一个字母
			if (firstName.matches(FORMAT)) {
				if (mSections.contains(firstName)) {
					mMap.get(firstName).add(city);
				} else {
					mSections.add(firstName);
					List<City> list = new ArrayList<City>();
					list.add(city);
					mMap.put(firstName, list);
				}
			} else {
				if (mSections.contains("#")) {
					mMap.get("#").add(city);
				} else {
					mSections.add("#");
					List<City> list = new ArrayList<City>();
					list.add(city);
					mMap.put("#", list);
				}
			}
		}
		Collections.sort(mSections);// 按照字母重新排序
		int position = 0;
		for (int i = 0; i < mSections.size(); i++) {
			mIndexer.put(mSections.get(i), position);// 存入map中，key为首字母字符串，value为首字母在listview中位置
			mPositions.add(position);// 首字母在listview中位置，存入list中
			position += mMap.get(mSections.get(i)).size();// 计算下一个首字母在listview的位置
		}
		return true;
	}

}
