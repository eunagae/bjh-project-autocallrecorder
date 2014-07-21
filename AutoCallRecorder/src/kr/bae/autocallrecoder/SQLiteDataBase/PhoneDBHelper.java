package kr.bae.autocallrecoder.SQLiteDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhoneDBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "autocall.db";  
    private static final int DB_VER = 1;  
    
	public PhoneDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VER);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//table 생성
		String sql = "create table phone ("
				+ "_id integer primary key autoincrement," + "phonenum text,"
				+ "sendreceive text," + "filename text," + "memo text,"
				+ "date text," + "path text," + "duration text,"+ "favorite integer default 0," + "save integer default 0," + "visible integer default 1" + ");";
		db.execSQL(sql);
		
		//groupview에서 사용(View table 생성)
		String view_sql = "create view group_list as "
				+ "select "
				+ "phonenum as 'phonenumber', "
				+ "(select count(phonenum) from phone as p1 where p1.phonenum = p.phonenum and sendreceive = 'out' and visible = 1) as 'out', "
				+ "(select count(phonenum) from phone as p1 where p1.phonenum = p.phonenum and sendreceive = 'in' and visible = 1) as 'in' "
				+ "from phone as p " 
				+ "where visible = 1 " 
				+ "group by phonenum;";
		db.execSQL(view_sql);	
		
		//즐겨 찾기 사용
		String view_sql2 = "create view group_list2 as "
				+ "select "
				+ "phonenum as 'phonenumber', "
				+ "(select count(phonenum) from phone as p1 where p1.phonenum = p.phonenum and sendreceive = 'out' and favorite = 1 and visible = 1) as 'out', "
				+ "(select count(phonenum) from phone as p1 where p1.phonenum = p.phonenum and sendreceive = 'in' and favorite = 1 and visible = 1) as 'in' "
				+ "from phone as p "
				+ "where favorite = 1 "
				+ "and visible = 1 " 
				+ "group by phonenum;";
		db.execSQL(view_sql2);
		
		//저장 사용
		String view_sql3 = "create view group_list3 as "
				+ "select "
				+ "phonenum as 'phonenumber', "
				+ "(select count(phonenum) from phone as p1 where p1.phonenum = p.phonenum and sendreceive = 'out' and save = 1) as 'out', "
				+ "(select count(phonenum) from phone as p1 where p1.phonenum = p.phonenum and sendreceive = 'in' and save = 1) as 'in' "
				+ "from phone as p "
				+ "where save = 1 "
				+ "group by phonenum;";
		db.execSQL(view_sql3);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists phone;");
		onCreate(db);
	}

}
