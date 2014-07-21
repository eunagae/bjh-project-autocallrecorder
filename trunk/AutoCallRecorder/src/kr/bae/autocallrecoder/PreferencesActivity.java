package kr.bae.autocallrecoder;

import kr.bae.autocallrecoder.fileexplorer.FileExploreActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Preference initPref = (Preference) findPreference("prefinitailize");
		initPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this); 
				builder.setTitle("설정 초기화")
				.setMessage("설정을 초기화 하시겠습니까?")
				.setCancelable(false)
				.setPositiveButton(
					"확인",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int whichButton) {
							setInitSettings();
							Intent intent = getIntent();
						    finish();
						    startActivity(intent);
						}
					})
				.setNegativeButton(
					"취소",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
						}
					});
				AlertDialog dialog = builder.create(); // 알림창 객체 생성
				dialog.show(); // 알림창 띄우기
				return false;
			}
		});
		
		Preference filePref = (Preference) findPreference("preffileexplore");
		filePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(PreferencesActivity.this, FileExploreActivity.class));
				return false;
			}
		});
		
		Preference fileInitPref = (Preference) findPreference("preffileinit");
		fileInitPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder builder = new AlertDialog.Builder(PreferencesActivity.this); 
				builder.setTitle("경로 초기화")
				.setMessage("경로를 초기화 하시겠습니까?")
				.setCancelable(false)
				.setPositiveButton(
					"확인",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,	int whichButton) {
							saveFilePreferences("/storage/sdcard0");
							Intent intent = getIntent();
						    finish();
						    startActivity(intent);
						}
					})
				.setNegativeButton(
					"취소",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.cancel();
						}
					});
				AlertDialog dialog = builder.create(); // 알림창 객체 생성
				dialog.show(); // 알림창 띄우기
				return false;
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionswitchmenu, menu);

		MenuItem check = menu.findItem(R.id.onoffswitch);
		Switch sw = (Switch) check.getActionView();
		sw.setChecked(getCheckPreferences());
		sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				savePreferences(isChecked);
				if (isChecked)
					Toast.makeText(getApplicationContext(), "자동 녹음이 실행됩니다.",
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getApplicationContext(), "자동 녹음이 해제됩니다.",
							Toast.LENGTH_SHORT).show();
			}
		});
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// 값 저장하기
    private void savePreferences(boolean isChecked){
        SharedPreferences pref = getSharedPreferences("preRecordCheck", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("Check", isChecked);
        editor.commit();
    }
    
    // 녹음 on off 여부 불러오기
    private boolean getCheckPreferences() {
    	SharedPreferences pref = getSharedPreferences("preRecordCheck", MODE_PRIVATE);
        boolean isCheck = pref.getBoolean("Check", true);
        return isCheck;
    }
    
    private void setInitSettings() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("preaudiosource", "1");
		editor.putString("preoutputformat", "0");
		editor.putBoolean("prefnotify", true);
		editor.putString("prefsavefile", "1");
		editor.putString("prefexecutetime", "1");
		editor.commit();
	}
    
    // 저장 경로 저장하기
    private void saveFilePreferences(String path){
        SharedPreferences pref = getSharedPreferences("fileexplore", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("path", path);
        editor.commit();
    }
}
