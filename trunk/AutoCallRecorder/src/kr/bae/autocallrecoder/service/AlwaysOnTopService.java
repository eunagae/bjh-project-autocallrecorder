package kr.bae.autocallrecoder.service;

import kr.bae.autocallrecoder.R;
import kr.bae.autocallrecoder.recorder.CallRecorder;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class AlwaysOnTopService extends Service {
	private ImageView mPopupView;							//항상 보이게 할 뷰
	private WindowManager.LayoutParams mParams;				//layout params 객체. 뷰의 위치 및 크기를 지정하는 객체
	private WindowManager mWindowManager;					//윈도우 매니저
	private boolean m_isSelect, flag;
	private float START_X, START_Y;							//움직이기 위해 터치한 시작 점
	private int PREV_X, PREV_Y;								//움직이기 이전에 뷰가 위치한 점
	private int MAX_X = -1, MAX_Y = -1;						//뷰의 위치 최대 값
	private CallRecorder mRecord;
	private String mPhoneNum, mSendReceive;
	
	private OnTouchListener mViewTouchListener = new OnTouchListener() {
		@Override public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			case MotionEvent.ACTION_UP:
				if(m_isSelect) {
					if(flag) {
					mPopupView.setImageResource(R.drawable.rec_start);
					mRecord = new CallRecorder(getApplicationContext(), mPhoneNum, mSendReceive);
					mRecord.startRec(); 
					flag = false;
					} else {
						mPopupView.setImageResource(R.drawable.rec_stop);
						mRecord.stopRec(1);
						flag = true;
					}
				}
				break;
			case MotionEvent.ACTION_DOWN: // 사용자 터치 다운이면
				if (MAX_X == -1)
					setMaxPosition();
				START_X = event.getRawX(); // 터치 시작 점
				START_Y = event.getRawY(); // 터치 시작 점
				PREV_X = mParams.x; // 뷰의 시작 점
				PREV_Y = mParams.y; // 뷰의 시작 점
				m_isSelect = true;
				break;
			case MotionEvent.ACTION_MOVE:
				int x = (int) (event.getRawX() - START_X); // 이동한 거리
				int y = (int) (event.getRawY() - START_Y); // 이동한 거리

				// 터치해서 이동한 만큼 이동 시킨다
				mParams.x = PREV_X + x;
				mParams.y = PREV_Y + y;

				optimizePosition(); // 뷰의 위치 최적화
				mWindowManager.updateViewLayout(mPopupView, mParams); // 뷰 업데이트
				m_isSelect = false;
				break;
			}

			return true;
		}
	};
	
	@Override
	public IBinder onBind(Intent arg0) { return null; }
	
	@Override
	public void onCreate() {
		super.onCreate();

		mPopupView = new ImageView(this);	
		
		if(getIconSettings()) {
			mPopupView.setImageResource(R.drawable.rec_start);//뷰 생성
			mPopupView.setOnTouchListener(mViewTouchListener);										//팝업뷰에 터치 리스너 등록
		}

		//최상위 윈도우에 넣기 위한 설정
		mParams = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,						//항상 최 상위에 있게. status bar 밑에 있음. 터치 이벤트 받을 수 있음.
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,				//이 속성을 안주면 터치 & 키 이벤트도 먹게 된다. 
																		//포커스를 안줘서 자기 영역 밖터치는 인식 안하고 키이벤트를 사용하지 않게 설정
			PixelFormat.TRANSLUCENT);									//투명
		mParams.gravity = Gravity.LEFT | Gravity.TOP;					//왼쪽 상단에 위치하게 함.
		
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);	//윈도우 매니저 불러옴.
		mWindowManager.addView(mPopupView, mParams);		//최상위 윈도우에 뷰 넣기. *중요 : 여기에 permission을 미리 설정해 두어야 한다. 매니페스트에
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mPhoneNum = intent.getStringExtra("phonenumber");
		mSendReceive = intent.getStringExtra("sendReceive");
		mRecord = new CallRecorder(this, mPhoneNum, mSendReceive);
		mRecord.startRec(); 
		return START_STICKY;
	}
	
	/**
	 * 뷰의 위치가 화면 안에 있게 최대값을 설정한다
	 */
	private void setMaxPosition() {
		DisplayMetrics matrix = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(matrix);		//화면 정보를 가져와서
		
		MAX_X = matrix.widthPixels - mPopupView.getWidth();			//x 최대값 설정
		MAX_Y = matrix.heightPixels - mPopupView.getHeight();			//y 최대값 설정
	}
	
	/**
	 * 뷰의 위치가 화면 안에 있게 하기 위해서 검사하고 수정한다.
	 */
	private void optimizePosition() {
		//최대값 넘어가지 않게 설정
		if(mParams.x > MAX_X) mParams.x = MAX_X;
		if(mParams.y > MAX_Y) mParams.y = MAX_Y;
		if(mParams.x < 0) mParams.x = 0;
		if(mParams.y < 0) mParams.y = 0;
	}

	/**
	 * 가로 / 세로 모드 변경 시 최대값 다시 설정해 주어야 함.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setMaxPosition();		//최대값 다시 설정
		optimizePosition();		//뷰 위치 최적화
	}
	
	@Override
	public void onDestroy() {
		if(mWindowManager != null) {		//서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
			if(mPopupView != null) mWindowManager.removeView(mPopupView);
			//if(mSeekBar != null) mWindowManager.removeView(mSeekBar);
			mRecord.stopRec(0); 
		}
		super.onDestroy();
	}
	  
    private boolean getIconSettings() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isCheck = sharedPrefs.getBoolean("prefnotify", true);
		return isCheck;
	}
}