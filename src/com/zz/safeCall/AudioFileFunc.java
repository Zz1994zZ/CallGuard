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
	// AudioName����Ƶ�����ļ� ����˷�
	private String AudioName = "";
	// NewAudioName�ɲ��ŵ���Ƶ�ļ�
	private String NewAudioName = "";
	private AudioRecord audioRecord;
	private boolean isRecord = false;// ��������¼�Ƶ�״̬
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
		// �ж��Ƿ����ⲿ�洢�豸sdcard
		if (AudioFileFunc.isSdcardExit()) {
			if (isRecord) {
				return ErrorCode.E_STATE_RECODING;
			} else {
				if (audioRecord == null)
					creatAudioRecord();

				audioRecord.startRecording();
				// ��¼��״̬Ϊtrue
				isRecord = true;
				// ������Ƶ�ļ�д���߳�
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
			isRecord = false;// ֹͣ�ļ�д��
			audioRecord.stop();
			audioRecord.release();// �ͷ���Դ
			audioRecord = null;
		}
	}

	private void creatAudioRecord() {
		// ��ȡ��Ƶ�ļ�·��
		AudioName = AudioFileFunc.getRawFilePath();

		// ��û������ֽڴ�С
		bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		// ����AudioRecord����
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
	
	}

	class AudioRecordThread implements Runnable {
		@Override
		public void run() {
			writeDateTOFile();// ���ļ���д��������
			//copyWaveFile(AudioName, NewAudioName);// �������ݼ���ͷ�ļ�
		}
	}

	/**
	 * ���ｫ����д���ļ������ǲ����ܲ��ţ���ΪAudioRecord��õ���Ƶ��ԭʼ������Ƶ��
	 * �����Ҫ���žͱ������һЩ��ʽ���߱����ͷ��Ϣ�����������ĺô���������Զ���Ƶ�� �����ݽ��д���������Ҫ��һ����˵����TOM
	 * è������ͽ�����Ƶ�Ĵ���Ȼ�����·�װ ����˵�����õ�����Ƶ�Ƚ�������һЩ��Ƶ�Ĵ���
	 */
	private void writeDateTOFile() {
		// newһ��byte����������һЩ�ֽ����ݣ���СΪ��������С
		byte[] audiodata = new byte[bufferSizeInBytes];
		FileOutputStream fos = null;
		int readsize = 0;
		try {
			File file = new File(AudioName);
			if (file.exists()) {
				file.delete();
			}
			fos = new FileOutputStream(file);// ����һ���ɴ�ȡ�ֽڵ��ļ�
		} catch (Exception e) {
			e.printStackTrace();
		}
			while (isRecord == true) {
				readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
				if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
					try {
						//�������ʶ��
						fos.write(audiodata);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				if (fos != null)
					fos.close();// �ر�д����
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("д��¼����� path:"+AudioName);
			XFRecognizer xfr=new XFRecognizer(localService,localService.getHandler());
			xfr.recognizeStream();
			//localService.jump();//��תACITIVITY
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
