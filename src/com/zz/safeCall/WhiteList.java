package com.zz.safeCall;

import com.emobile.localservicedemo.R;
import com.emobile.localservicedemo.R.layout;
import com.emobile.localservicedemo.R.menu;
import com.zz.bean.WhiteInfo;
import com.zz.dao.DatabaseHelper;
import com.zz.dao.RecordsDao;
import com.zz.dao.WhiteListDao;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WhiteList extends Activity {
	private ListView myListView;
	private Cursor myCursor;
	public WhiteListDao dao;
	private static final int DELEUser_ID = Menu.FIRST+2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_white_list);
		 myListView=(ListView)findViewById(R.id.whiteList);
		 updateListView();
		   //添加长按点击  
	     myListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
	              
	            @Override  
	            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
	                menu.setHeaderTitle("操作"); 
	              //添加删除菜单
	           		menu.add(0, DELEUser_ID, 0, "删除记录");
	            }  
	        });   
	}
	  public boolean onContextItemSelected(MenuItem item) { 
	    	final AdapterView.AdapterContextMenuInfo info;
	   		try
	   		{
	   			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	   		}
	   		catch (ClassCastException e)
	   		{
	   			return false;
	   		}

	   		switch (item.getItemId())
	   		{
	   			case DELEUser_ID:
	   			{
	   				AlertDialog dialog = new AlertDialog.Builder(WhiteList.this).setTitle("确认").setMessage("删除该号码？").setPositiveButton("确定",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//删除一条记录   				
			   				dao.delRecord((int) info.id);
			   				Toast.makeText(WhiteList.this, "号码删除成功！", Toast.LENGTH_SHORT).show();
			   				updateListView();
							dialog.dismiss();
									   				
						}
					}).setNegativeButton("取消",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					}).create();
					dialog.show();
					return true;
	   			}
	   		}
	   		return false;
	    } 
	//显示用户列表
	private void updateListView() {		
		 myListView=(ListView)findViewById(R.id.whiteList);
		 TextView empty_txt=(TextView)findViewById(R.id.white_empty_txt);
	     dao=new WhiteListDao(WhiteList.this);
	     myCursor=dao.select_data();
	     Integer stu_num=myCursor.getCount();//获取数量
	     if(stu_num.equals(0)){
	    	 empty_txt.setText("暂无白名单号码（点击右上角添加）！");}
	    	 else 
	    	  empty_txt.setText("");	 
	    // }//else{
	       SimpleCursorAdapter adpater=new SimpleCursorAdapter(this
	        		, R.layout.white_call_row, myCursor,
	        		new String[]{DatabaseHelper.COLUMN_TEL},
	        		new int[]{R.id.phone}
	        		
	       );
	        myListView.setAdapter(adpater);
	       // }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.white_list, menu);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		           case R.id.add:
		        	   showDialog();
		        	   break;
		           default:
		break;
		           }   
		           return super.onMenuItemSelected(featureId, item);
		      }
	private void showDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(WhiteList.this);
       // builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请输入号码");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(WhiteList.this).inflate(R.layout.get_phone, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        
        final EditText username = (EditText)view.findViewById(R.id.editText1);
        
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String a = username.getText().toString().trim();
                //    将输入的用户名和密码打印出来
                if(a.equals(""))
                {
                	return;
                }
                WhiteListDao dao = WhiteListDao.getInstance(WhiteList.this);
	        	   WhiteInfo wf=new WhiteInfo();
	        	   wf.setTel(a);
	        	   dao.InsertRecord(wf);
	        	   updateListView();
           
            } 
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                
            }
        });
        builder.show();
    
	}

}
