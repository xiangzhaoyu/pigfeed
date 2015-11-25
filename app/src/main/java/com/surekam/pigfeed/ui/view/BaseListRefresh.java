package com.surekam.pigfeed.ui.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.surekam.pigfeed.ui.view.DropDownListView1.OnDropDownListener;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;


public class BaseListRefresh<T> {
	Context _context;
	List<T> listDatas = new ArrayList<T>();
	int pageIndex = 1;
	int pageSize = 10;

	DropDownListView1 listView;
	BaseAdapter _adapter;

	boolean _ibIsFirstRun = false;

	public BaseListRefresh(Context context, DropDownListView1 list) {
		// TODO Auto-generated constructor stub
		_context = context;

		list.setOnDropDownListener(new OnDropDownListener() {

			@Override
			public void onDropDown() {
				// TODO Auto-generated method stub
				pageIndex = 1;
				listView.setHasMore(true);
				getListViewData();
			}
		});

		list.setOnBottomListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				pageIndex++;
				getListViewData();
			}
		});

		this.listView = list;
	}

	public void clear() {
		listDatas = new ArrayList<T>();
		pageIndex = 1;
	}

	public void getListViewData() {
		if (pageIndex == 1) {
			listView.setShowFooterWhenNoMore(false);
		}
	}

	public void setListViewItemClickedListener(OnItemClickListener listener) {
		listView.setOnItemClickListener(listener);
	}
	
	public void setFirstRun(boolean run){
		_ibIsFirstRun = run;
	}

	public void refreshData(List<T> data) {
		int size = 0;

		if (pageIndex == 1) {
			listDatas.clear();
		}

		if (data != null) {
			listDatas.addAll(data);
			size = data != null ? data.size() : 0;
		}

		refreshUI(size);

	}

	private void refreshUI(int count) {
		// _progress.dimiss();
		listView.setVisibility(View.VISIBLE);
		listView.setShowFooterWhenNoMore(true);

		// if (listView.getVisibility() == View.GONE) {
		// listView.setVisibility(View.VISIBLE);
		// }

		if (count < pageSize) {
			listView.setHasMore(false);
		} else {
			listView.setHasMore(true);
		}
		listView.onBottomComplete();

		if (pageIndex == 1) {

			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
			String refreshText = "上次更新时间" + dateFormat.format(new Date());

			listView.onDropDownComplete(refreshText);
		}

		_adapter.notifyDataSetChanged();

	}

	public List<T> getListDatas() {
		return listDatas;
	}

	public void setListDatas(List<T> listDatas) {
		this.listDatas = listDatas;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public DropDownListView1 getListView() {
		return listView;
	}

	public void setListView(DropDownListView1 listView) {
		this.listView = listView;
	}

	public BaseAdapter getAdapter() {
		return _adapter;
	}

	public void setAdapter(BaseAdapter adapter) {
		this._adapter = adapter;
		listView.setAdapter(adapter);
	}

}
