package kr.bae.autocallrecoder;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class DialogActivity extends Activity {
	//CallRecorder mRecordPlay;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//mRecordPlay = new CallRecorder();
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
	            WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	    LayoutParams params = getWindow().getAttributes(); 
	    params.height = 300; //fixed height
	    params.width = 800; //fixed width
	    getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	    
		setContentView(R.layout.dialog_activity);		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.dialogactionbarmenu, menu);

		return true;
	}
	
	public void mOnClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			//mRecordPlay.startPlay("/sdcard/Download/Record.mp4");
			break;

		case R.id.close:
			//mRecordPlay.stopPlay();
			break;
		default:
			break;
		}
	}

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Toast.makeText(this, "Selected Item: " + item.getTitle(),
				Toast.LENGTH_SHORT).show();
		return true;
	}*/
}
