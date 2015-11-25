package com.surekam.pigfeed.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


public class AgencyVo  implements Serializable  {

	public Long id;
	
	public String code;//编码
	
	public String name;//名称
	

	public Long agencyTypeId;//经销商类型
	
	public String agencyTypeName;//经销商类型名称
	
	public Long areaId;//区域
	
	public String areaName;//区域名称
	
	public String phone1;//联系电话
	
	public String phone2;//手机
	
	public String address;//地址
	
	public String contactMan;//联系人姓名
	
	public String companyWeb;//企业网站
	
	public String systemUnitPrice;//单位价格
	
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
