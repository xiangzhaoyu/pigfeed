package com.surekam.pigfeed.db;

import java.util.ArrayList;
import java.util.List;

import com.surekam.pigfeed.bean.City;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;


public class CityDb {
	public static final String CITY_DB_NAME = "city.db";
	private static final String CITY_TABLE_NAME = "city";
	private static SQLiteDatabase db;

	public CityDb(Context context, String path) {
		if(db==null)
			db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
	}

	public List<City> getAllCity() {
		List<City> list = new ArrayList<City>();
		Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME, null);
		while (c.moveToNext()) {
			String province = c.getString(c.getColumnIndex("province"));
			String city = c.getString(c.getColumnIndex("city"));
			String number = c.getString(c.getColumnIndex("number"));
			String allPY = c.getString(c.getColumnIndex("allpy"));
			String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
			String firstPY = c.getString(c.getColumnIndex("firstpy"));
			City item = new City(province, city, number, firstPY, allPY,
					allFirstPY);
			list.add(item);
		}
		return list;
	}

	public City getCity(String city) {
		if (TextUtils.isEmpty(city))
			return null;
		City item = getCityInfo(parseName(city));
		if (item == null) {
			item = getCityInfo(city);
		}
		return item;
	}

	/**
	 * 去掉市或县搜索
	 * 
	 * @param city
	 * @return
	 */
	private String parseName(String city) {
		if (city.contains("市")) {// 如果为空就去掉市字再试试
			String subStr[] = city.split("市");
			city = subStr[0];
		} else if (city.contains("县")) {// 或者去掉县字再试试
			String subStr[] = city.split("县");
			city = subStr[0];
		}
		return city;
	}

	private City getCityInfo(String city) {
		Cursor c = db.rawQuery("SELECT * from " + CITY_TABLE_NAME
				+ " where city=?", new String[] { city });
		if (c.moveToFirst()) {
			String province = c.getString(c.getColumnIndex("province"));
			String name = c.getString(c.getColumnIndex("city"));
			String number = c.getString(c.getColumnIndex("number"));
			String allPY = c.getString(c.getColumnIndex("allpy"));
			String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
			String firstPY = c.getString(c.getColumnIndex("firstpy"));
			City item = new City(province, name, number, firstPY, allPY,
					allFirstPY);
			return item;
		}
		return null;
	}

	public void closeCursor(Cursor c) {
		if (c != null && c.isClosed() == false) {
			c.close();
		}
	}

	// 获取一个城市的下级市县
	public Bundle getSubLocations(String location) {
		Bundle bundle = new Bundle();
		if (db == null) {
			bundle.putString("error", "未找到数据库");
			return bundle;
		}
		ArrayList<String> codes_list = new ArrayList<String>();
		ArrayList<String> values_list = new ArrayList<String>();
		String location_code = "";
		Cursor c = null;
		try {
			c = db.query("first_level", null, "value=?",
					new String[] { location }, null, null, null);
			int size = c.getCount();
			if (size > 0) {
				c.move(1);
				location_code = c.getString(0);
				c = db.query("second_level", null, "code like '"
						+ location_code + "__'", null, null, null, null);
				size = c.getCount();
				for (int i = 0; i < size; i++) {
					c.move(1);
					codes_list.add(c.getString(0));
					values_list.add(c.getString(1));
				}
				closeCursor(c);
				bundle.putStringArrayList("codes_list", codes_list);
				bundle.putStringArrayList("values_list", values_list);
				bundle.putString("error", "success");
				return bundle;
			}

			c = db.query("second_level", null, "value=?",
					new String[] { location }, null, null, null);
			size = c.getCount();
			if (size > 0) {
				c.move(1);
				location_code = c.getString(0);
				c = db.query("third_level", null, "code like '" + location_code
						+ "__'", null, null, null, null);
				size = c.getCount();
				for (int i = 0; i < size; i++) {
					c.move(1);
					codes_list.add(c.getString(0));
					values_list.add(c.getString(1));
				}
				closeCursor(c);
				bundle.putStringArrayList("codes_list", codes_list);
				bundle.putStringArrayList("values_list", values_list);
				bundle.putString("error", "success");
				return bundle;
			}

			c = db.query("third_level", null, "value=?",
					new String[] { location }, null, null, null);
			size = c.getCount();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					c.move(1);
					codes_list.add(c.getString(0));
					values_list.add(c.getString(1));
				}
				closeCursor(c);
				bundle.putStringArrayList("codes_list", codes_list);
				bundle.putStringArrayList("values_list", values_list);
				bundle.putString("error", "success");
				return bundle;
			}

		} catch (Exception e) {
			bundle.putString("error", e == null ? "不明原因" : e.getMessage());
			return bundle;
		} finally {
			closeCursor(c);
		}
		bundle.putString("error", "不明错误");
		return bundle;
	}

}
