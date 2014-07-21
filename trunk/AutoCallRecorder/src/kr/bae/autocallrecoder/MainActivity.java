package kr.bae.autocallrecoder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kr.bae.autocallrecoder.SQLiteDataBase.PhoneDAO;
import kr.bae.autocallrecoder.common.DelayRecord;
import kr.bae.autocallrecoder.viewpager.MyFragmentPagerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
	private PhoneDAO dao;
	private List<DelayRecord> recLists;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (getFileSaveTime() == 0) {
			dao = PhoneDAO.open(this);
			dao.updateDelayFile(addDay(getFileSaveTime())); // 삭제하기위해 visible 0으로 업데이트
			recLists = dao.selectDelayFile(addDay(getFileSaveTime())); // 지난거 검색해서 폴더에 있는거 삭제할라고
			for (int i = 0; i < recLists.size(); i++) {
				deleteRecordFile(recLists.get(i).getPhoneNum(), recLists.get(i).getFileName());
			}
			dao.deleteDelayFile(addDay(getFileSaveTime())); // 지난거 디비 삭제
		}
		
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fm = getSupportFragmentManager();
        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(fm, this);
        pager.setAdapter(pagerAdapter);
        
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionviewmenu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_preferences:
			Intent i = new Intent(getApplicationContext(), PreferencesActivity.class);
			startActivity(i);
			break;
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		Log.d("aaa", "onDestroy");
		removeAllPreferences();
		super.onDestroy();
	}
	
	// 값(ALL Data) 삭제하기
    private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("image_select", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
    
    private int getFileSaveTime() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		int day = Integer.parseInt(sharedPrefs.getString("prefsavefile", "1"));
		switch (day) {
		case 0: day = -10; break;
		case 1: day = -30; break;
		case 2: day = -100; break;
		default: day = 0; break;
		}
		return day;
	}
    
    private static String addDay(int num) {
    	Log.d("aaa", "int num = " + num);
	    Date date = new Date();
	    date.setHours(date.getDay()+num*24);
	    return getFormatDate("yyMMdd", date);
	}
	
	// 날짜 포맷하기
	private static String getFormatDate(String dateFormat, Date date) {
	    SimpleDateFormat format = new SimpleDateFormat(dateFormat);
	    return format.format(date);
	}
	
	private void deleteRecordFile(String phonNum, String fileName) {
		String sdcard = Environment.getExternalStorageState();
		File file = null;

		if (!sdcard.equals(Environment.MEDIA_MOUNTED)) {
			// SD카드가 마운트되어있지 않음
			file = Environment.getRootDirectory();
		} else {
			// SD카드가 마운트되어있음
			file = Environment.getExternalStorageDirectory();
		}

		String dir = file.getAbsolutePath() + "/AutoCallRecorder/" + phonNum
				+ "/" + fileName;
		File f = new File(dir);
		if (f.exists())
			f.delete();
		
		String dir2 = file.getAbsolutePath() + "/AutoCallRecorder/" + phonNum;
		File f2 = new File(dir2);
		if(f2.exists()) {
			f2.delete();  //root 삭제
		}
	}
}
