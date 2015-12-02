package com.surekam.pigfeed.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.surekam.pigfeed.bean.ArtificialNur;
import com.surekam.pigfeed.bean.City;
import com.surekam.pigfeed.bean.NutritionVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangzy_1 on 2015/12/1.
 */
public class PfDb {
    public static final String PIGFEED_DB_NAME = "pigfeed.db";
    private static SQLiteDatabase db;

    public PfDb(Context context, String path) {
        if(db==null)
            db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
    }

    /*
    * 获取配方的营养素组成
    * */
    public List<ArtificialNur> getFormulaNurs(String formulaid){
        List<ArtificialNur> result = new ArrayList<ArtificialNur>();
        try{
            String sql="";
            if(formulaid!=null&&formulaid.length()>0){
                sql="SELECT ff.f_id formulaid,ff.f_name formulaname,n.f_id nutriid,n.f_name nutriname,su.f_id nuid,su.f_name nuname,ffd.f_sys_unit_num nunum\n" +
                        " from tb_zsl_feed_formula_detail ffd \n" +
                        "LEFT JOIN tb_zsl_feed_formula ff on ffd.f_feed_formula_id=ff.f_id \n" +
                        "LEFT JOIN tb_zsl_nutrition n on ffd.f_nutrition_id=n.f_id \n" +
                        "LEFT JOIN tb_zsl_system_unit su on ffd.f_sys_unit_id=su.f_id\n" +
                        "where ffd.f_feed_formula_id='"+formulaid+"'";
            }else{
                sql="SELECT ff.f_id formulaid,ff.f_name formulaname,n.f_id nutriid,n.f_name nutriname,su.f_id nuid,su.f_name nuname,ffd.f_sys_unit_num nunum\n" +
                        " from tb_zsl_feed_formula_detail ffd \n" +
                        "LEFT JOIN tb_zsl_feed_formula ff on ffd.f_feed_formula_id=ff.f_id \n" +
                        "LEFT JOIN tb_zsl_nutrition n on ffd.f_nutrition_id=n.f_id \n" +
                        "LEFT JOIN tb_zsl_system_unit su on ffd.f_sys_unit_id=su.f_id\n";
            }
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                try {
                    ArtificialNur item = new ArtificialNur();
                    item.Mid = c.getLong(c.getColumnIndex("formulaid"));
                    item.Mname = c.getString(c.getColumnIndex("formulaname"));
                    item.Cid = c.getLong(c.getColumnIndex("nutriid"));
                    item.Cname = c.getString(c.getColumnIndex("nutriname"));
                    item.UnitId=c.getLong(c.getColumnIndex("nuid"));
                    item.UnitName = c.getString(c.getColumnIndex("nuname"));
                    item.UnitNumber = c.getDouble(c.getColumnIndex("nunum"));
                    result.add(item);
                }catch (Exception e){}
            }
        }catch (Exception e){}
        return result;
    }

    /*
    * 找所有饲料中营养素nurid含量在value上的饲料列表
    * */
    public List<ArtificialNur> getMaxFeedNurs(long nurid,double value){
        List<ArtificialNur> result= new ArrayList<ArtificialNur>();
        try{
            String sql="SELECT f.f_id feedid,f.f_name feedname,n.f_id nutriid,n.f_name nutriname,sn.f_id nuid,sn.f_name nuname,fd.f_sys_unit_num nunum\n" +
                    "from tb_zsl_feed_detail fd \n" +
                    "LEFT JOIN tb_zsl_feed f on fd.f_feed_id=f.f_id \n" +
                    "LEFT JOIN tb_zsl_nutrition n on fd.f_nutrition_id=n.f_id \n" +
                    "LEFT JOIN tb_zsl_system_unit sn on fd.f_sys_unit_id=sn.f_id\n" +
                    "where n.f_id= "+nurid+" and fd.f_sys_unit_num >= "+value;
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                try {
                    ArtificialNur item = new ArtificialNur();
                    item.Mid = c.getLong(c.getColumnIndex("feedid"));
                    item.Mname = c.getString(c.getColumnIndex("feedname"));
                    item.Cid = c.getLong(c.getColumnIndex("nutriid"));
                    item.Cname = c.getString(c.getColumnIndex("nutriname"));
                    item.UnitId=c.getLong(c.getColumnIndex("nuid"));
                    item.UnitName = c.getString(c.getColumnIndex("nuname"));
                    item.UnitNumber = c.getDouble(c.getColumnIndex("nunum"));
                    result.add(item);
                }catch (Exception e){}
            }
        }catch (Exception e){}
        return result;
    }

    /*
    * 找所有饲料中营养素nurid含量在value之下的饲料表
    * */
    public List<ArtificialNur> getMinFeedNurs(long nurid,double value){
        List<ArtificialNur> result= new ArrayList<ArtificialNur>();
        try{
            String sql="SELECT f.f_id feedid,f.f_name feedname,n.f_id nutriid,n.f_name nutriname,sn.f_id nuid,sn.f_name nuname,fd.f_sys_unit_num nunum\n" +
                    "from tb_zsl_feed_detail fd \n" +
                    "LEFT JOIN tb_zsl_feed f on fd.f_feed_id=f.f_id \n" +
                    "LEFT JOIN tb_zsl_nutrition n on fd.f_nutrition_id=n.f_id \n" +
                    "LEFT JOIN tb_zsl_system_unit sn on fd.f_sys_unit_id=sn.f_id\n" +
                    "where n.f_id= "+nurid+" and fd.f_sys_unit_num <= "+value;
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                try {
                    ArtificialNur item = new ArtificialNur();
                    item.Mid = c.getLong(c.getColumnIndex("feedid"));
                    item.Mname = c.getString(c.getColumnIndex("feedname"));
                    item.Cid = c.getLong(c.getColumnIndex("nutriid"));
                    item.Cname = c.getString(c.getColumnIndex("nutriname"));
                    item.UnitId=c.getLong(c.getColumnIndex("nuid"));
                    item.UnitName = c.getString(c.getColumnIndex("nuname"));
                    item.UnitNumber = c.getDouble(c.getColumnIndex("nunum"));
                    result.add(item);
                }catch (Exception e){}
            }
        }catch (Exception e){}
        return result;
    }

    /*
    * 获取饲料的营养组成
    * */
    public List<ArtificialNur> getFeedNurs(long feedid){
        List<ArtificialNur> result=new ArrayList<ArtificialNur>();
        try{
            String sql="SELECT f.f_id feedid,f.f_name feedname,n.f_id nutriid,n.f_name nutriname,sn.f_id nuid,sn.f_name nuname,fd.f_sys_unit_num nunum\n" +
                    "from tb_zsl_feed_detail fd \n" +
                    "LEFT JOIN tb_zsl_feed f on fd.f_feed_id=f.f_id \n" +
                    "LEFT JOIN tb_zsl_nutrition n on fd.f_nutrition_id=n.f_id \n" +
                    "LEFT JOIN tb_zsl_system_unit sn on fd.f_sys_unit_id=sn.f_id\n" +
                    "where fd.f_id= "+feedid;
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                try {
                    ArtificialNur item = new ArtificialNur();
                    item.Mid = c.getLong(c.getColumnIndex("feedid"));
                    item.Mname = c.getString(c.getColumnIndex("feedname"));
                    item.Cid = c.getLong(c.getColumnIndex("nutriid"));
                    item.Cname = c.getString(c.getColumnIndex("nutriname"));
                    item.UnitId=c.getLong(c.getColumnIndex("nuid"));
                    item.UnitName = c.getString(c.getColumnIndex("nuname"));
                    item.UnitNumber = c.getDouble(c.getColumnIndex("nunum"));
                    result.add(item);
                }catch (Exception e){}
            }
        }catch (Exception e){}
        return result;
    }

    /*
    * 获取在指定feedids的，大于value，且营养素为nuri的列表
    * */
    public List<ArtificialNur> getFeedMaxNurs(String feedids,long nuri,double value){
        List<ArtificialNur> result= new ArrayList<ArtificialNur>();
        try{
            String sql="SELECT f.f_id feedid,f.f_name feedname,n.f_id nutriid,n.f_name nutriname,sn.f_id nuid,sn.f_name nuname,fd.f_sys_unit_num nunum\n" +
                    "from tb_zsl_feed_detail fd \n" +
                    "LEFT JOIN tb_zsl_feed f on fd.f_feed_id=f.f_id \n" +
                    "LEFT JOIN tb_zsl_nutrition n on fd.f_nutrition_id=n.f_id \n" +
                    "LEFT JOIN tb_zsl_system_unit sn on fd.f_sys_unit_id=sn.f_id\n" +
                    "where n.f_id= "+nuri+" and fd.f_sys_unit_num >= "+value+" and f.f_id in ("+feedids+")";
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                try {
                    ArtificialNur item = new ArtificialNur();
                    item.Mid = c.getLong(c.getColumnIndex("feedid"));
                    item.Mname = c.getString(c.getColumnIndex("feedname"));
                    item.Cid = c.getLong(c.getColumnIndex("nutriid"));
                    item.Cname = c.getString(c.getColumnIndex("nutriname"));
                    item.UnitId=c.getLong(c.getColumnIndex("nuid"));
                    item.UnitName = c.getString(c.getColumnIndex("nuname"));
                    item.UnitNumber = c.getDouble(c.getColumnIndex("nunum"));
                    result.add(item);
                }catch (Exception e){}
            }
        }catch (Exception e){}
        return result;
    }

    /*
    * 获取在指定feedids的，小于value，且营养素为nuri的列表
    * */
    public List<ArtificialNur> getFeedMinNurs(String feedids,long nuri,double value){
        List<ArtificialNur> result= new ArrayList<ArtificialNur>();
        try{
            String sql="SELECT f.f_id feedid,f.f_name feedname,n.f_id nutriid,n.f_name nutriname,sn.f_id nuid,sn.f_name nuname,fd.f_sys_unit_num nunum\n" +
                    "from tb_zsl_feed_detail fd \n" +
                    "LEFT JOIN tb_zsl_feed f on fd.f_feed_id=f.f_id \n" +
                    "LEFT JOIN tb_zsl_nutrition n on fd.f_nutrition_id=n.f_id \n" +
                    "LEFT JOIN tb_zsl_system_unit sn on fd.f_sys_unit_id=sn.f_id\n" +
                    "where n.f_id= "+nuri+" and fd.f_sys_unit_num <= "+value+" and f.f_id in ("+feedids+")";
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                try {
                    ArtificialNur item = new ArtificialNur();
                    item.Mid = c.getLong(c.getColumnIndex("feedid"));
                    item.Mname = c.getString(c.getColumnIndex("feedname"));
                    item.Cid = c.getLong(c.getColumnIndex("nutriid"));
                    item.Cname = c.getString(c.getColumnIndex("nutriname"));
                    item.UnitId=c.getLong(c.getColumnIndex("nuid"));
                    item.UnitName = c.getString(c.getColumnIndex("nuname"));
                    item.UnitNumber = c.getDouble(c.getColumnIndex("nunum"));
                    result.add(item);
                }catch (Exception e){}
            }
        }catch (Exception e){}
        return result;
    }

    /*
    * 获取在指定feedids的，大于value，且营养素为nuri的feedid的列表
    * */
    public List<Long> getMaxFeedIds(String feedids,long nuri,double value){
        List<Long> result=new ArrayList<Long>();
        try{
            String sql="";
            if(feedids!=null&&feedids.length()>0){
                sql="SELECT DISTINCT f_feed_id from tb_zsl_feed_detail where f_feed_id in ("+feedids+") and f_nutrition_id="+nuri+" and f_sys_unit_num>="+value;
            }else{
                sql="SELECT DISTINCT f_feed_id from tb_zsl_feed_detail where f_nutrition_id="+nuri+" and f_sys_unit_num>="+value;
            }
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                try {
                    Long id=c.getLong(c.getColumnIndex("f_feed_id"));
                    if(id>0){
                        result.add(id);
                    }
                }catch (Exception e){}
            }
        }catch (Exception e){}
        return result;
    }

    /*
    * 获取在指定feedids的，小鱼value，且营养素为nuri的feedid的列表
    * */
    public List<Long> getMinFeedIds(String feedids,long nuri,double value){
        List<Long> result= new ArrayList<Long>();
        try{
            String sql="";
            if(feedids!=null&&feedids.length()>0){
                sql="SELECT DISTINCT f_feed_id from tb_zsl_feed_detail where f_feed_id in ("+feedids+") and f_nutrition_id="+nuri+" and f_sys_unit_num<="+value;
            }else{
                sql="SELECT DISTINCT f_feed_id from tb_zsl_feed_detail where f_nutrition_id="+nuri+" and f_sys_unit_num<="+value;
            }
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                try {
                    Long id=c.getLong(c.getColumnIndex("f_feed_id"));
                    if(id>0){
                        result.add(id);
                    }
                }catch (Exception e){}
            }
        }catch (Exception e){}
        return result;
    }

    /*
    * 根据饲料id，营养素id，获取指定的值
    * */
    public ArtificialNur getAri(Long feedid,Long nuri){
        ArtificialNur result=null;
        try{
            String sql="SELECT f.f_id feedid,f.f_name feedname,n.f_id nutriid,n.f_name nutriname,sn.f_id nuid,sn.f_name nuname,fd.f_sys_unit_num nunum\n" +
                    "from tb_zsl_feed_detail fd \n" +
                    "LEFT JOIN tb_zsl_feed f on fd.f_feed_id=f.f_id \n" +
                    "LEFT JOIN tb_zsl_nutrition n on fd.f_nutrition_id=n.f_id \n" +
                    "LEFT JOIN tb_zsl_system_unit sn on fd.f_sys_unit_id=sn.f_id\n" +
                    "where n.f_id= "+nuri+" and f.f_id ="+feedid;
            Cursor c = db.rawQuery(sql, null);
            while (c.moveToNext()) {
                try {
                    ArtificialNur item = new ArtificialNur();
                    item.Mid = c.getLong(c.getColumnIndex("feedid"));
                    item.Mname = c.getString(c.getColumnIndex("feedname"));
                    item.Cid = c.getLong(c.getColumnIndex("nutriid"));
                    item.Cname = c.getString(c.getColumnIndex("nutriname"));
                    item.UnitId=c.getLong(c.getColumnIndex("nuid"));
                    item.UnitName = c.getString(c.getColumnIndex("nuname"));
                    item.UnitNumber = c.getDouble(c.getColumnIndex("nunum"));
                    result=item;
                }catch (Exception e){}
            }
        }catch (Exception e){}
        return result;
    }

}
