package com.surekam.pigfeed.tools;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 类: JsonUtil <br>
 * 描述: json <br>
 * 作者: xiangzy <br>
 * 时间: 2015-6-1 下午10:04:15
 */
public class JsonUtil {

	/**
	 *
	 * 方法: fromJsonArray <br>
	 * 描述: json转为list <br>
	 * 作者: ws <br>
	 * 时间: 2015-6-1 下午10:19:44
	 *
	 * @param 被转换的json字符串
	 * @param 转换目标类
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> fromJsonArray(String json, Class<T> clazz)
			throws Exception {
		List<T> lst = new ArrayList<T>();

		JsonArray array = new JsonParser().parse(json).getAsJsonArray();
		for (final JsonElement elem : array) {
			lst.add(new Gson().fromJson(elem, clazz));
		}

		return lst;
	}

	/**
	 *
	 * 方法: toJson <br>
	 * 描述: list转为json字符串 <br>
	 * 作者: ws <br>
	 * 时间: 2015-6-1 下午10:23:08
	 *
	 * @param list
	 * @return
	 */
	public String toJson(List<?> list) {
		Gson gson = new Gson();
		String jsonstring = gson.toJson(list);
		return jsonstring;
	}

	/**
	 * 对象转换成json字符串
	 *
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj);
	}

	/**
	 * json字符串转成对象
	 * 
	 * @param str
	 * @param type
	 * @return
	 */
	public static <T> T fromJson(String str, Type type) {
		Gson gson = new Gson();
		return gson.fromJson(str, type);
	}

}
