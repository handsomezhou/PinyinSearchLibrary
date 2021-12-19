package com.handsomezhou.contactssearch.fragment;

import android.Manifest;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.handsomezhou.contactssearch.R;
import com.handsomezhou.contactssearch.activity.MainActivity;
import com.handsomezhou.contactssearch.activity.QwertySearchActivity;
import com.handsomezhou.contactssearch.activity.T9SearchActivity;

import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class MainFragment extends BaseFragment {
	private static final String TAG="MainFragment";
	private Button mT9SearchBtn;
	private Button mQwertySearchBtn;
	
	@Override
	protected void initData() {
		setContext(getActivity());

	}

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container) {
		View view=inflater.inflate(R.layout.fragment_main, container, false);
		mT9SearchBtn=(Button) view.findViewById(R.id.t9_search_btn);
		mQwertySearchBtn=(Button) view.findViewById(R.id.qwerty_search_btn);
		return view;
	}

	@Override
	protected void initListener() {
		mT9SearchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startT9Search();
			}
		});
		
		mQwertySearchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startQwertySearch();
			}
		});

		checkPermission();
	}
	
	private void startT9Search(){
		Intent intent=new Intent(getContext(), T9SearchActivity.class);
		startActivity(intent);
	}

	private void startQwertySearch(){
		Intent intent=new Intent(getContext(), QwertySearchActivity.class);
		startActivity(intent);
	}

	private void checkPermission(){
		List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
		permissionItems.add(new PermissionItem(Manifest.permission.READ_CONTACTS, getString(R.string.contacts), R.mipmap.ic_launcher));


		HiPermission.create(getActivity())
				.permissions(permissionItems)
				.checkMutiPermission(new PermissionCallback() {
					@Override
					public void onClose() {
						Log.i(TAG, "onClose");
						String tips=getString(R.string.can_not_read_contacts_data_tips);
						Toast.makeText(getContext(),tips,Toast.LENGTH_LONG).show();
					}

					@Override
					public void onFinish() {
						String tips="permissions requested completed";
						Log.i(TAG, tips);
						//Toast.makeText(getContext(),tips,Toast.LENGTH_LONG).show();
					}

					@Override
					public void onDeny(String permission, int position) {
						Log.i(TAG, "onDeny");
					}

					@Override
					public void onGuarantee(String permission, int position) {
						Log.i(TAG, "onGuarantee");
					}
				});
	}

}
