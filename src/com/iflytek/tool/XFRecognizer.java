package com.iflytek.tool;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.zz.bean.CallInfo;
import com.zz.bean.WhiteInfo;
import com.zz.dao.RecordsDao;
import com.zz.dao.WhiteListDao;
import com.zz.safeCall.WhiteList;
import com.zz.tools.JsonParser;
import com.zz.tools.Time;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class XFRecognizer {
	private static String TAG = XFRecognizer.class.getSimpleName();
	// ��������
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	// ������д����
		private SpeechRecognizer mIat;
	// ��HashMap�洢��д���
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private Toast mToast;
	private Context  context;
	int ret = 0; // �������÷���ֵ
	private Handler mHandler;
public XFRecognizer(Context  context,Handler mHandler){
	this.context=context;
	this.mHandler=mHandler;
	// ��ʼ��ʶ����UIʶ�����
	// ʹ��SpeechRecognizer���󣬿ɸ��ݻص���Ϣ�Զ�����棻
	mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
}
/**
 * ��ʼ����������
 */
private InitListener mInitListener = new InitListener() {

	@Override
	public void onInit(int code) {
		Log.d(TAG, "SpeechRecognizer init() code = " + code);
		if (code != ErrorCode.SUCCESS) {
			//showTip("��ʼ��ʧ�ܣ������룺" + code);
			System.out.println("��ʼ��ʧ�ܣ������룺" + code);
		}
	}
};
public void recognizeStream(){
	
	mIatResults.clear();
	// ���ò���
	setParam();
	// ������Ƶ��ԴΪ�ⲿ�ļ�
	//mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
	// Ҳ��������������ֱ��������Ƶ�ļ�·��ʶ��Ҫ�������ļ���sdcard�ϵ�ȫ·������
	 mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
	 mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "/fzpzz.pcm");
	 ret = mIat.startListening(mRecognizerListener);
	if (ret != ErrorCode.SUCCESS) {
		//showTip("ʶ��ʧ��,�����룺" + ret);
		System.out.println("ʶ��ʧ��,�����룺" + ret);
	} 
}
/**
 * ��д��������
 */
private RecognizerListener mRecognizerListener = new RecognizerListener() {

	@Override
	public void onBeginOfSpeech() {
		// �˻ص���ʾ��sdk�ڲ�¼�����Ѿ�׼�����ˣ��û����Կ�ʼ��������
		//showTip("��ʼ˵��");
	}

	@Override
	public void onError(SpeechError error) {
		// Tips��
		// �����룺10118(��û��˵��)��������¼����Ȩ�ޱ�������Ҫ��ʾ�û���Ӧ�õ�¼��Ȩ�ޡ�
		// ���ʹ�ñ��ع��ܣ���ǣ���Ҫ��ʾ�û�������ǵ�¼��Ȩ�ޡ�
		//showTip(error.getPlainDescription(true));
	}

	@Override
	public void onEndOfSpeech() {
		// �˻ص���ʾ����⵽��������β�˵㣬�Ѿ�����ʶ����̣����ٽ�����������
		//showTip("����˵��");
	}

	@Override
	public void onResult(RecognizerResult results, boolean isLast) {
		Log.d(TAG, results.getResultString());
		//printResult(results);
		  printResult(results);
          System.out.println(results.getResultString());
		if (isLast) {
			// TODO ���Ľ��
        	 
	       	StringBuffer resultBuffer = new StringBuffer();
			for (String key : mIatResults.keySet()) {
				resultBuffer.append(mIatResults.get(key));
			}
			Message message=new Message();
			Bundle r=new Bundle();
			r.putString("reasult", resultBuffer.toString());
			message.setData(r);
	        mHandler.sendMessage(message);
	   
			//mResultText.setSelection(mResultText.length());
		}
	}
	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// ��ȡjson����е�sn�ֶ�
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

	
	}
	@Override
	public void onVolumeChanged(int volume, byte[] data) {
		//showTip("��ǰ����˵����������С��" + volume);
		Log.d(TAG, "������Ƶ���ݣ�"+data.length);
	}

	@Override
	public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		// ���´������ڻ�ȡ���ƶ˵ĻỰid����ҵ�����ʱ���Ựid�ṩ������֧����Ա�������ڲ�ѯ�Ự��־����λ����ԭ��
		// ��ʹ�ñ����������ỰidΪnull
		//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
		//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
		//		Log.d(TAG, "session id =" + sid);
		//	}
	}
};


public void setParam() {
	// ��ղ���
	mIat.setParameter(SpeechConstant.PARAMS, null);

	// ������д����
	mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
	// ���÷��ؽ����ʽ
	mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

//	String lag = mSharedPreferences.getString("iat_language_preference",
//			"mandarin");
	String lag="zh_cn";
	if (lag.equals("en_us")) {
		// ��������
		mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
	} else {
		// ��������
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// ������������
		mIat.setParameter(SpeechConstant.ACCENT, lag);
	}

//	// ��������ǰ�˵�:������ʱʱ�䣬���û��೤ʱ�䲻˵��������ʱ����
	mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
//	
//	// ����������˵�:��˵㾲�����ʱ�䣬���û�ֹͣ˵���೤ʱ���ڼ���Ϊ�������룬 �Զ�ֹͣ¼��
	mIat.setParameter(SpeechConstant.VAD_EOS, "10000");
//	
//	// ���ñ�����,����Ϊ"0"���ؽ���ޱ��,����Ϊ"1"���ؽ���б��
	//mIat.setParameter(SpeechConstant.ASR_PTT,"1");
	
	// ������Ƶ����·����������Ƶ��ʽ֧��pcm��wav������·��Ϊsd����ע��WRITE_EXTERNAL_STORAGEȨ��
	// ע��AUDIO_FORMAT���������Ҫ���°汾������Ч
	//mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"pcm");
	//mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "/fzpzz.pcm");
}


}
