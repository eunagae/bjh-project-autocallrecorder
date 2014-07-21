package kr.bae.autocallrecoder.contact;

import java.io.InputStream;

import kr.bae.autocallrecoder.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactInfo {
	private String mName;
	private String mId;
	private Context mContext;
	private String mPhoneNumber;
	final String[] CONTACTS_PROJECTION = new String[] {
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

	// 본 생성자에서는 인자값으로 넘어온 전화번호(number)에 해당되는 contact id 와 catact name을 가져와서 멤버
	// 변수 name과 id에 저장해 준다.

	public ContactInfo(Context context, String number) {
		this.mContext = context;
		this.mPhoneNumber = number;
		ContentResolver resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				CONTACTS_PROJECTION,
				ContactsContract.CommonDataKinds.Phone.NUMBER + "=?",
				new String[] { mPhoneNumber }, null);
		if (cursor.getCount() < 1) {
			mName = null;
			mId = null;
		} else {
			cursor.moveToFirst();
			mId = cursor.getString(0);
			mName = cursor.getString(1);
		}
		cursor.close();
	}

	// 위 생성자에서 구해진 id 값을 기준으로 해서 id와 name에 일치하는 Contact의 사진을 가져와서 Bitmap 형식으로
	// 반환해 준다.

	public String getName() {
		if(mId == null)
			return mPhoneNumber;
		else
			return mName;
	}

	public Bitmap getPhoto() {
		if (mId == null) {
			if(getPreferences()) {
				return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.check);
			}
			else {
				return null;
			}
		}
		
		Uri uri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, Long.parseLong((mId)));
		InputStream data = ContactsContract.Contacts
				.openContactPhotoInputStream(mContext.getContentResolver(), uri);
		
		if (data != null) {
			if(getPreferences())
				return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.check);
			else
				return BitmapFactory.decodeStream(data);
		} else {
			return null;
		}
	}
	
	// 값 불러오기
    private boolean getPreferences(){
        SharedPreferences pref = mContext.getSharedPreferences("image_select", Activity.MODE_PRIVATE);
        boolean select = pref.getBoolean(mPhoneNumber, false);
        return select;
    }
     
}