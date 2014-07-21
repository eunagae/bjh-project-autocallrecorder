package kr.bae.autocallrecoder.common;

public class DelayRecord {
	private String phoneNum;
	private String fileName;
	
	public DelayRecord(String phoneNum, String fileName) {
		this.phoneNum = phoneNum;
		this.fileName = fileName;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "DelayRecord [phoneNum=" + phoneNum + ", fileName=" + fileName
				+ "]";
	}
}
