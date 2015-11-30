package com.surekam.pigfeed.bean;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 饲料配方类型表
 * @author lixie
 */

public class FeedFormulaType extends AbstractEntity implements Serializable{
	
	public String code;//编码
	
	public String name;//名称
	
	@Override
	public String toString(){
		return name;
	}
	
	
}
