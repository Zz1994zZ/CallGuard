package com.zz.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.zz.bean.WhiteInfo;

public class WhiteListDao {
private static final String TAG ="DAO_TAG";
	
	private DatabaseHelper mdbhelper;
	private static WhiteListDao mrecordsDao; 
	private static Context context;
	private SQLiteDatabase db;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//支持多线程的单例模式
		public WhiteListDao(Context context){
			mdbhelper = new DatabaseHelper(context);
		}
		public static WhiteListDao getInstance(Context c){
			if(null == mrecordsDao){
				synchronized(RecordsDao.class){
					if(null == mrecordsDao){
						mrecordsDao = new WhiteListDao(c);
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
			
			String sql = "delete from "+ DatabaseHelper.TABLE_NAME2 +" where _id in ("+idstr +")";
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
	            String sql = "SELECT * FROM "+DatabaseHelper.TABLE_NAME2+" where NAME='"+username+"'";
	            cursor = db.rawQuery(sql, null);
	            return cursor;
	        }catch(Exception ex){
	            ex.printStackTrace();
	            return null;
	        }
	    }
		//插入记录
		public long InsertRecord(WhiteInfo r){
			long rows = 0;
			db = mdbhelper.getWritableDatabase();
			Calendar c =Calendar.getInstance(Locale.CHINA);
			ContentValues cv=new ContentValues();
			cv.put(DatabaseHelper.COLUMN_TEL,r.getTel());
			try{
				rows = db.insert(DatabaseHelper.TABLE_NAME2, null, cv);
			}catch(Exception e){
				Log.e(TAG, e.getMessage());
			}
			db.close();
			mdbhelper.close();
			return rows;
		}
		//修改记录
		public long UpdateRecord(WhiteInfo r,Integer id){
			long rows = 0;
			db = mdbhelper.getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(DatabaseHelper.COLUMN_TEL,r.getTel());
			try{
				rows =db.update(DatabaseHelper.TABLE_NAME2, cv, "_id = "+id, null);
			}catch(Exception e){
				Log.e(TAG, e.getMessage());
			}
			db.close();
			mdbhelper.close();
			return rows;
		}
		
		//查询结果
		public static final String[] PROJECTION ={
			DatabaseHelper.COLUMN_TEL,
		};
		//查询数据
		public Cursor select_data()
		{
			db=mdbhelper.getReadableDatabase();
			Cursor cursor=db.query(DatabaseHelper.TABLE_NAME2, null, null, null, null, null," _id desc");
			return cursor;
		}
		//查询匹配ID数据
		  public Cursor select_phone(String phone){
		        Cursor cursor = null;
		        try{
		        	db = mdbhelper.getWritableDatabase();	
		            String sql = "SELECT * FROM "+DatabaseHelper.TABLE_NAME2+" where TEL='"+phone+"'";
		            cursor = db.rawQuery(sql, null);
		            if(cursor.getCount()==0)
		            	return null;
		            return cursor;
		        }catch(Exception ex){
		            ex.printStackTrace();
		            return null;
		        }
		    }
		//查询匹配ID数据
		  public Cursor select_info(String id){
		        Cursor cursor = null;
		        try{
		        	db = mdbhelper.getWritableDatabase();	
		            String sql = "SELECT * FROM "+DatabaseHelper.TABLE_NAME2+" where _id='"+id+"'";
		            cursor = db.rawQuery(sql, null);
		            return cursor;
		        }catch(Exception ex){
		            ex.printStackTrace();
		            return null;
		        }
		    }
}
