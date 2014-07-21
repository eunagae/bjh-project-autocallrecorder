package kr.bae.autocallrecoder.common;

public class DBInfo {
	private String phoneNumber;
	private String sendReceive;
	private String fileName;
	private String memo;
	private String date;
	private String path;
	private int duration;
	
	public DBInfo(String phoneNumber, String fileName, String sendReceive,
			String memo, String date, String path, int duration) {
		super();
		this.phoneNumber = phoneNumber;
		this.fileName = fileName;
		this.sendReceive = sendReceive;
		this.memo = memo;
		this.date = date;
		this.path = path;
		this.duration = duration;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setName(String fileNname) {
		this.fileName = fileNname;
	}

	public String getSendReceive() {
		return sendReceive;
	}

	public void setSendReceive(String sendReceive) {
		this.sendReceive = sendReceive;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
