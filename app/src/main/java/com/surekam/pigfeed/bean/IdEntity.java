package com.surekam.pigfeed.bean;


/**
 * IdEntity
 * 统一定义id的entity基类.
 * 基类统一定义id的属性名称、数据类型、列名映射及生成策略. 子类可重载getId()函数重定义id的列名映射和生成策略.
 */
// JPA 基类的标识
public abstract class IdEntity {
    
    // 主键
    public Long id;

    
}
