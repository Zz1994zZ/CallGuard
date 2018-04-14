package com.zz.dao;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.zz.bean.CallInfo;

import android.view.View;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
public class RecordsDao {
private static final String TAG ="DAO_TAG";
	
	private DatabaseHelper mdbhelper;
	private static RecordsDao mrecordsDao; 
	private static Context context;
	private SQLiteDatabase db;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//支持多线程的单例模式
		public RecordsDao(Context context){
			mdbhelper = new DatabaseHelper(context);
		}
		public static RecordsDao getInstance(Context c){
			if(null == mrecordsDao){
				synchronized(RecordsDao.class){
					if(null == mrecordsDao){
						mrecordsDao = new RecordsDao(c);
					}
				}
			}
			context = c;
			return mrecordsDao;
		}
		


		
		public void execSQL(String sql){
			try{
				db = mdbhelper.getWritableDatabase();
				db.execSQL(sql);
			}catch(Exception e) {
				
			}finally{
				db.close();
				mdbhelper.close();
			}
		}		
		//
		public void closeclose() {
			mdbhelper.close();
		}

		//删除记录
		public void delRecord(Integer idstr){
			
			String sql = "delete from "+ DatabaseHelper.TABLE_NAME +" where _id in ("+idstr +")";
			try{
				db = mdbhelper.getWritableDatabase();
				db.execSQL(sql);
			}catch(Exception e) {
				
			}finally{
				db.close();
				mdbhelper.close();
			}
		}
		 /**
	     * 查询
	     */
	    public Cursor select(String username){
	        Cursor cursor = null;
	        try{
	        	db = mdbhelper.getWritableDatabase();	
	            String sql = "SELECT * FROM "+DatabaseHelper.TABLE_NAME+" where NAME='"+username+"'";
	            cursor = db.rawQuery(sql, null);
	            return cursor;
	        }catch(Exception ex){
	            ex.printStackTrace();
	            return null;
	        }
	    }
		//插入记录
		public long InsertRecord(CallInfo r){
			long rows = 0;
			db = mdbhelper.getWritableDatabase();
			Calendar c =Calendar.getInstance(Locale.CHINA);
			ContentValues cv=new ContentValues();
			cv.put(DatabaseHelper.COLUMN_NAME, r.getName());
			cv.put(DatabaseHelper.COLUMN_TIME,r.getTime());
			cv.put(DatabaseHelper.COLUMN_TEL,r.getTel());
			cv.put(DatabaseHelper.COLUMN_DURATION,r.getDuration());
			cv.put(DatabaseHelper.COLUMN_MSG,r.getMsg());
			try{
				rows = db.insert(DatabaseHelper.TABLE_NAME, null, cv);
			}catch(Exception e){
				Log.e(TAG, e.getMessage());
			}
			db.close();
			mdbhelper.close();
			return rows;
		}
		//修改记录
		public long UpdateRecord(CallInfo r,Integer id){
			long rows = 0;
			db = mdbhelper.getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(DatabaseHelper.COLUMN_NAME, r.getName());
			cv.put(DatabaseHelper.COLUMN_TIME,r.getTime());
			cv.put(DatabaseHelper.COLUMN_TEL,r.getTel());
			cv.put(DatabaseHelper.COLUMN_DURATION,r.getDuration());
			cv.put(DatabaseHelper.COLUMN_MSG,r.getMsg());
			try{
				rows =db.update(DatabaseHelper.TABLE_NAME, cv, "_id = "+id, null);
			}catch(Exception e){
				Log.e(TAG, e.getMessage());
			}
			db.close();
			mdbhelper.close();
			return rows;
		}
		
		//查询结果
		public static final String[] PROJECTION ={
			DatabaseHelper.COLUMN_ID,
			DatabaseHelper.COLUMN_NAME,
			DatabaseHelper.COLUMN_TEL,
			DatabaseHelper.COLUMN_DURATION,
			DatabaseHelper.COLUMN_TIME,
			DatabaseHelper.COLUMN_MSG
		};
		//查询数据
		public Cursor select_data()
		{
			db=mdbhelper.getReadableDatabase();
			Cursor cursor=db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null," _id desc");
			return cursor;
		}
		//查询匹配ID数据
		  public Cursor select_info(String id){
		        Cursor cursor = null;
		        try{
		        	db = mdbhelper.getWritableDatabase();	
		            String sql = "SELECT * FROM "+DatabaseHelper.TABLE_NAME+" where _id='"+id+"'";
		            cursor = db.rawQuery(sql, null);
		            return cursor;
		        }catch(Exception ex){
		            ex.printStackTrace();
		            return null;
		        }
		    }
}
