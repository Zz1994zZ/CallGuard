package com.zz.safeCall;

import java.util.ArrayList;

import com.emobile.localservicedemo.R;
import com.emobile.localservicedemo.SettingsActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class StartedActivity extends Activity {
	public static int messageNum=0;
	private static boolean isWork=false;
	ImageButton start,stop;
	TextView isOpen,web;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_service_controller);
		// Watch for button clicks.
		 start = (ImageButton) findViewById(R.id.start);
		 start.setOnClickListener(mStartListener);
		stop = (ImageButton) findViewById(R.id.stop);
		stop.setOnClickListener(mStopListener);
		isOpen=(TextView) findViewById(R.id.phone);
		web=(TextView) findViewById(R.id.textView2);
		String html = "";
		html+=getText(R.string.main_url);//这里即使不加协议好HTTP；也能自动被系统识别出来。 
		web.setText(html); 
		web.setAutoLinkMask(Linkify.ALL); 
		web.setMovementMethod(LinkMovementMethod.getInstance()); 
		//Recognizer.setRecognizer(this);
	}
	@Override  
	  public boolean onCreateOptionsMenu(Menu menu) {  
	    MenuInflater inflater = getMenuInflater();  
	    inflater.inflate(R.menu.mainmenu, menu);  
	    return true;  
	  }   
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		           case R.id.action_exit:
		        	    Intent intent=new Intent(this,CallList.class);
						startActivity(intent);
		        	   break;
		           case R.id.action_about:
		        	   break;
		           case R.id.action_settings:
		        		Intent toBound = new Intent(StartedActivity.this,
		    					SettingsActivity.class);
		    			startActivity(toBound);
		        	   break;
		           default:
		break;
		           }   
		           return super.onMenuItemSelected(featureId, item);
		      }
	@Override
	protected void  onResume(){
		super.onResume();
		setOpenIcon();
		if(messageNum>0){
				Intent intent=new Intent(this,CallList.class);
				startActivity(intent);
				messageNum--;
		}
	}
	private OnClickListener mStartListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			 startService(new Intent(StartedActivity.this,
			 LocalService.class));
			 isWork=true;
			 setOpenIcon();
		}
	};

	private OnClickListener mStopListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			Builder dialog = new AlertDialog.Builder(StartedActivity.this).setTitle("确认").setMessage(
				     "关闭安全服务？").setPositiveButton("确定",
				     new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						isWork=false;
						stopService(new Intent(StartedActivity.this, LocalService.class));
						setOpenIcon();
					}
				     }).setNegativeButton("取消",null);
			dialog.show();
		}
	};
/**
 * 设置图标
 */
	private void setOpenIcon(){
		if(isWork)
		{
			 start.setVisibility(View.INVISIBLE);
			 stop.setVisibility(View.VISIBLE);
			 isOpen.setTextColor(android.graphics.Color.WHITE);
			 isOpen.setText("正在保护通话安全");
		}
		else
		{
			 start.setVisibility(View.VISIBLE);
			 stop.setVisibility(View.INVISIBLE);
			 isOpen.setTextColor(android.graphics.Color.RED);
			 isOpen.setText("点击启动保护");
		}
		
		
	}
	private OnClickListener mJumpListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent toBound = new Intent(StartedActivity.this,
					BindingActivity.class);
			startActivity(toBound);
		}
	};
	 @Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event)  
	    {  
	        if (keyCode == KeyEvent.KEYCODE_BACK )  
	        {  
	        	Intent home = new Intent(Intent.ACTION_MAIN);  
	        	home.addCategory(Intent.CATEGORY_HOME);   
	        	startActivity(home); 
	        }  
	          
	        return false;  
	          
	    } 
	 private VelocityTracker velocityTracker;//用于得到手势在屏幕上的滑动速度
	 private static final int VELOCITY = 600;        
	     @Override
	 public boolean onTouchEvent(MotionEvent event) {
	     String localClassName = getLocalClassName();//当前所在类名
	     int action = event.getAction();
	     //float x = event.getX();
	     //float y = event.getY();
	      
	     switch (action) {
	     case MotionEvent.ACTION_DOWN:
	         if(velocityTracker == null){
	             velocityTracker = VelocityTracker.obtain();//取得手势在屏幕上的滑动速度
	             velocityTracker.addMovement(event);
	         }           
	         //lastMotionX = x;
	          
	         break;
	      
	     case MotionEvent.ACTION_MOVE:
	         //int deltaX = (int) (lastMotionX - x);
	         if(velocityTracker != null){
	             velocityTracker.addMovement(event);
	         }
	         //lastMotionX = x;
	         break;
	          
	     case MotionEvent.ACTION_UP:
	          
	         int velocityX = 0;
	         if(velocityTracker != null){
	             velocityTracker.addMovement(event);
	             velocityTracker.computeCurrentVelocity(1000);//计算每秒滑动多少个像素
	             velocityX = (int) velocityTracker.getXVelocity();//最后计算检索X速度
	         }
	          
	         if(velocityX > VELOCITY ){
	        	  //  if("safeCall.StartedActivity".equals(localClassName)){
	        	    	Intent toBound = new Intent(StartedActivity.this,
	        					CallList.class);
	        			startActivity(toBound);
		           //  }
	         } else if(velocityX < -VELOCITY ){
	           //  if("safeCall.StartedActivity".equals(localClassName)){
	            		Intent toBound = new Intent(StartedActivity.this,
	        					CallList.class);
	        			startActivity(toBound);
	            // }
	         }
	          
	         if(velocityTracker != null){
	             velocityTracker.recycle();//回收
	             velocityTracker = null;
	         }
	          
	         break;
	     }
	      
	     return true;
	 }
}