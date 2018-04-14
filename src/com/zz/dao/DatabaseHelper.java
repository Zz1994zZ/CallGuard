package com.zz.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
public class DatabaseHelper extends SQLiteOpenHelper{
//number time name duration
	//数据库字段应该定义成常量,以便重用方便
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME="NAME";
	public static final String COLUMN_DURATION="DURATION";
	public static final String COLUMN_TEL="TEL";
	public static final String COLUMN_TIME="TIME";
	public static final String COLUMN_MSG="MSG";

	private static final String DATABASE_NAME = "CallInfo";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME="calls";
	public static final String TABLE_NAME2="whitelist";
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	public DatabaseHelper(Context context,String name,int version){
		this(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public DatabaseHelper(Context context,String name){
		this(context, DATABASE_NAME,DATABASE_VERSION);
	}
	
	public DatabaseHelper(Context context){
		this(context, DATABASE_NAME,DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT," + COLUMN_TEL + " TEXT," + COLUMN_TIME + " TEXT,"+ COLUMN_MSG + " TEXT," + COLUMN_DURATION + " TEXT)");
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_NAME2 +" (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TEL + " TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}
