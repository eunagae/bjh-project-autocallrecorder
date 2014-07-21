package kr.bae.autocallrecoder.recorder;

import kr.bae.autocallrecoder.SQLiteDataBase.PhoneDAO;
import android.content.Context;
import android.media.MediaPlayer;

public class SaveRecordPlayer {
	private String mFileName;
	private MediaPlayer mPlayer;
	private String mPhoneNum;
	private int mDuration;
	private Context mContext;
	private PhoneDAO dao;
	
	public SaveRecordPlayer(String phoneNumber, String fileName, Context context) {
		this.mFileName = fileName;
		this.mPhoneNum = phoneNumber;
		this.mContext = context;
		dao = PhoneDAO.open(mContext);
		initMediaPlayer(GetFilePath());
	}
	
	public MediaPlayer getmPlayer() {
		return mPlayer;
	}

	private void initMediaPlayer(String fileName) {
		if (mPlayer == null)
			mPlayer = new MediaPlayer();
		else
			mPlayer.reset();

		try {
			mPlayer.setDataSource(fileName);
			mPlayer.prepare();
			mDuration = mPlayer.getDuration();
		} catch (Exception e) {
		}
	}
	
	public int getDuration() {
		return mDuration;
	}

	public void startPlay() {
		if (mPlayer == null) {
			return;
		} 
		
		mPlayer.start();
	}

	public void stopPlay() {
		if (mPlayer == null) {
			return;
		}

		mPlayer.stop();
		releaseMediaPlayer();
	}
	
	public void pausePlay() {
		if (mPlayer == null) {
			return;
		}
		mPlayer.pause();
	}
	
	public void releaseMediaPlayer() {
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}

		initMediaPlayer(GetFilePath());
	}

	public String GetFilePath() {
		String filePath = "/SaveCallRecorder/" + mPhoneNum + "/";
		String dir = dao.selectPath(mFileName) + filePath;
		String path = dir + mFileName;
		// 파일 경로 반환
		return path;
	}
}


