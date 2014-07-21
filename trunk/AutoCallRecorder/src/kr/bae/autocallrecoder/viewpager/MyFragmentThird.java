package kr.bae.autocallrecoder.viewpager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import kr.bae.autocallrecoder.R;
import kr.bae.autocallrecoder.SaveMyExpandableAdapter;
import kr.bae.autocallrecoder.SQLiteDataBase.PhoneDAO;
import kr.bae.autocallrecoder.common.ChildItem;
import kr.bae.autocallrecoder.common.GroupItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MyFragmentThird extends Fragment{
	private List<GroupItem> listDataHeader;
	private HashMap<GroupItem, List<ChildItem>> listDataChild;
    
	private ExpandableListView mListView;
	private SaveMyExpandableAdapter adapter;
	private MyFragmentPagerAdapter fragmentAdapter;
	private PhoneDAO dao;
	private Context mContext;
	private List<Integer> selectlist;
	int mCurrentPage;
	
	public MyFragmentThird(MyFragmentPagerAdapter fragmentAdapter, Context context) {
		this.fragmentAdapter = fragmentAdapter;
		this.mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = getArguments();
		mCurrentPage = data.getInt("current_page", 0);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.myfragment_layout, container,false);
		dao = PhoneDAO.open(getActivity());
		// preparing list data
        prepareListData();
		adapter = new SaveMyExpandableAdapter(listDataHeader, listDataChild);
		adapter.setInflater(
				(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
				getActivity(), v, fragmentAdapter);

		mListView = (ExpandableListView) v.findViewById(R.id.expandable_list);
		mListView.setGroupIndicator(null);
		mListView.setAdapter(adapter);
		mListView.setItemsCanFocus(false);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			private int nr = 0;

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = getActivity().getMenuInflater();
				inflater.inflate(R.menu.cabselection_menu, menu);
				menu.findItem(R.id.save_entry).setVisible(false);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				final ActionMode acmode = mode;
				selectlist = new ArrayList<>();
				Set<Integer> positions = adapter.getCurrentCheckedPosition();
				for (Integer pos : positions) {
					selectlist.add(pos);
				}
				Collections.sort(selectlist);
				Collections.reverse(selectlist);

				switch (item.getItemId()) {
				case R.id.delete_entry:
					AlertDialog.Builder builder = new AlertDialog.Builder(mContext); 
					builder.setTitle("삭제")
					.setMessage("삭제 하시겠습니까?")
					.setCancelable(false)
					.setPositiveButton(
						"확인",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int whichButton) {
								removeGroup(acmode);
								dialog.cancel();
							}
						})
					.setNegativeButton(
						"취소",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								acmode.finish();
								dialog.cancel();
							}
						});
					AlertDialog dialog = builder.create(); // 알림창 객체 생성
					dialog.show(); // 알림창 띄우기
					break;
				case R.id.finish_it:
					nr = 0;
					adapter.clearSelection();
					mode.finish();
				}
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				nr = 0;
				removeAllPreferences();
				adapter.clearSelection();
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				if (checked) {
					nr++;
					savePreferences(listDataHeader.get(position).getmPhoneNumber(), checked);
					adapter.setNewSelection(position, checked);
				} else {
					nr--;
					savePreferences(listDataHeader.get(position).getmPhoneNumber(), checked);
					adapter.removeSelection(position);
				}
				mode.setTitle(nr + " selected");

			}

		});
		
		// 그룹리스트가 열렸을 경우
		mListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				int groupCount = adapter.getGroupCount();
				
				// 나머지는 닫힌다.
				for (int i = 0; i < groupCount; i++) {
					if (!(i == groupPosition))
						mListView.collapseGroup(i);
				}
			}
		});
		
		mListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
			@Override
			public void onGroupCollapse(int groupPosition) {
				LinearLayout linear = (LinearLayout) v.findViewById(R.id.play_menu);
				linear.setVisibility(View.GONE);
				
				// 그룹이 닫힐경우 mediaplay cancel
				if (adapter.getmRecordPlay() != null) {
					if(adapter.getmRecordPlay().getmPlayer() != null)
						if (adapter.getmRecordPlay().getmPlayer().isPlaying()) {
							adapter.getmRecordPlay().stopPlay();
							adapter.getmRecordPlay().releaseMediaPlayer();
							adapter.getMonitor().cancel();
						}
				}
			}
		});
		return v;		
	}
	
	//group 삭제
	protected void removeGroup(ActionMode mode) {
		for (int pos : selectlist) {
			deleteListData(listDataHeader.get(pos).getmPhoneNumber());
			deleteRecordFile(listDataHeader.get(pos).getmPhoneNumber());
			listDataHeader.remove(pos);
		}
		adapter.notifyDataSetChanged();
		adapter.clearSelection();
		mode.finish();
		
		int groupCount = adapter.getGroupCount();
		for (int i = 0; i < groupCount; i++) {
			mListView.collapseGroup(i);
		}
	}

	private void deleteRecordFile(String phoneNum) {
		String dir = dao.selectPathAll(phoneNum) + "/SaveCallRecorder/" + phoneNum;
		File f = new File(dir);
		if(f.exists()) {
			File[] childFileList = f.listFiles();
			for (File childFile : childFileList) {
				if (childFile.isDirectory()) {
					deleteRecordFile(childFile.getAbsolutePath()); // 하위 디렉토리 루프
				} else {
					childFile.delete(); // 하위 파일삭제
				}
			}
			f.delete();    //root 삭제 
		}
	}

	private void deleteListData(String phoneNumber) {
		dao.updateGroupSave(phoneNumber, 0);
		dao.deleteGroup(phoneNumber);
	}

	private void prepareListData() {
		listDataHeader = dao.selectSaveGroupItem();
		listDataChild = new HashMap<GroupItem, List<ChildItem>>();
		
        for (int i = 0; i < listDataHeader.size(); i++) {
        	List<ChildItem> childList = dao.selectSaveChildItem(listDataHeader.get(i).getmPhoneNumber());
        	listDataChild.put(listDataHeader.get(i), childList); // Header, Child data        	
        }
	}
	
	// 값 저장하기
    private void savePreferences(String phoneNumber, boolean isSelect){
        SharedPreferences pref = getActivity().getSharedPreferences("image_select", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(phoneNumber, isSelect);
        editor.commit();
    }
     
    // 값(ALL Data) 삭제하기
    private void removeAllPreferences(){
        SharedPreferences pref = getActivity().getSharedPreferences("image_select", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
    
    @Override
	public void onDestroy() {
		removeAllPreferences();
		adapter.clearSelection();
		if (adapter.getmRecordPlay() != null) {
			if(adapter.getmRecordPlay().getmPlayer() != null)
				if (adapter.getmRecordPlay().getmPlayer().isPlaying()) {
					adapter.getmRecordPlay().stopPlay();
					adapter.getmRecordPlay().releaseMediaPlayer();
					adapter.getMonitor().cancel();
				}
		}
		super.onDestroy();
	};
	
}