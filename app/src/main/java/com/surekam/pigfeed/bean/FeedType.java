package com.surekam.pigfeed.bean;

import java.util.ArrayList;
import java.util.List;


/**
 * 饲料类型表
 * @author lixie
 */
public class FeedType extends AbstractEntity{
	
	public String code;//编码
	
	public String name;//名称
	
	@Override
	public String toString(){
		return name;
	}
	
}
