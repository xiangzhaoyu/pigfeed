package com.surekam.pigfeed.bean;

/**
 * Created by xiangzy_1 on 2015/12/1.
 * 智能计算的时候需要的类
 */
public class ArtificialNur {
    public long Mid;//主体ID，比如是配方或者饲料
    public String Mname;//主体名称
    public long Cid;//从id，比如营养素
    public String Cname;//从名称，比如营养素名称
    public long UnitId;//单位id,这里用克
    public String UnitName;//单位名称
    public double UnitNumber;//含量
}
