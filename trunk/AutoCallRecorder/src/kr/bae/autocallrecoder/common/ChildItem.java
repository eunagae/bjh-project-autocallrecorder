package kr.bae.autocallrecoder.common;

import java.text.SimpleDateFormat;

public class ChildItem {
	private String mFileName;
	private String mSendReceive;
	private String mMemo;
	private int mDuration;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
	private String durationFormat;
	
	public ChildItem(String sendReceive, String fileName, String memo, int duration) {
		this.mSendReceive = sendReceive;
		this.mFileName = fileName;
		this.mMemo = memo;
		this.mDuration = duration;
		this.durationFormat = dateFormat.format(mDuration);
	}

	public String getmFileName() {
		return mFileName;
	}

	public void setmFileName(String mFileName) {
		this.mFileName = mFileName;
	}

	public String getmSendReceive() {
		return mSendReceive;
	}

	public void setmSendReceive(String mSendReceive) {
		this.mSendReceive = mSendReceive;
	}

	public String getmMemo() {
		return mMemo;
	}

	public void setmMemo(String mMemo) {
		this.mMemo = mMemo;
	}

	public String getDurationFormat() {
		return durationFormat;
	}

	public void setDurationFormat(String durationFormat) {
		this.durationFormat = durationFormat;
	}
}
