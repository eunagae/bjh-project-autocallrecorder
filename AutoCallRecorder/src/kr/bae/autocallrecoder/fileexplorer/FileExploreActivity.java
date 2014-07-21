package kr.bae.autocallrecoder.fileexplorer;

import kr.bae.autocallrecoder.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileExploreActivity extends Activity {
	String mPath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fileexplorer);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        FileList _FileList = new FileList(this);
        _FileList.setOnPathChangedListener(_OnPathChanged);
        _FileList.setOnFileSelected(_OnFileSelected);
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.LinearLayout01);
        layout.addView(_FileList);
        
        _FileList.setPath(getPreferences());
        _FileList.setFocusable(true);
        _FileList.setFocusableInTouchMode(true);       
        
    }
    
    private OnPathChangedListener _OnPathChanged = new OnPathChangedListener() {
		@Override
		public void onChanged(String path) {
			mPath = path;
			((TextView) findViewById(R.id.TextView01)).setText(path);
		}
	};
    
    private OnFileSelectedListener _OnFileSelected = new OnFileSelectedListener() {
		@Override
		public void onSelected(String path, String fileName) {
			// TODO
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionviewfile, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_fileexplore:
			savePreferences(mPath);
			finish();
			break;
			
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// 저장 경로 저장하기
    private void savePreferences(String path){
        SharedPreferences pref = getSharedPreferences("fileexplore", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("path", path);
        editor.commit();
    }
    
    // 저장 경로 불러오기
    private String getPreferences(){
        SharedPreferences pref = getSharedPreferences("fileexplore", MODE_PRIVATE);
        String path = pref.getString("path", "/storage/sdcard0");
		return path;
    }
    
}