package com.handsomezhou.pinyinsearchdemo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.util.ViewUtil;


public class ContactsAdapter extends ArrayAdapter<Contacts> {
	public static final String PINYIN_FIRST_LETTER_DEFAULT_VALUE="#";
	private Context mContext;
	private int mTextViewResourceId;
	private List<Contacts> mContacts;
	
	public ContactsAdapter(Context context, int textViewResourceId,
			List<Contacts> contacts) {
		super(context, textViewResourceId, contacts);
		mContext=context;
		mTextViewResourceId=textViewResourceId;
		mContacts=contacts;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=null;
		ViewHolder viewHolder;
		Contacts contacts=getItem(position);
		if(null==convertView){
			view=LayoutInflater.from(mContext).inflate(mTextViewResourceId, null);
			viewHolder=new ViewHolder();
			viewHolder.mAlphabetTv=(TextView)view.findViewById(R.id.alphabet_text_view);
			viewHolder.mNameTv=(TextView) view.findViewById(R.id.name_text_view);
			viewHolder.mPhoneNumber=(TextView) view.findViewById(R.id.phone_number_text_view);
			view.setTag(viewHolder);
		}else{
			view=convertView;
			viewHolder=(ViewHolder) view.getTag();
		}
		
		//show the first alphabet of name
		showAlphabetIndex(viewHolder.mAlphabetTv, position, contacts);
		//show name and phone number
		switch (contacts.getSearchByType()) {
		case SearchByNull:
			ViewUtil.showTextNormal(viewHolder.mNameTv, contacts.getName());
			ViewUtil.showTextNormal(viewHolder.mPhoneNumber, contacts.getPhoneNumber());
			break;
		case SearchByPhoneNumber:
			ViewUtil.showTextNormal(viewHolder.mNameTv, contacts.getName());
			ViewUtil.showTextHighlight(viewHolder.mPhoneNumber, contacts.getPhoneNumber(), contacts.getMatchKeywords().toString());
			break;
		case SearchByName:
			ViewUtil.showTextHighlight(viewHolder.mNameTv, contacts.getName(), contacts.getMatchKeywords().toString());
			ViewUtil.showTextNormal(viewHolder.mPhoneNumber, contacts.getPhoneNumber());
			break;
		default:
			break;
		}	
		return view;
	}
	
	private class ViewHolder{
		TextView mAlphabetTv;
		TextView mNameTv;
		TextView mPhoneNumber;
	}
	
	private void showAlphabetIndex(TextView textView, int position, final Contacts contacts){
		if((null==textView)||position<0||(null==contacts)){
			return;
		}
		String curAlphabet=getAlphabet(contacts.getSortKey());
		if(position>0){
			Contacts preContacts=getItem(position-1);
			String preAlphabet=getAlphabet(preContacts.getSortKey());
			if(curAlphabet.equals(preAlphabet)){
				textView.setVisibility(View.GONE);
				textView.setText(curAlphabet);
			}else{
				textView.setVisibility(View.VISIBLE);
				textView.setText(curAlphabet);
			}
		}else {
			textView.setVisibility(View.VISIBLE);
			textView.setText(curAlphabet);
		}
		
		return ;
	}
	
	private String getAlphabet(String str){
		if((null==str)||(str.length()<=0)){
			return PINYIN_FIRST_LETTER_DEFAULT_VALUE;
		}
		String alphabet=null;
		char chr=str.charAt(0);
		if (chr >= 'A' && chr <= 'Z') {
			alphabet = String.valueOf(chr);
		} else if (chr >= 'a' && chr <= 'z') {
			alphabet = String.valueOf((char) ('A' + chr - 'a'));
		} else {
			alphabet = PINYIN_FIRST_LETTER_DEFAULT_VALUE;
		}
		return alphabet;
	}
}
