package kr.bae.autocallrecoder.SQLiteDataBase;

import java.util.ArrayList;
import java.util.List;

import kr.bae.autocallrecoder.common.ChildItem;
import kr.bae.autocallrecoder.common.DBInfo;
import kr.bae.autocallrecoder.common.DelayRecord;
import kr.bae.autocallrecoder.common.GroupItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PhoneDAO {
	private PhoneDBHelper helper;
	private SQLiteDatabase db;
	private static final String TABLE_NAME = "phone";
	
	// 싱글턴 패턴으로 생성자 생성
	private PhoneDAO(Context context){
		helper = new PhoneDBHelper(context);			// DB버전에 해당하는 데이터베이스 생성
		db = helper.getWritableDatabase();
	}
	
	public static PhoneDAO open(Context context){
		return new PhoneDAO(context);
	}
	
	public void close(){
		helper.close();
	}
	
	// 추가
	public boolean insert(DBInfo info){
		ContentValues row = new ContentValues();
		row.put("phonenum", info.getPhoneNumber());
		row.put("sendreceive", info.getSendReceive());
		row.put("filename", info.getFileName());
		row.put("memo", info.getMemo());
		row.put("date", info.getDate());
		row.put("duration", String.valueOf(info.getDuration()));
		
		long res = db.insert(TABLE_NAME, null, row);
		if(res == -1)
			return false;
		else
			return true;
	}
	
	// 전체 삭제
	public boolean deleteAll(){
		db.delete(TABLE_NAME, null, null);
		return true;
	}
	
	// Group 삭제
	public boolean deleteGroup(String phoneNum){
		String sql = "delete from " + TABLE_NAME + " where phonenum = '"+ phoneNum + "' and save = 0 and visible = 0;";
		try{
			db.execSQL(sql);
		} catch(SQLException e){
			return false;
		}
		return true;
	}
	// Child 삭제
	public boolean deleteChild(String fileName){
		String sql = "delete from " + TABLE_NAME +" where filename = '"+ fileName +"' and save = 0 and visible = 0;";	
		try{
			db.execSQL(sql);
		} catch(SQLException e){
			return false;
		}
		return true;
	}
	
	// 기간이 지난 녹음 정보 삭제
	public boolean deleteDelayFile(String date){
		String sql = "delete from " + TABLE_NAME +" where date = '"+ date +"';";	
		try{
			db.execSQL(sql);
		} catch(SQLException e){
			return false;
		}
		return true;
	}
	
	// 즐겨찾기
	public List<DelayRecord> selectDelayFile(String date) {
		List<DelayRecord> record = new ArrayList<>();
		db = helper.getReadableDatabase();

		String sql = "select phonenum, filename from " + TABLE_NAME + " where date = '"+ date +"' and save = 0 and visible = 0;";
		Cursor cursor = db.rawQuery(sql, null); // args는 리턴타입이 들어감.

		if (cursor.moveToFirst()) {
			do {
				record.add(new DelayRecord(cursor.getString(0), cursor.getString(1)));
			} while (cursor.moveToNext());
		}

		cursor.close();

		return record;
	}
	
	// visible 0으로 갱신
	public boolean updateDelayFile(String date) {
		db = helper.getWritableDatabase();
		String sql = "update " + TABLE_NAME + " set visible = 0 where date = '" + date + "';";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	// groupView에 표시할 데이터 받아오기
	public List<GroupItem> selectGroupItem() {
		List<GroupItem> groupItems = new ArrayList<>();
		db = helper.getReadableDatabase();
		
		//view table
		String sql = "select * from group_list;";
		Cursor cursor = db.rawQuery(sql, null);					// args는 리턴타입이 들어감.
		
		if(cursor.moveToFirst()) {
			do {
				groupItems.add(new GroupItem(cursor.getString(0), 
						Integer.parseInt(cursor.getString(1)), 
						Integer.parseInt(cursor.getString(2))
						));
				
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		
		return groupItems;
	}
	
	// 즐겨찾기
	public List<GroupItem> selectFavoriteItem() {
		List<GroupItem> groupItems = new ArrayList<>();
		db = helper.getReadableDatabase();
		
		//view table
		String sql = "select * from group_list2;";
		Cursor cursor = db.rawQuery(sql, null);					// args는 리턴타입이 들어감.
		
		if(cursor.moveToFirst()) {
			do {
				groupItems.add(new GroupItem(cursor.getString(0), 
						Integer.parseInt(cursor.getString(1)), 
						Integer.parseInt(cursor.getString(2))
						));
				
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		
		return groupItems;
	}
	
	// 저장
	public List<GroupItem> selectSaveGroupItem() {
		List<GroupItem> groupItems = new ArrayList<>();
		db = helper.getReadableDatabase();
		
		//view table
		String sql = "select * from group_list3;";
		Cursor cursor = db.rawQuery(sql, null);					// args는 리턴타입이 들어감.
		/*= "create table phone ("
				+ "_id integer primary key autoincrement," + "phonenum text,"
				+ "sendreceive text," + "filename text," + "memo text,"
				+ "date text," + "path text," + "duration text,"+ "favorite integer default 0," + "save integer default 0," + "visible integer default 1" + ");";*/
		if(cursor.moveToFirst()) {
			do {
				groupItems.add(new GroupItem(cursor.getString(0), 
						Integer.parseInt(cursor.getString(1)), 
						Integer.parseInt(cursor.getString(2))
						));
				
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		
		return groupItems;
	}
	
	// childView에 표시할 데이터 받아오기
	public List<ChildItem> selectChildItem(String phoneNum) {
		List<ChildItem> childItems = new ArrayList<>();
		db = helper.getReadableDatabase();
		
		String sql = "select sendreceive, filename, memo, duration from phone where phonenum = '" + phoneNum + "' and visible = 1;";
		Cursor cursor = db.rawQuery(sql, null);
		
		if(cursor.moveToFirst()) {
			do {
				childItems.add(new ChildItem(
						cursor.getString(0), 
						cursor.getString(1), 
						cursor.getString(2),
						Integer.parseInt(cursor.getString(3))));
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		
		return childItems;
	}
	
	// childView 즐겨찾기
	public List<ChildItem> selectFavoriteChildItem(String phoneNum) {
		List<ChildItem> childItems = new ArrayList<>();
		db = helper.getReadableDatabase();
		
		String sql = "select sendreceive, filename, memo, duration from phone where phonenum = '" + phoneNum + "' and favorite = 1;";
		Cursor cursor = db.rawQuery(sql, null);
				
		if(cursor.moveToFirst()) {
			do {
				childItems.add(new ChildItem(
						cursor.getString(0), 
						cursor.getString(1), 
						cursor.getString(2),
						Integer.parseInt(cursor.getString(3))));
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		
		return childItems;
	}
	
	// childView 저장
	public List<ChildItem> selectSaveChildItem(String phoneNum) {
		List<ChildItem> childItems = new ArrayList<>();
		db = helper.getReadableDatabase();
		
		String sql = "select sendreceive, filename, memo, duration from phone where phonenum = '" + phoneNum + "' and save = 1;";
		Cursor cursor = db.rawQuery(sql, null);
		
		if(cursor.moveToFirst()) {
			do {
				childItems.add(new ChildItem(
						cursor.getString(0), 
						cursor.getString(1), 
						cursor.getString(2),
						Integer.parseInt(cursor.getString(3))));
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		
		return childItems;
	}
	
	// memo 갱신
	public boolean updateMemo(String fileName, String memo) {
		db = helper.getWritableDatabase();
		String sql = "update " + TABLE_NAME + " set memo = '" + memo
				+ "' where filename = '" + fileName + "';";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;

	}
	
	public String selectMemo(String fileName) {
		db = helper.getReadableDatabase();
		String sql = "select memo from " + TABLE_NAME + " where filename = '" + fileName + "'";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		String path = cursor.getString(0);
		cursor.close();
		return path;
	}
	
	// visible 갱신
	public boolean updateVisible(String fileName, int visible) {
		db = helper.getWritableDatabase();
		String sql = "update " + TABLE_NAME + " set visible = " + visible
				+ " where filename = '" + fileName + "';";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;
 
	}
	
	// 즐겨찾기 갱신
	public boolean updateFavorite(String fileName, int favorite) {
		db = helper.getWritableDatabase();
		String sql = "update " + TABLE_NAME + " set favorite = " + favorite
				+ " where filename = '" + fileName + "';";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	// 저장 갱신
	public boolean updateSave(String fileName, int save) {
		db = helper.getWritableDatabase();
		String sql = "update " + TABLE_NAME + " set save = " + save
				+ " where filename = '" + fileName + "';";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	// 그룹 visible 갱신
	public boolean updateGroupVisible(String phoneNum) {
		db = helper.getWritableDatabase();
		String sql = "update " + TABLE_NAME + " set visible = 0" +
				" where phonenum = '" + phoneNum + "';";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	public boolean updateGroupSave(String phoneNum, int mode) {
		db = helper.getWritableDatabase();
		String sql = "update " + TABLE_NAME + " set save = "+ mode +
				" where phonenum = '" + phoneNum + "';";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	public boolean updateGroupFavorite(String phoneNum) {
		db = helper.getWritableDatabase();
		String sql = "update " + TABLE_NAME + " set favorite = 0" +
				" where phonenum = '" + phoneNum + "';";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	public String selectPath(String fileName) {
		db = helper.getReadableDatabase();
		String sql = "select path from " + TABLE_NAME + " where filename = '" + fileName + "'";
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		String path = cursor.getString(0);
		cursor.close();
		return path;
	}
	
	public String selectPathAll(String phoneNum) {
		db = helper.getReadableDatabase();
		String sql = "select path from " + TABLE_NAME + " where phoneNum = '" + phoneNum + "'";
		Cursor cursor = db.rawQuery(sql, null);
		String path = null;
	
		if(cursor.moveToFirst()) {
			do {
				path = cursor.getString(0);				
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		return path;
	}
	
	public boolean updatePath(String path, String fileName) {
		db = helper.getWritableDatabase();
		String sql = "update " + TABLE_NAME + " set path = '"+ path +
				"' where filename = '" + fileName + "';";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	public boolean updatePathAll(String path, String phoneNum) {
		db = helper.getWritableDatabase();
		String sql = "update " + TABLE_NAME + " set path = '"+ path +
				"' where phonenum = '" + phoneNum + "';";
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
}
