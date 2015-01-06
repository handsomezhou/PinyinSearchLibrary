package com.handsomezhou.pinyinsearchdemo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;

/**
 * Adapter for Contacts who has one or more than one phone number.
 * @author handsomezhou
 * @date 2015-01-06
 * @reference ContactsAdapter.java
 */
public class ContactsDetailAdapter extends ArrayAdapter<Contacts>{
	private Context mContext;
	private int mTextViewResourceId;
	private List<Contacts> mContacts;
	private OnContactsAdapter mOnContactsAdapter;
	
	public interface OnContactsAdapter{
	//	void onContactsSelectedChanged(List<Contacts> contactsList);
		void onAddContactsSelected(Contacts contacts);
		void onRemoveContactsSelected(Contacts contacts);
	}
	
	public ContactsDetailAdapter(Context context, int textViewResourceId,
			List<Contacts> contacts) {
		super(context, textViewResourceId, contacts);
		mContext=context;
		mTextViewResourceId=textViewResourceId;
		mContacts=contacts;
	}
	
	public OnContactsAdapter getOnContactsAdapter() {
		return mOnContactsAdapter;
	}

	public void setOnContactsAdapter(OnContactsAdapter onContactsAdapter) {
		mOnContactsAdapter = onContactsAdapter;
	}
	
	public void clearSelectedContacts(){
		//clear data
		for(Contacts contacts:mContacts){
			contacts.setSelected(false);
			
			//other phoneNumber 
			if(contacts.getMultipleNumbersContacts().size()>0){
				List<Contacts> multipleNumbersContacts=contacts.getMultipleNumbersContacts();
				for(Contacts cs:multipleNumbersContacts){
					cs.setSelected(false);
				}
			}
		}
		
		//refresh view
		notifyDataSetChanged();
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=null;
		ViewHolder viewHolder;
		Contacts contacts=getItem(position);
		if(null==convertView){
			view=LayoutInflater.from(mContext).inflate(mTextViewResourceId, null);
			viewHolder=new ViewHolder();
			viewHolder.mSelectContactsCB=(CheckBox) view.findViewById(R.id.select_contacts_check_box);
			viewHolder.mPhoneNumber=(TextView) view.findViewById(R.id.phone_number_text_view);
			view.setTag(viewHolder);
		}else{
			view=convertView;
			viewHolder=(ViewHolder) view.getTag();
		}
		
		//show phoneNumber
		viewHolder.mPhoneNumber.setText(contacts.getPhoneNumber());
		
		//set checkBox listener
		viewHolder.mSelectContactsCB.setTag(position);
		viewHolder.mSelectContactsCB.setChecked(contacts.isSelected());
		viewHolder.mSelectContactsCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int position = (Integer) buttonView.getTag();
				Contacts contacts = getItem(position);
				if((true==isChecked)&&(false==contacts.isSelected())){
					contacts.setSelected(isChecked);
					addSelectedContacts(contacts);
					
				}else if((false==isChecked)&&(true==contacts.isSelected())){
					contacts.setSelected(isChecked);
					removeSelectedContacts(contacts);
				}else{
					return;
				}
			}
		});
		return view;
	}

	private class ViewHolder{
		CheckBox mSelectContactsCB;
		TextView mPhoneNumber;
	}
	
	private boolean addSelectedContacts(Contacts contacts){
		
		
		do{
			if(null==contacts){
				break;
			}
			
			if(null!=mOnContactsAdapter){
				mOnContactsAdapter.onAddContactsSelected(contacts);
			}
			
			return true;
		}while(false);
		
		return false;
	
	}
	
	private void removeSelectedContacts(Contacts contacts){
		if(null==contacts){
			return;
		}
		
		if(null!=mOnContactsAdapter){
			mOnContactsAdapter.onRemoveContactsSelected(contacts);
		}
	}
	
	/**
	 * key=id+phoneNumber
	 * */
	private String getSelectedContactsKey(Contacts contacts){
		if(null==contacts){
			return null;
		}
		
		return contacts.getId()+contacts.getPhoneNumber();
	}
}
