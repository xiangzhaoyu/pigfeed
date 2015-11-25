package com.surekam.pigfeed.bean;

public class UrlClass {
	String url;
	String urlAccess; // DENY -阻止 ACCEPT-允许

	public UrlClass(String url) {
		// TODO Auto-generated constructor stub
		this.url = url;
		this.urlAccess = "DENY";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlAccess() {
		return urlAccess;
	}

	public void setUrlAccess(String urlAccess) {
		this.urlAccess = urlAccess;
	}

}
