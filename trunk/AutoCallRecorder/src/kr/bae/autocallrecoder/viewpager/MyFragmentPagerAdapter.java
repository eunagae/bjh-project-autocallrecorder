package kr.bae.autocallrecoder.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
	private Context mContext;
	final int PAGE_COUNT = 3;
	final static String TILTE[] = {"통화 목록", "즐겨찾기", "저장 목록"};
	/** Constructor of the class */
	public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.mContext = context;
	}

	/** This method will be invoked when a page is requested to create */
	@Override
	public Fragment getItem(int arg0) {
		Fragment myFragment = null;
		Bundle data = new Bundle();
		data.putInt("current_page", arg0);
		switch (arg0) {
		case 0:
			myFragment = new MyFragmentFirst(this, mContext);
			break;

		case 1:
			myFragment = new MyFragmentSecond(this, mContext);
			break;

		case 2:
			myFragment = new MyFragmentThird(this, mContext);
			break;
		}
		myFragment.setArguments(data);
		return myFragment;
	}

	/** Returns the number of pages */
	@Override
	public int getCount() {		
		return PAGE_COUNT;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {	
		return TILTE[position];
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
}