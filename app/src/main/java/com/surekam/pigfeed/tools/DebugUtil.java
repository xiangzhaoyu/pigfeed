package com.surekam.pigfeed.tools;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

import com.surekam.pigfeed.app.AppContext;

public class DebugUtil {
	private static final boolean DEBUG = AppContext.getInstance().debug;

	private final static String ACTIVITY_TAG = "调用界面:";
	private final static String METHOD_TAG = "调用方法";
	private final static String NET_TAG = "返回数据";

	private enum messageType {
		File, String
	};

	public static void printActivityTag(Context context) {
		if (DEBUG) {
			Log.d(ACTIVITY_TAG, context.getClass().toString());
		}
	}

	public static void printMethodTag(String info) {
		if (DEBUG) {
			Log.d(METHOD_TAG, info);
		}
	}

	public static void printNetReturnDataTag(String info) {
		if (DEBUG) {
			Log.d(NET_TAG, info);
		}
	}

	public static void printDebugTag(String tag, String info) {
		if (DEBUG) {
			Log.d(tag, info);
		}
	}

	/***
	 * 将错误信息写入指定的文件
	 * 
	 * @param tag
	 * @param info
	 * @param file
	 */
	public static void printDebugTag(String tag, String info, File file) {
		if (DEBUG) {
			if (!file.exists())
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
	}

	public static boolean isDebug() {
		return DEBUG;
	}

}
