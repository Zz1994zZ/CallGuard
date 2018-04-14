package com.zz.safeCall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.iflytek.tool.XFRecognizer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioFileFunc {
	private int bufferSizeInBytes = 0;
	// AudioName裸音频数据文件 ，麦克风
	private String AudioName = "";
	// NewAudioName可播放的音频文件
	private String NewAudioName = "";
	private AudioRecord audioRecord;
	private boolean isRecord = false;// 设置正在录制的状态
	public static int AUDIO_SAMPLE_RATE = 8000;
	private static AudioFileFunc mInstance;
	private static String mRawFilePath;
	private LocalService localService;
	private  AudioFileFunc(LocalService localService) {
		// TODO Auto-generated constructor stub
		this.localService=localService;
	}

	public synchronized static AudioFileFunc getInstance(LocalService localService) {
		if (mInstance == null)
			mInstance = new AudioFileFunc(localService);
		return mInstance;
	}

	public int startRecordAndFile() {
		// 判断是否有外部存储设备sdcard
		if (AudioFileFunc.isSdcardExit()) {
			if (isRecord) {
				return ErrorCode.E_STATE_RECODING;
			} else {
				if (audioRecord == null)
					creatAudioRecord();

				audioRecord.startRecording();
				// 让录制状态为true
				isRecord = true;
				// 开启音频文件写入线程
				new Thread(new AudioRecordThread()).start();

				return ErrorCode.SUCCESS;
			}

		} else {
			return ErrorCode.E_NOSDCARD;
		}

	}

	public void stopRecordAndFile() {
		close();
	}

	public long getRecordFileSize() {
		return AudioFileFunc.getFileSize(NewAudioName);
	}

	private void close() {
		if (audioRecord != null) {
			System.out.println("stopRecord");
			isRecord = false;// 停止文件写入
			audioRecord.stop();
			audioRecord.release();// 释放资源
			audioRecord = null;
		}
	}

	private void creatAudioRecord() {
		// 获取音频文件路径
		AudioName = AudioFileFunc.getRawFilePath();

		// 获得缓冲区字节大小
		bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		// 创建AudioRecord对象
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
	
	}

	class AudioRecordThread implements Runnable {
		@Override
		public void run() {
			writeDateTOFile();// 往文件中写入裸数据
			//copyWaveFile(AudioName, NewAudioName);// 给裸数据加上头文件
		}
	}

	/**
	 * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
	 * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
	 * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
	 */
	private void writeDateTOFile() {
		// new一个byte数组用来存一些字节数据，大小为缓冲区大小
		byte[] audiodata = new byte[bufferSizeInBytes];
		FileOutputStream fos = null;
		int readsize = 0;
		try {
			File file = new File(AudioName);
			if (file.exists()) {
				file.delete();
			}
			fos = new FileOutputStream(file);// 建立一个可存取字节的文件
		} catch (Exception e) {
			e.printStackTrace();
		}
			while (isRecord == true) {
				readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
				if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
					try {
						//这里添加识别
						fos.write(audiodata);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				if (fos != null)
					fos.close();// 关闭写入流
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("写入录音完毕 path:"+AudioName);
			XFRecognizer xfr=new XFRecognizer(localService,localService.getHandler());
			xfr.recognizeStream();
			//localService.jump();//跳转ACITIVITY
	}


	public static boolean isSdcardExit() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	public static void  setRawFilePath(String rawFilePath){
		mRawFilePath = rawFilePath;
	}
	
	public static String getRawFilePath() {
		return mRawFilePath;
	}

	public static long getFileSize(String file) {
		return (new File(file)).length();
	}
	

	

}
