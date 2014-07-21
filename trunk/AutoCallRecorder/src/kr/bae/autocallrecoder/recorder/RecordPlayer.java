package kr.bae.autocallrecoder.recorder;

import java.io.File;

import android.media.MediaPlayer;
import android.os.Environment;

public class RecordPlayer {
	private String mFileName;
	private MediaPlayer mPlayer;
	private String mPhoneNum;
	private int mDuration;
	
	public RecordPlayer(String phoneNumber, String fileName) {
		this.mFileName = fileName;
		this.mPhoneNum = phoneNumber;
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
		String filePath = "/AutoCallRecorder/" + mPhoneNum + "/";
		String sdcard = Environment.getExternalStorageState();
		File file = null;

		if (!sdcard.equals(Environment.MEDIA_MOUNTED)) {
			// SD카드가 마운트되어있지 않음
			file = Environment.getRootDirectory();
		} else {
			// SD카드가 마운트되어있음
			file = Environment.getExternalStorageDirectory();
		}

		String dir = file.getAbsolutePath() + filePath;
		String path = dir + mFileName;

		return path;
	}
}


