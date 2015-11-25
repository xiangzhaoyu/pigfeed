package com.surekam.pigfeed.api;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.http.entity.StringEntity;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.surekam.pigfeed.tools.DebugUtil;
import com.surekam.pigfeed.ui.view.MyProgressDialog;

public class HttpExecute<T> {
	Context _context;
	httpReturn<T> _listener;
	HttpUtils http;
	Type _type;

	boolean _showDialog = true;
	MyProgressDialog _progressDialog;
	String _progressText = "正在获取数据...";

	public interface httpReturn<T> {

		public void onSuccess(T result);

		public void onFailure(int error, String msg);

		public void onCancel();
	}

	public HttpExecute(Context context, httpReturn<T> listener) {
		// TODO Auto-generated constructor stub
		_context = context;
		_listener = listener;
		http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(0);
		http.configCurrentHttpCacheExpiry(0);

	}

	public void setListner(httpReturn<T> listener) {
		_listener = listener;
	}

	public void setDialogShow(boolean isShow) {
		_showDialog = isShow;
	}

	public void setProgressText(String text) {
		_progressText = text;
	}

	public void setType(Type type) {
		_type = type;
	}

	public void get(String url, Map<String, Object> params) {
		String newUrl = url;
		if (params != null) {
			newUrl = MakeURL(url, params);
		}
		http.send(HttpRequest.HttpMethod.GET, newUrl, _returnListner);
	}

	public void post(String url, RequestParams params) {
		http.send(HttpRequest.HttpMethod.POST, url, params, _returnListner);
	}

	public void delete(String url, RequestParams params) {
		http.send(HttpRequest.HttpMethod.DELETE, url, params, _returnListner);
	}

	public void Create(String url, RequestParams params) {
		http.send(HttpRequest.HttpMethod.PUT, url, params, _returnListner);
	}

	public void update(String url, String stringEntity) {
		RequestParams params = new RequestParams();
		params.addHeader("Content-Type", "application/json");
		try {
			params.setBodyEntity(new StringEntity(stringEntity, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		http.send(HttpRequest.HttpMethod.PUT, url, params, _returnListner);
	}

	RequestCallBack<String> _returnListner = new RequestCallBack<String>() {
		public void onStart() {
			if (_showDialog) {
				if (_progressDialog == null) {
					_progressDialog = new MyProgressDialog(_context);
				}

				_progressDialog.show(_progressText, false);
			}

		};

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub

			if (_progressDialog != null) {
				_progressDialog.dimiss();
			}

			String result = arg0.result + "";
			DebugUtil.printDebugTag("HttpExe", result + "");
			if (result.length() == 0) {
				_listener.onSuccess(null);
			} else {
				analyzeString(result);
			}
		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			if (_progressDialog != null) {
				_progressDialog.dimiss();
			}

			_listener.onFailure(arg0.getExceptionCode(),
					arg1 + arg0.getMessage());
		}
	};

	// 解析Json语句
	private void analyzeString(String json) {
		T rt = null;
		String newJson = json;

		if (_type == null) {
			_type = new TypeToken<T>() {
			}.getType();
		}
		// 解析类型
		try {
			rt = new Gson().fromJson(newJson, _type);
		} catch (Exception e) {
			// 类型转换错误异常处理
			DebugUtil.printDebugTag("Gson解析错误", e.getMessage() + "");
			_listener.onFailure(-1, "Gson解析错误");
			return;
		}

		if (rt != null) {
			DebugUtil.printDebugTag("解析成功", new Gson().toJson(rt));
			_listener.onSuccess(rt);

		} else {

			// _listener.onFailure(-2, "返回空值");
		}

	}

	/** 拼凑get方式的URL字符串 */
	public static String MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			// 不做URLEncoder处理
			// url.append(URLEncoder.encode(String.valueOf(params.get(name)),
			// UTF_8));
		}

		return url.toString().replace("?&", "?");
	}

}
