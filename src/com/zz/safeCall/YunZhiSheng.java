package com.zz.safeCall;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.emobile.localservicedemo.R;
import com.zz.dao.DatabaseHelper;
import com.zz.dao.RecordsDao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.common.USCError;
import cn.yunzhisheng.pro.USCRecognizer;
import cn.yunzhisheng.pro.USCRecognizerListener;

/**
 * ��֪��ʶ��ʵ������
 * 
 * @author
 * 
 */

public class YunZhiSheng extends Activity implements OnClickListener {

	/**
	 * ��ǰʶ��״̬
	 */
	enum AsrStatus {
		idle, recording, recognizing
	}

	private AsrStatus statue = AsrStatus.idle;
	private TextView mResultTextView;
	private int id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status_bar_main);
		mResultTextView = (TextView) findViewById(R.id.result_textview);
		id=this.getIntent().getIntExtra("id",0);
		//String m=Recognizer.getRecognizer().getResult();
		RecordsDao dao=new RecordsDao(this);
		Cursor  myCursor=dao.select_data();
		myCursor.moveToPosition(id);
		String m=myCursor.getString(4);
		if(m!=null){
			CharSequence charSequence = Html.fromHtml(m);
			mResultTextView.setText(charSequence);
		}else 
			mResultTextView.setText("�޼�¼");

        System.out.println("����ʶ��ACTIVITY");
	}
	private void log_v(String msg) {
		Log.v("demo", msg);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
