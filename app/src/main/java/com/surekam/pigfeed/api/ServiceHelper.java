package com.surekam.pigfeed.api;

/*
 * 封装所有的http请求方法*/
public class ServiceHelper {
	//获取配方类别
	public static String GETFORMULATYPE="http://222.240.147.142:8119/zsl/summary.htm";
	
	//获取配方
	public static String GETFORMULA="http://222.240.147.142:8119/zsl/summary.htm";
	
	//获取配方营养素
	public static String GETFORMULANUTRITION="http://222.240.147.142:8119/zsl/summary.htm";
	
	//获取配方饲料的推荐组成
	public static String GETFORMULARECOMMAND="http://222.240.147.142:8119/zsl/summary.htm";

	//类型标志位0表示配方类型，1表示饲料类型，2表示经销商类型
	public static String TYPEFORMULA="0";
	public static String TYPEFEED="1";
	public static String TYPEAGENCY="2";
}


