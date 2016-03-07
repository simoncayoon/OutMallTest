package com.beetron.outmall.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.beetron.outmall.models.ProSummary;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PRO_SUMMARY".
*/
public class ProSummaryDao extends AbstractDao<ProSummary, String> {

    public static final String TABLENAME = "PRO_SUMMARY";

    /**
     * Properties of entity ProSummary.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Sid = new Property(0, String.class, "sid", true, "SID");
        public final static Property Fid = new Property(1, String.class, "fid", false, "FID");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property Jianshu = new Property(3, String.class, "jianshu", false, "JIANSHU");
        public final static Property Img = new Property(4, String.class, "img", false, "IMG");
        public final static Property Price1 = new Property(5, Double.class, "price1", false, "PRICE1");
        public final static Property Price2 = new Property(6, Double.class, "price2", false, "PRICE2");
        public final static Property Xl = new Property(7, Integer.class, "xl", false, "XL");
        public final static Property Count = new Property(8, Integer.class, "count", false, "COUNT");
        public final static Property IsLimit = new Property(9, Boolean.class, "isLimit", false, "IS_LIMIT");
    };


    public ProSummaryDao(DaoConfig config) {
        super(config);
    }
    
    public ProSummaryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PRO_SUMMARY\" (" + //
                "\"SID\" TEXT PRIMARY KEY NOT NULL ," + // 0: sid
                "\"FID\" TEXT," + // 1: fid
                "\"TITLE\" TEXT," + // 2: title
                "\"JIANSHU\" TEXT," + // 3: jianshu
                "\"IMG\" TEXT," + // 4: img
                "\"PRICE1\" REAL," + // 5: price1
                "\"PRICE2\" REAL," + // 6: price2
                "\"XL\" INTEGER," + // 7: xl
                "\"COUNT\" INTEGER," + // 8: count
                "\"IS_LIMIT\" INTEGER);"); // 9: isLimit
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PRO_SUMMARY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ProSummary entity) {
        stmt.clearBindings();
 
        String sid = entity.getSid();
        if (sid != null) {
            stmt.bindString(1, sid);
        }
 
        String fid = entity.getFid();
        if (fid != null) {
            stmt.bindString(2, fid);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        String jianshu = entity.getJianshu();
        if (jianshu != null) {
            stmt.bindString(4, jianshu);
        }
 
        String img = entity.getImg();
        if (img != null) {
            stmt.bindString(5, img);
        }
 
        Double price1 = entity.getPrice1();
        if (price1 != null) {
            stmt.bindDouble(6, price1);
        }
 
        Double price2 = entity.getPrice2();
        if (price2 != null) {
            stmt.bindDouble(7, price2);
        }
 
        Integer xl = entity.getXl();
        if (xl != null) {
            stmt.bindLong(8, xl);
        }
 
        Integer count = entity.getCount();
        if (count != null) {
            stmt.bindLong(9, count);
        }
 
        Boolean isLimit = entity.getIsLimit();
        if (isLimit != null) {
            stmt.bindLong(10, isLimit ? 1L: 0L);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ProSummary readEntity(Cursor cursor, int offset) {
        ProSummary entity = new ProSummary( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // sid
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // fid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // title
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // jianshu
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // img
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // price1
            cursor.isNull(offset + 6) ? null : cursor.getDouble(offset + 6), // price2
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // xl
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // count
            cursor.isNull(offset + 9) ? null : cursor.getShort(offset + 9) != 0 // isLimit
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ProSummary entity, int offset) {
        entity.setSid(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setFid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setJianshu(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setImg(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPrice1(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setPrice2(cursor.isNull(offset + 6) ? null : cursor.getDouble(offset + 6));
        entity.setXl(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setCount(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setIsLimit(cursor.isNull(offset + 9) ? null : cursor.getShort(offset + 9) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(ProSummary entity, long rowId) {
        return entity.getSid();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(ProSummary entity) {
        if(entity != null) {
            return entity.getSid();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
