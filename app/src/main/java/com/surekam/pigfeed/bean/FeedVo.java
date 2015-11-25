package com.surekam.pigfeed.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


public class FeedVo  implements Serializable {

	public Long id;
	
	public String code;//编码
	
	public String name;//名称
	
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
    
    @Override
	public String toString(){
		return name;
	}
    
    
        
}
