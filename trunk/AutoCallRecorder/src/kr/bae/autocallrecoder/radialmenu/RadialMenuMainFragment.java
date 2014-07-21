package kr.bae.autocallrecoder.radialmenu;

import kr.bae.autocallrecoder.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RadialMenuMainFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view  = inflater.inflate(R.layout.layout_main, container, false);		
		return view;
	}
}
