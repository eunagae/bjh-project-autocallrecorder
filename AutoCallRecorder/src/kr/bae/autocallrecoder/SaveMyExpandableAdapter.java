package kr.bae.autocallrecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import kr.bae.autocallrecoder.common.ChildItem;
import kr.bae.autocallrecoder.common.GroupItem;
import kr.bae.autocallrecoder.contact.ContactInfo;
import kr.bae.autocallrecoder.radialmenu.SaveRadialMenu;
import kr.bae.autocallrecoder.recorder.SaveRecordPlayer;
import kr.bae.autocallrecoder.recorder.SeekBarMonitor;
import kr.bae.autocallrecoder.viewpager.MyFragmentPagerAdapter;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuWidget;

public class SaveMyExpandableAdapter extends BaseExpandableListAdapter {

	private FragmentActivity mContext;
	private LayoutInflater inflater;
	private SaveRadialMenu mRadialMenu;
	private RadialMenuWidget pieMenu;
	private List<GroupItem> mListDataHeader;
    private HashMap<GroupItem, List<ChildItem>> mListDataChild;
    private SaveRecordPlayer mRecordPlay;
    private MyFragmentPagerAdapter fragmentAdapter;
    
    private SeekBar recBar, preBar;
    private TextView childTextView, preChildTextView, mDurationTv;
    
    private LinearLayout linear;
    private ImageView iv_play, iv_pause, iv_stop, iv_close, preCloseImg;
    private View closeDiv, precloseDiv;
    private SeekBarMonitor monitor;
    
    private ContactInfo mContact;
    private View v;
    
    private int groupPos;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    
	// constructor
	public SaveMyExpandableAdapter(List<GroupItem> listDataHeader,
            HashMap<GroupItem, List<ChildItem>> listChildData) {
		
		this.mListDataHeader = listDataHeader;
		this.mListDataChild = listChildData;
	}
	
