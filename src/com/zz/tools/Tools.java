package com.zz.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.zz.bean.CallInfo;
import com.zz.dao.RecordsDao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class Tools {
	public static String getContactNameFromPhoneBook(Context context, String phoneNum) {  
	    String contactName = "";  
	    ContentResolver cr = context.getContentResolver();  
	    Cursor pCur = cr.query(  
	            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,  
	            ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",  
	            new String[] { phoneNum }, null);  
	    if (pCur.moveToFirst()) {  
	        contactName = pCur  
	                .getString(pCur  
	                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));  
	        pCur.close();  
	    }  
	    return contactName;  
	}
	public static boolean  isInTXL(Context context,String num){
		  String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
				             ContactsContract.CommonDataKinds.Phone.NUMBER };
		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,projection,  ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + num + "'",null, null);
		if(cursor!=null)
			 for (int i = 0; i < cursor.getCount(); i++) {
				              cursor.moveToPosition(i);
				              // 取得联系人名字
				              int nameFieldColumnIndex = cursor
				                      .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
				              String name = cursor.getString(nameFieldColumnIndex);
				             System.out.println("在联系人中："+name);
				             return true;
				          }
		return false;
	}
}
