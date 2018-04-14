package com.zz.safeCall;

import com.emobile.localservicedemo.R;
import com.emobile.localservicedemo.SettingsActivity;
import com.zz.dao.DatabaseHelper;
import com.zz.dao.RecordsDao;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/*public class CallList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_list);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.call_list, menu);
        return true;
    }
    
}*/
import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;  
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
public class CallList extends Activity{
	private static final int BACK_ID = Menu.FIRST;
	private static final int HELP_ID = Menu.FIRST+1;
	private static final int DELEUser_ID = Menu.FIRST+2;
	private Cursor myCursor;
	private ListView myListView;
	public RecordsDao dao;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		 super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_call_list);   
	      updateListView();
	     myListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					myCursor.moveToPosition(arg2);
				    Intent intent=new Intent();
					/*Bundle bundle=new Bundle();
					bundle.putString("UID",myCursor.getString(0));*/
					System.out.println(myCursor.getString(4));
					intent.putExtra("id", arg2);
					intent.setClass(CallList.this, YunZhiSheng.class);
					//intent.putExtras(bundle);
					startActivity(intent);
				}
			});
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
	  //长按菜单响应函数  
    //@Override  
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
   				AlertDialog dialog = new AlertDialog.Builder(CallList.this).setTitle("确认").setMessage("删除该记录？").setPositiveButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//删除一条记录   				
		   				dao.delRecord((int) info.id);
		   				Toast.makeText(CallList.this, "记录删除成功！", Toast.LENGTH_SHORT).show();
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
		 myListView=(ListView)findViewById(R.id.ListView1);
		 TextView empty_txt=(TextView)findViewById(R.id.empty_txt);
	     dao=new RecordsDao(CallList.this);
	     myCursor=dao.select_data();
	     Integer stu_num=myCursor.getCount();//获取数量
	     if(stu_num.equals(0)){
	    	 empty_txt.setText("暂无危险通话记录！");
	     }else{
	    	 empty_txt.setText("");
	     }
	       SimpleCursorAdapter adpater=new SimpleCursorAdapter(this
	        		, R.layout.call_row, myCursor,
	        		new String[]{DatabaseHelper.COLUMN_TEL,DatabaseHelper.COLUMN_NAME,DatabaseHelper.COLUMN_TIME},
	        		new int[]{R.id.name,R.id.email,R.id.num}
	        		
	       );
	        myListView.setAdapter(adpater);
	        
	}
	@Override  
	  public boolean onCreateOptionsMenu(Menu menu) {  
	    MenuInflater inflater = getMenuInflater();  
	    inflater.inflate(R.menu.call_list, menu);  
	    return true;  
	  }   
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		           case R.id.action_whiteList:
		        		Intent toBound = new Intent(CallList.this,
		        				WhiteList.class);
		    			startActivity(toBound);
		        	   break;
		           default:
		break;
		           }   
		           return super.onMenuItemSelected(featureId, item);
		      }

}

