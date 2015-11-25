package com.surekam.pigfeed.bean;

import java.text.SimpleDateFormat;

/**
 * AbstractEntity
 * 统一定义公用字段的entity基类.
 */
// JPA 基类的标识
public abstract class AbstractEntity extends IdEntity{
    
    // 备注
    public String remark;
    // 系统标识
    public String sysFlag;
    // 创建人
    //public User creator;
    // 创建时间
    public String createTime;
    // 最后修改人
    //public User lastModifier;
    // 最后修改时间
    public String lastModifiedTime;

    
}
