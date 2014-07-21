package kr.bae.autocallrecoder.radialmenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.bae.autocallrecoder.MyExpandableAdapter;
import kr.bae.autocallrecoder.R;
import kr.bae.autocallrecoder.SQLiteDataBase.PhoneDAO;
import kr.bae.autocallrecoder.common.ChildItem;
import kr.bae.autocallrecoder.common.GroupItem;
import kr.bae.autocallrecoder.viewpager.MyFragmentPagerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuItem;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuWidget;

public class RadialMenu {
	private FragmentActivity mContext;
	private View mChildView;
	private RadialMenuWidget pieMenu;
	private FrameLayout mFragmentContainer;
	private RadialMenuItem menuShareItem, menuCloseItem, menuMemoItem,
			menuDeleteItem, menuSaveItem, menuStarItem;
	private PhoneDAO dao;
	private MyExpandableAdapter adapter;
	
	private List<GroupItem> listDataHeader;
	private HashMap<GroupItem, List<ChildItem>> listDataChild;
	private String mPhoneNum;
	private static String mFileName;
	private EditText et;
	private MyFragmentPagerAdapter fragmentAdapter;
	
	public RadialMenu(FragmentActivity context, MyExpandableAdapter adapter, MyFragmentPagerAdapter fragmentAdapter) {
		this.mContext = context;
		this.adapter = adapter;
		this.fragmentAdapter = fragmentAdapter;
		pieMenu = new RadialMenuWidget(mContext);
		dao = PhoneDAO.open(mContext);
		radialmenu();
	}
	
	public static void setFileName(String filename) {
		mFileName = filename;
	}

