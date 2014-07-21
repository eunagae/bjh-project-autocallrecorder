package kr.bae.autocallrecoder.common;

public class GroupItem {
	private String mPhoneNumber;
	private int mSendCnt;
	private int mReceiveCnt;
	
	public GroupItem(String mPhoneNumber, int mSendCnt, int mReceiveCnt) {
		super();
		this.mPhoneNumber = mPhoneNumber;
		this.mSendCnt = mSendCnt;
		this.mReceiveCnt = mReceiveCnt;
	}

	public String getmPhoneNumber() {
		return mPhoneNumber;
	}

	public void setmPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}

	public int getmSendCnt() {
		return mSendCnt;
	}

	public void setmSendCnt(int mSendCnt) {
		this.mSendCnt = mSendCnt;
	}

	public int getmReceiveCnt() {
		return mReceiveCnt;
	}

	public void setmReceiveCnt(int mReceiveCnt) {
		this.mReceiveCnt = mReceiveCnt;
	}
}
