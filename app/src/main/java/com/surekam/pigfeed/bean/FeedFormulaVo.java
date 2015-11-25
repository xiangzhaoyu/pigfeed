package com.surekam.pigfeed.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


public class FeedFormulaVo implements Serializable {

	public Long id;
	
	public String code;//编码
	
	public String name;//名称
	
	@Override
	public String toString(){
		return name;
	}
	
	
	public Long feedFormulaTypeId;//饲料配方类型
	
	public String feedFormulaTypeName;//饲料配方类型名称
	
	public String content;//内容
	
	public String useNum;//使用单位数目
	
	public Long systemUnitId;//使用单位ID
	
	public String systemUnitName;//使用单位名称
	
	public Long feedFormulaDetailId;//配方明细
	public String feedFormulaDetailName;//配方明细名称
	
	public Long areaId;//区域
	public String areaName;//区域名称
	
	
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
