package com.surekam.pigfeed.bean;

public class EventClass {

	// "time": 1405627488000,
	// "protocol": "HTTP",
	// "src": "10.0.2.4",
	// "dst": "188.40.238.250",
	// "action": "BLOCKED",
	// "reason": "VIRUS",
	// "dstInfo": "www.eicar.org",
	// "detail": "EICAR-Test-File",
	// "timeString": "2014-07-17 14:04:48"

	long time;
	String protocol;
	String src;
	String dst;
	String action;
	String reason;
	String dstInfo;
	String detail;
	String timeString;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getDstInfo() {
		return dstInfo;
	}

	public void setDstInfo(String dstInfo) {
		this.dstInfo = dstInfo;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

}