	private void radialmenu() {
		// radialmenu
		mFragmentContainer = (FrameLayout) mContext.findViewById(R.id.alt_fragment_container);
		

		menuShareItem = new RadialMenuItem("share", "공유");
		menuShareItem.setDisplayIcon(android.R.drawable.ic_menu_share);
		menuShareItem
				.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
					@Override
					public void execute() {
						shareRecordFile();
						pieMenu.dismiss();
					}
				});
		
		menuMemoItem = new RadialMenuItem("memo", "메모");
		menuMemoItem.setDisplayIcon(android.R.drawable.ic_menu_edit);
		menuMemoItem
				.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
					@Override
					public void execute() {
						// AlertDialog 객체 선언
						AlertDialog dialog = create_inputDialog();
						// Context 얻고, 해당 컨텍스트의 레이아웃 정보 얻기
						LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
						// 레이아웃 설정
						View layout = inflater.inflate(R.layout.memodialog,	(ViewGroup) mContext.findViewById(R.id.memo));
						et = (EditText) layout.findViewById(R.id.memo_edit);
						et.setText(dao.selectMemo(mFileName));
						// Input 소프트 키보드 보이기
						dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						// AlertDialog에 레이아웃 추가
						dialog.setView(layout);
						dialog.show();
						pieMenu.dismiss();
					}
				});
		
		menuSaveItem = new RadialMenuItem("save", "저장");
		menuSaveItem.setDisplayIcon(android.R.drawable.ic_menu_save);
		menuSaveItem
				.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
					@Override
					public void execute() {
						dao.updateSave(mFileName, 1);
						dao.updatePath(getPreferences(), mFileName);
						fragmentAdapter.notifyDataSetChanged();
						copyFile();
						Toast.makeText(mContext, "저장 되었습니다.", Toast.LENGTH_SHORT).show();
						pieMenu.dismiss();
					}
				});
		
		menuStarItem = new RadialMenuItem("star", "즐겨찾기");
		
		if(getFavoritePreferences()) {
			menuStarItem.setDisplayIcon(R.drawable.ic_action_important);
		} else {
			menuStarItem.setDisplayIcon(R.drawable.ic_action_not_important);
		}
		menuStarItem
				.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
					@Override
					public void execute() {
						if(getFavoritePreferences()) {
							saveFavoritePreferences(false);
							dao.updateFavorite(mFileName, 0);
							fragmentAdapter.notifyDataSetChanged();
						} else {
							saveFavoritePreferences(true);
							dao.updateFavorite(mFileName, 1);
							fragmentAdapter.notifyDataSetChanged();
						}
						pieMenu.dismiss();
					}
				});

		menuDeleteItem = new RadialMenuItem("delete", "삭제");
		menuDeleteItem.setDisplayIcon(android.R.drawable.ic_menu_delete);
		menuDeleteItem
				.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
					@Override
					public void execute() {
						saveFavoritePreferences(false);
						dao.updateVisible(mFileName, 0);
						dao.deleteChild(mFileName); //save 0 visible 0
						prepareListData();
						adapter.setmListDataHeader(listDataHeader);
						adapter.setmListDataChild(listDataChild);
						adapter.notifyDataSetChanged();
						deleteRecordFile();						
						pieMenu.dismiss();
					}
				});

		menuCloseItem = new RadialMenuItem("close", null);
		menuCloseItem.setDisplayIcon(android.R.drawable.ic_menu_close_clear_cancel);
		menuCloseItem
				.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
					@Override
					public void execute() {
						// menuLayout.removeAllViews();
						pieMenu.dismiss();
					}
				});

	}

	private AlertDialog create_inputDialog() {
	      AlertDialog dialogBox = new AlertDialog.Builder(mContext)
	          .setTitle("메모")
	          .setMessage("메모를 입력하세요")
	          .setPositiveButton("예", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   boolean flag = dao.updateMemo(mFileName, et.getText().toString());
	            	   if(flag) {
	            		   Toast.makeText(mContext, "메모저장", Toast.LENGTH_SHORT).show();
	            	   }
	               }
	          }).create();
	       return dialogBox;
	}
	
	public RadialMenuWidget setRadialMenu() {
		pieMenu.setAnimationSpeed(10);
		pieMenu.setSourceLocation(500, 500);
		pieMenu.setIconSize(15, 30);
		pieMenu.setTextSize(13);
		pieMenu.setOutlineColor(Color.BLACK, 225);
		pieMenu.setInnerRingColor(0xe0e0e0, 180);
		pieMenu.setOuterRingColor(0x0099CC, 180);
		pieMenu.setCenterCircle(menuCloseItem);

		pieMenu.addMenuEntry(new ArrayList<RadialMenuItem>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				add(menuShareItem);
				add(menuStarItem);
				add(menuDeleteItem);
				add(menuMemoItem);
				add(menuSaveItem);
			}
		});

		return pieMenu;
	}
	
	public void setChildView(View v, String phoneNum) {
		mChildView = v;
		mPhoneNum = phoneNum;
		
		TextView tv = (TextView) v.findViewById(R.id.textViewChild);
		mFileName = tv.getText().toString();
	}
	
	private void prepareListData() {
		listDataHeader = dao.selectGroupItem();
		listDataChild = new HashMap<GroupItem, List<ChildItem>>();
		
        for (int i = 0; i < listDataHeader.size(); i++) {
        	List<ChildItem> childList = dao.selectChildItem(listDataHeader.get(i).getmPhoneNumber());
        	listDataChild.put(listDataHeader.get(i), childList); // Header, Child data        	
        }
	}
	
	private void deleteRecordFile() {
		String sdcard = Environment.getExternalStorageState();
		File file = null;

		if (!sdcard.equals(Environment.MEDIA_MOUNTED)) {
			// SD카드가 마운트되어있지 않음
			file = Environment.getRootDirectory();
		} else {
			// SD카드가 마운트되어있음
			file = Environment.getExternalStorageDirectory();
		}

		String dir = file.getAbsolutePath() + "/AutoCallRecorder/" + mPhoneNum
				+ "/" + mFileName;
		File f = new File(dir);
		if (f.exists())
			f.delete();
		
		String dir2 = file.getAbsolutePath() + "/AutoCallRecorder/" + mPhoneNum;
		File f2 = new File(dir2);
		if(f2.exists()) {
			f2.delete();  //root 삭제
		}
	}
	
	
	private void shareRecordFile() {
		String sdcard = Environment.getExternalStorageState();
		File file = null;

		if (!sdcard.equals(Environment.MEDIA_MOUNTED)) {
			// SD카드가 마운트되어있지 않음
			file = Environment.getRootDirectory();
		} else {
			// SD카드가 마운트되어있음
			file = Environment.getExternalStorageDirectory();
		}

		String dir = file.getAbsolutePath() + "/AutoCallRecorder/" + mPhoneNum + "/" + mFileName;
		File f = new File(dir);
		if (f.exists())	{
			Intent intentSend = new Intent(Intent.ACTION_SEND);
			intentSend.setType("audio/*");
			// 이름으로 저장된 파일의 경로를 넣어서 공유하기

			intentSend.putExtra(Intent.EXTRA_STREAM,
					Uri.parse(dir));
			mContext.startActivity(Intent.createChooser(
					intentSend, "공유")); // 공유하기 창

		} else {
			// 파일이 없다면 저장을 해달라는 토스트메세지를 띄운다.
			//Toast.makeText(mContext, "저장을 먼저 해주세요", Toast.LENGTH_LONG).show();

		}
	}
	
    // 즐겨찾기 정보 불러오기
    private boolean getFavoritePreferences() {
    	SharedPreferences pref = mContext.getSharedPreferences("importent", Activity.MODE_PRIVATE);
        boolean isCheck = pref.getBoolean(mFileName, false);
        return isCheck;
    }
     
    // 즐겨찾기 정보 저장하기
    private void saveFavoritePreferences(boolean isCheck){
        SharedPreferences pref = mContext.getSharedPreferences("importent", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(mFileName, isCheck);
        editor.commit();
    }
    
    private boolean copyFile(){
    	File file = getFile();
    	String saveFile = getSaveFile() + mFileName;
        boolean result;
        if(file!=null&&file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream newfos = new FileOutputStream(saveFile);
                int readcount=0;
                byte[] buffer = new byte[1024];
                while((readcount = fis.read(buffer,0,1024))!= -1){
                    newfos.write(buffer,0,readcount);
                }
                newfos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
    }

	private File getFile() {
		String sdcard = Environment.getExternalStorageState();
		File file = null;
		if (!sdcard.equals(Environment.MEDIA_MOUNTED)) {
			// SD카드가 마운트되어있지 않음
			file = Environment.getRootDirectory();
		} else {
			// SD카드가 마운트되어있음
			file = Environment.getExternalStorageDirectory();
		}
		String dir = file.getAbsolutePath() + "/AutoCallRecorder/" + mPhoneNum + "/" + mFileName;
		file = new File(dir);
		return file;
	}
    
	//복사할 폴더
	private String getSaveFile() {
		String dir = getPreferences() + "/SaveCallRecorder/" + mPhoneNum + "/";

		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dir;
	}
	
	private String getPreferences(){
        SharedPreferences pref = mContext.getSharedPreferences("fileexplore", Activity.MODE_PRIVATE);
        String path = pref.getString("path", "/storage/sdcard0");
		return path;
    }

}
