package kr.bae.autocallrecoder.recorder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.bae.autocallrecoder.SQLiteDataBase.PhoneDAO;
import kr.bae.autocallrecoder.common.DBInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class CallRecorder {

	private Context mContext;
	private String mFilePath, mFileName, mFilePathName, mDate;
	private SimpleDateFormat mDateFormat;
	private String mPhoneNumber;
	private String mSendReceive;
	private DBInfo info;
	private MediaRecorder mRecorder = null;
	private PhoneDAO dao;
	private String path;
	
	private int[] mOutputFormat = {MediaRecorder.OutputFormat.THREE_GPP,
			MediaRecorder.OutputFormat.AMR_NB,
			MediaRecorder.OutputFormat.MPEG_4};
	
	private String[] format = {".3gp", ".amr", ".mp4"};
	
	public CallRecorder(Context context, String phonenumber, String sendReceive) {
		this.mContext = context;
		this.mPhoneNumber = phonenumber;
		this.mSendReceive = sendReceive;
		
		dao = PhoneDAO.open(mContext);
		String dateFormat = "yy_MM_dd_HHmmss";

		mFilePath = "/AutoCallRecorder/" + mPhoneNumber + "/";
		mDateFormat = new SimpleDateFormat(dateFormat);
		mDate = mDateFormat.format(new Date()).toString();
		mFileName = mSendReceive + "_" + mDate + format[getOutputFormat()];
		mFilePathName = GetFilePath();
	}
	
	private String getDay() {
	    Date date = new Date();
	    return getFormatDate("yyMMdd", date);
	}
	
	// 날짜 포맷하기
	private static String getFormatDate(String dateFormat, Date date) {
	    SimpleDateFormat format = new SimpleDateFormat(dateFormat);
	    return format.format(date);
	}
	
	private int getPlayDuration() {
		RecordPlayer player = new RecordPlayer(mPhoneNumber, mFileName);
		return player.getDuration();
	}

	public void stopRec(int flag) {
		if(flag == 0) {
			if (mRecorder != null) {
				try {
					mRecorder.stop();
				} catch (Exception e) {
					e.getStackTrace();
				}
				mRecorder.release();
				mRecorder = null;
				insertRecord();
			}
		} else {
			if (mRecorder != null) {
				try {
					mRecorder.stop();
				} catch (Exception e) {
					e.getStackTrace();
				}
				mRecorder.release();
				mRecorder = null;
				deleteFile();
			}
		}
	}

	private void deleteFile() {
		String sdcard = Environment.getExternalStorageState();
		File file = null;

		if (!sdcard.equals(Environment.MEDIA_MOUNTED)) {
			// SD카드가 마운트되어있지 않음
			file = Environment.getRootDirectory();
		} else {
			// SD카드가 마운트되어있음
			file = Environment.getExternalStorageDirectory();
		}

		String dir = file.getAbsolutePath() + "/AutoCallRecorder/" + mPhoneNumber
				+ "/" + mFileName;
		File f = new File(dir);
		if (f.exists())
			f.delete();

		String dir2 = file.getAbsolutePath() + "/AutoCallRecorder/" + mPhoneNumber;
		File f2 = new File(dir2);
		File[] childFileList = f2.listFiles();
		if (childFileList.length == 0)
			f2.delete(); // root 삭제
	}


	private void insertRecord() {
		// db에 저장
		info = new DBInfo(mPhoneNumber, mFileName, mSendReceive, "", getDay(), path, getPlayDuration());
		boolean result = dao.insert(info);
		//String str = result ? "insert Success" : "insert False";
	}

	public void startRec() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
		mRecorder.setOutputFormat(mOutputFormat[getOutputFormat()]);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mRecorder.setOutputFile(mFilePathName);

		try {
			mRecorder.prepare();
			mRecorder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String GetFilePath() {
		String sdcard = Environment.getExternalStorageState();
		File file = null;

		if (!sdcard.equals(Environment.MEDIA_MOUNTED)) {
			// SD카드가 마운트되어있지 않음
			file = Environment.getRootDirectory();
		} else {
			// SD카드가 마운트되어있음
			file = Environment.getExternalStorageDirectory();
		}
		
		path = file.getAbsolutePath();
		String dir = path + mFilePath;
		String filePath = dir + mFileName;

		file = new File(dir);
		if (!file.exists()) {
			// 디렉토리가 존재하지 않으면 디렉토리 생성
			file.mkdirs();
		}

		// 파일 경로 반환
		return filePath;
	}
	
	private int getOutputFormat() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		int output = Integer.parseInt(pref.getString("preoutputformat", "0"));
		return output;
	}

}
