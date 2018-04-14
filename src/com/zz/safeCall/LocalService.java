package com.zz.safeCall;

import java.util.List;

import com.emobile.localservicedemo.R;
import com.iflytek.cloud.SpeechUtility;
import com.zz.bean.CallInfo;
import com.zz.dao.RecordsDao;
import com.zz.dao.WhiteListDao;
import com.zz.safeCall.YunZhiSheng.AsrStatus;
import com.zz.tools.Time;
import com.zz.tools.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application. The
 * {@link LocalServiceActivities.Controller} and
 * {@link LocalServiceActivities.Binding} classes show how to interact with the
 * service.
 * 
 * <p>
 * Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service. This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

public class LocalService extends Service {
	private NotificationManager mNM;
	private AudioFileFunc audioFileFunc = AudioFileFunc.getInstance(this);//����ʵ����
	private int callstate = -1;
	private TelephonyManager tm;
	private Myphonelistener mpl;
	private String phoneNum;
	//
	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		LocalService getService() {
			return LocalService.this;
		}
	}
	public Handler getHandler(){
		return mHander;
	}
	private Handler  mHander=new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
		       RecordsDao dao = RecordsDao.getInstance(LocalService.this);
	     	    CallInfo wf=new CallInfo();
	     	    wf.setMsg(msg.getData().getString("reasult"));
	     	    wf.setTel(phoneNum);
	     	    wf.setTime(Time.getTime());
	     	    dao.InsertRecord(wf);
			showMessage("����鿴");
		};
		
	};
	@Override
	public void onCreate() {
		SpeechUtility.createUtility(LocalService.this, "appid=" + getString(R.string.app_id));
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();
		 tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		 mpl=new Myphonelistener();
		tm.listen(mpl, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		setSpeekModle(false);//�ر�����
		tm.listen(mpl, PhoneStateListener.LISTEN_NONE);
		mNM.cancel(R.string.local_service_started);
		// Tell the user we stopped.
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT)
				.show();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		CharSequence text = getText(R.string.local_service_started);
		Notification notification = new Notification(R.drawable.stat_sample,
				text, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, StartedActivity.class), 0);
		notification.setLatestEventInfo(this,
				getText(R.string.local_service_label), text, contentIntent);
		mNM.notify(R.string.local_service_started, notification);
	}
	@SuppressLint("NewApi")
	public void showMessage(String s){	
		  Builder builder = new Notification.Builder(LocalService.this);  
	        PendingIntent contentIndent = PendingIntent.getActivity(LocalService.this, 0, 
	        		new Intent(LocalService.this,YunZhiSheng.class), 
	        		PendingIntent.FLAG_UPDATE_CURRENT);  
	        builder . setContentIntent(contentIndent) .setSmallIcon(R.drawable.ic_launcher)//����״̬�������ͼ�꣨Сͼ�꣩ ����������������������������������������.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.i5))//���������б������ͼ�꣨��ͼ�꣩ ��������������.setTicker("this is bitch!") //����״̬������ʾ����Ϣ  
	               .setWhen(System.currentTimeMillis())//����ʱ�䷢��ʱ��  
	               .setAutoCancel(true)//���ÿ������  
	               .setContentTitle("��⵽Σ�գ�")//���������б���ı���  
	               .setContentText(s);//��������������  
	        Notification notification = builder.getNotification();  
	        //��i��Ϊ����ʾ����Notification  
	        mNM.notify(1,notification); 
	        StartedActivity.messageNum++;
	}
	void setSpeekModle(boolean open){  
		AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setMode(AudioManager.ROUTE_SPEAKER);  
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);  
        audioManager.setMode(AudioManager.MODE_IN_CALL);   
  
        if(!audioManager.isSpeakerphoneOn()&&true==open) {  
          audioManager.setSpeakerphoneOn(true);  
          System.out.println("��������");
        }else if(audioManager.isSpeakerphoneOn()&&false==open){  
                 audioManager.setSpeakerphoneOn(false); 
                 System.out.println("�ر�����");
        }  
      
    } 
	private class Myphonelistener extends PhoneStateListener {
		private Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		@SuppressLint("SdCardPath")
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub

			try {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE: // ����״̬д�ϴ����ݵĲ���
					if (0 == callstate){
						System.out.println("¼������,��ʼд��");
						audioFileFunc.stopRecordAndFile();
						callstate = -1;
					}
					break;
				case TelephonyManager.CALL_STATE_RINGING: // ����״̬
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK: // ͨ��״̬
					if (-1 == callstate){
						/**********���ò���***********/
						phoneNum=incomingNumber;
						WhiteListDao wdao=WhiteListDao.getInstance(LocalService.this);
						if(wdao.select_phone(incomingNumber)!=null) {	
							System.out.println("�õ绰���ڰ�������ɨ��") ;
							return;
							}//��������ɨ��
						Boolean lxr=PreferenceManager.getDefaultSharedPreferences(LocalService.this).getBoolean("example_checkbox", true);
					
						System.out.println("������ϵ��ͨ���� "+lxr);						
						if(lxr&&Tools.isInTXL(LocalService.this, incomingNumber)) 
						{
							System.out.println(incomingNumber+"�õ绰����ͨѶ¼��ɨ��") ;
							return;		
						}
	                        
						
						Boolean is=PreferenceManager.getDefaultSharedPreferences(LocalService.this).getBoolean("openmt", true);
						if(is)
						setSpeekModle(true);//��������
						else
						setSpeekModle(false);
						System.out.println("��ʼ¼��"+Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/fzpzz.pcm");
						/**********¼������***********/
						audioFileFunc.setRawFilePath(Environment.getExternalStorageDirectory()
								.getAbsolutePath() + "/fzpzz.pcm");
						audioFileFunc.startRecordAndFile();
						callstate = 0;
					}
					break;
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.onCallStateChanged(state, incomingNumber);
		}


	}
}