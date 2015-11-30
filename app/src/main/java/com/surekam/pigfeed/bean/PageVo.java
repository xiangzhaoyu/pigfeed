package com.surekam.pigfeed.bean;

import java.io.Serializable;

public class PageVo implements Serializable {
	public int pageNo = 1;
    public int pageSize = 20;
    public long totalItems = 0;
}
