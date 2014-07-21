package kr.bae.autocallrecoder.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ServiceReceiver extends BroadcastReceiver {
	Context mContext;
	String mPhoneNumber;
	Intent service;
	static boolean isIdle;
	@Override
	public void onReceive(Context context, Intent received) {
		mContext = context;
		String action = received.getAction();
		Bundle bundle = received.getExtras();
		if(getCheckPreferences()) {
			if (action.equals("android.intent.action.PHONE_STATE")) {
				String state = bundle.getString(TelephonyManager.EXTRA_STATE);
				if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
					isIdle = true;
					mTask.cancel(false);
					removeAllPreferences();
					
				} else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
					mPhoneNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
					savePreferences(mPhoneNumber, new String("in"));
					
				} else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
					String[] prefStr = getPreferences();
					
					
					if(prefStr[1].equals("out")) {
						switch (getExcuteTime()) {
						case 0: mTask.execute(1000); break; // 1초뒤 실행
						case 1: mTask.execute(3000); break; // 3초뒤 실행
						case 2: mTask.execute(5000); break; // 5초뒤 실행
						case 3: mTask.execute(7000); break; // 7초뒤 실행
						default: mTask.execute(100); break; // 바로 실행
						}
					} else {
						mTask.execute(100); // 바로 실행
					}
				}
			} else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				mPhoneNumber = bundle.getString(Intent.EXTRA_PHONE_NUMBER);
				savePreferences(mPhoneNumber, new String("out"));
			}
		}
	}
	
	AsyncTask<Integer, Void, Void> mTask = new AsyncTask<Integer, Void, Void>() {
		
		@Override
		protected void onPreExecute() {
			isIdle = false;
			service = new Intent(mContext, AlwaysOnTopService.class);
			service.setAction("kr.bae.autocallrecoder.service");
		}
		
		@Override
		protected Void doInBackground(Integer... params) {
			try {
				Thread.sleep(params[0]);
				if(!isIdle) {
					Log.d("aaa", "" + params[0]);
					String[] prefStr = getPreferences();
					if(!prefStr[1].equals("")) {
						service.putExtra("phonenumber", prefStr[0]);
						service.putExtra("sendReceive", prefStr[1]);
						mContext.startService(service);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			service = new Intent(mContext, AlwaysOnTopService.class);
			service.setAction("kr.bae.autocallrecoder.service");
			mContext.stopService(service);
		}
	};
	
	// 값 불러오기
	private String[] getPreferences(){
        SharedPreferences pref = mContext.getSharedPreferences("phoneStatus", Activity.MODE_PRIVATE);
        String[] str = {pref.getString("phoneNumber", ""), pref.getString("sendReceive", "")};
        return str;
    }
    
    // 녹음 on off 여부 불러오기
    private boolean getCheckPreferences() {
    	SharedPreferences pref = mContext.getSharedPreferences("preRecordCheck", Activity.MODE_PRIVATE);
        boolean isCheck = pref.getBoolean("Check", true);
        return isCheck;
    }
     
    // 값 저장하기
    private void savePreferences(String phoneNumber, String sendReceive){
        SharedPreferences pref = mContext.getSharedPreferences("phoneStatus", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("sendReceive", sendReceive);
        editor.commit();
    }
    
    // 값(ALL Data) 삭제하기
    private void removeAllPreferences(){
        SharedPreferences pref = mContext.getSharedPreferences("phoneStatus", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
    
    // 발신 실행시간 가져오기
    private int getExcuteTime() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		int time = Integer.parseInt(sharedPrefs.getString("prefexecutetime", "1"));
		return time;
	}
}

