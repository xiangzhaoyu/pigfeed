package com.surekam.pigfeed.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


public class FeedFormulaRecommandVo implements Serializable {

	public Long id;
	
	public String code;//编码
	
	public String name;//名称
	
	public Long feedFormulaId;//配方
	
	public String feedFormulaName;//配方名称
	
	public Long feedId;//饲料
	
	public String feedName;//饲料名称
	
	public Long systemUnitId;//系统单位
	
	public String systemUnitName;//使用单位名称
	
	public String systemUnitNum;//单位数量
	
	public String systemUnitMax;//单位数量上限
	
	public String systemUnitMin;//单位数量下限
	
	public Long flag;//按什么推荐 0是按价格排序，1是按长得快速与否排序
	
	public Long value;//价格多少或多少天
	
	
	 // 备注
    public String remark;
    // 系统标识
    public String sysFlag;
    // 创建人
    public Long creatorId;
    // 创建人
    public String creatorName;
    // 创建时间
    public String createTime;
    // 最后修改人
    public Long lastModifierId;
    // 最后修改时间
    public String lastModifiedTime;
    
	
}
