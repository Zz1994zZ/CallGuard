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
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	// 语音听写对象
		private SpeechRecognizer mIat;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private Toast mToast;
	private Context  context;
	int ret = 0; // 函数调用返回值
	private Handler mHandler;
public XFRecognizer(Context  context,Handler mHandler){
	this.context=context;
	this.mHandler=mHandler;
	// 初始化识别无UI识别对象
	// 使用SpeechRecognizer对象，可根据回调消息自定义界面；
	mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
}
/**
 * 初始化监听器。
 */
private InitListener mInitListener = new InitListener() {

	@Override
	public void onInit(int code) {
		Log.d(TAG, "SpeechRecognizer init() code = " + code);
		if (code != ErrorCode.SUCCESS) {
			//showTip("初始化失败，错误码：" + code);
			System.out.println("初始化失败，错误码：" + code);
		}
	}
};
public void recognizeStream(){
	
	mIatResults.clear();
	// 设置参数
	setParam();
	// 设置音频来源为外部文件
	//mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
	// 也可以像以下这样直接设置音频文件路径识别（要求设置文件在sdcard上的全路径）：
	 mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
	 mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "/fzpzz.pcm");
	 ret = mIat.startListening(mRecognizerListener);
	if (ret != ErrorCode.SUCCESS) {
		//showTip("识别失败,错误码：" + ret);
		System.out.println("识别失败,错误码：" + ret);
	} 
}
/**
 * 听写监听器。
 */
private RecognizerListener mRecognizerListener = new RecognizerListener() {

	@Override
	public void onBeginOfSpeech() {
		// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
		//showTip("开始说话");
	}

	@Override
	public void onError(SpeechError error) {
		// Tips：
		// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
		// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
		//showTip(error.getPlainDescription(true));
	}

	@Override
	public void onEndOfSpeech() {
		// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
		//showTip("结束说话");
	}

	@Override
	public void onResult(RecognizerResult results, boolean isLast) {
		Log.d(TAG, results.getResultString());
		//printResult(results);
		  printResult(results);
          System.out.println(results.getResultString());
		if (isLast) {
			// TODO 最后的结果
        	 
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
		// 读取json结果中的sn字段
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
		//showTip("当前正在说话，音量大小：" + volume);
		Log.d(TAG, "返回音频数据："+data.length);
	}

	@Override
	public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
		// 若使用本地能力，会话id为null
		//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
		//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
		//		Log.d(TAG, "session id =" + sid);
		//	}
	}
};


public void setParam() {
	// 清空参数
	mIat.setParameter(SpeechConstant.PARAMS, null);

	// 设置听写引擎
	mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
	// 设置返回结果格式
	mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

//	String lag = mSharedPreferences.getString("iat_language_preference",
//			"mandarin");
	String lag="zh_cn";
	if (lag.equals("en_us")) {
		// 设置语言
		mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
	} else {
		// 设置语言
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置语言区域
		mIat.setParameter(SpeechConstant.ACCENT, lag);
	}

//	// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
	mIat.setParameter(SpeechConstant.VAD_BOS, "10000");
//	
//	// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
	mIat.setParameter(SpeechConstant.VAD_EOS, "10000");
//	
//	// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
	//mIat.setParameter(SpeechConstant.ASR_PTT,"1");
	
	// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
	// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
	//mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"pcm");
	//mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "/fzpzz.pcm");
}


}