	//select
	public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        //removeAllPreferences();
        notifyDataSetChanged();
    }
	
	public SaveRecordPlayer getmRecordPlay() {
		return mRecordPlay;
	}

	public SeekBarMonitor getMonitor() {
		return monitor;
	}
	
	public void setInflater(LayoutInflater inflater, FragmentActivity context, View v, MyFragmentPagerAdapter fragmentAdapter) {
		this.inflater = inflater;
		this.mContext = context;
		this.fragmentAdapter = fragmentAdapter;
		this.v = v;
		initContext();
	}
	
	public void initContext() {
		linear = (LinearLayout) v.findViewById(R.id.play_menu);
		iv_play = (ImageView) v.findViewById(R.id.play);
		iv_play.setOnClickListener(listener);
		iv_pause = (ImageView) v.findViewById(R.id.pause);
		iv_pause.setOnClickListener(listener);
		iv_stop = (ImageView) v.findViewById(R.id.stop);
		iv_stop.setOnClickListener(listener);	
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final ChildItem childItem = (ChildItem) getChild(groupPosition, childPosition);
		final GroupItem groupItem = (GroupItem) getGroup(groupPosition);
		groupPos = groupPosition;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.child_view, null);
		}
		recBar = (SeekBar) convertView.findViewById(R.id.record_play);

		childTextView = (TextView) convertView.findViewById(R.id.textViewChild);
		childTextView.setText(childItem.getmFileName());
		mDurationTv = (TextView) convertView.findViewById(R.id.playtimeTextView);
		mDurationTv.setText(childItem.getDurationFormat());
		iv_close = (ImageView) convertView.findViewById(R.id.closeImage);
		closeDiv = convertView.findViewById(R.id.closediv);
		
		ImageView iv = (ImageView) convertView.findViewById(R.id.childImage);
		if(childItem.getmSendReceive().equals("out")) {
			iv.setImageResource(R.drawable.call_outgoing);
		} else {
			iv.setImageResource(R.drawable.call_incoming);
		}
				
		childTextView.setVisibility(View.VISIBLE);
		iv_close.setVisibility(View.GONE);
		recBar.setVisibility(View.GONE);
		closeDiv.setVisibility(View.GONE);
		
		convertView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				TextView tv = (TextView) v.findViewById(R.id.textViewChild);
				mRadialMenu.setFileName(tv.getText().toString());

				mRadialMenu = new SaveRadialMenu(mContext, SaveMyExpandableAdapter.this, fragmentAdapter);
				pieMenu = mRadialMenu.setRadialMenu();
				mRadialMenu.setChildView(v, groupItem.getmPhoneNumber());
				pieMenu.setHeader(childItem.getmFileName(), 20);
				pieMenu.show(v);
				
				return false;
			}
		});
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				recBar = (SeekBar) v.findViewById(R.id.record_play);
				childTextView = (TextView) v.findViewById(R.id.textViewChild);
				mDurationTv = (TextView) v.findViewById(R.id.playtimeTextView);
				
				String fileName = childItem.getmFileName();
				String phoneNum = mListDataHeader.get(groupPos).getmPhoneNumber();
				iv_close = (ImageView) v.findViewById(R.id.closeImage);
				iv_close.setOnClickListener(listener);
				closeDiv = (View) v.findViewById(R.id.closediv);
				
				if (preBar != null && preChildTextView != null) {
					childVisibility(false);
	
					mRecordPlay.stopPlay();
					mRecordPlay.releaseMediaPlayer();
					if(monitor != null)
						monitor.cancel();
				}
				
				if (recBar.getVisibility() == View.GONE) {
					childVisibility(true);
										
					mRecordPlay = new SaveRecordPlayer(phoneNum, fileName, mContext);
					mRecordPlay.startPlay();
					monitor = new SeekBarMonitor(recBar, mRecordPlay.getmPlayer(), mDurationTv);
				}
			}
		});
		return convertView;
	}
	
	OnClickListener listener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.play: 
				mRecordPlay.startPlay(); 
				monitor.setCurrentStopMode(false);
				break;
			case R.id.pause:
 				mRecordPlay.pausePlay();
 				monitor.setCurrentStopMode(false);
				break;
			case R.id.stop:
				mRecordPlay.pausePlay();
				monitor.setCurrentStopMode(true);
				break;
			case R.id.closeImage:
				mRecordPlay.stopPlay();
				mRecordPlay.releaseMediaPlayer();
				monitor.cancel();
				childVisibility(false);				
			}
		}
	};
	
	public void childVisibility(boolean isClicked) {
		if(isClicked) {
			recBar.setVisibility(View.VISIBLE);
			iv_close.setVisibility(View.VISIBLE);
			closeDiv.setVisibility(View.VISIBLE);
			linear.setVisibility(View.VISIBLE);
			childTextView.setVisibility(View.GONE);		
			
			preChildTextView = childTextView;
			preBar = recBar;
			preCloseImg = iv_close;
			precloseDiv = closeDiv;
		} else {
			preBar.setVisibility(View.GONE);
			preCloseImg.setVisibility(View.GONE);
			linear.setVisibility(View.GONE);
			precloseDiv.setVisibility(View.GONE);
			preChildTextView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		final int groupPos = groupPosition;
		final ExpandableListView elv = (ExpandableListView) parent;
		final GroupItem item = (GroupItem) getGroup(groupPosition);
		
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.parent_view, null);
		}
		mContact = new ContactInfo(mContext, item.getmPhoneNumber());
		
		CheckedTextView ctv = (CheckedTextView) convertView.findViewById(R.id.check);
		ctv.setChecked(isExpanded);
		
		TextView titleTv = (TextView) convertView.findViewById(R.id.title_phone_id);
		titleTv.setText(mContact.getName());

		TextView receiveTv = (TextView) convertView.findViewById(R.id.incoming_count);
		receiveTv.setText(String.valueOf(item.getmReceiveCnt()));
		
		TextView sendTv = (TextView) convertView.findViewById(R.id.outgoing_count);
		sendTv.setText(String.valueOf(item.getmSendCnt()));
		
		final ImageView iv = (ImageView) convertView.findViewById(R.id.person_image);
		iv.setImageBitmap(mContact.getPhoto());
		
		convertView.setBackgroundColor(Color.parseColor("#e0e0e0"));
		if (mSelection.get(groupPosition) != null) {
			convertView.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_blue_bright));// this is a selected position so make it red
        }
		
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				elv.setItemChecked(groupPos, !isPositionChecked(groupPos));
			}
		});
		
		return convertView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mListDataChild.get(mListDataHeader.get(groupPosition))
                .get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mListDataChild.get(mListDataHeader.get(groupPosition))
                .size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mListDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mListDataHeader.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void setmListDataHeader(List<GroupItem> mListDataHeader) {
		this.mListDataHeader = mListDataHeader;
	}

	public void setmListDataChild(HashMap<GroupItem, List<ChildItem>> mListDataChild) {
		this.mListDataChild = mListDataChild;
	}
	
}
