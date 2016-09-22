package com.handsomezhou.contactssearch.util;

import android.content.Context;
import android.content.Intent;

/**
 * share content by all kinds of ways
 * @author handsomezhou
 * @date 2015-01-23
 */
public class ShareUtil {
	/**
	 * share text by sms
	 * 
	 * @param context
	 * @param RecipientsPhoneNumber	phoneNumberformat:"phoneNumber1;phoneNumber2;..."
	 * @param textContent
	 *          
	 */
	public static void shareTextBySms(Context context,
			String RecipientsPhoneNumber, String textContent) {
		Intent share = new Intent(Intent.ACTION_VIEW);

		share.putExtra("address", RecipientsPhoneNumber);
		share.putExtra("sms_body", textContent);
		share.setType("vnd.android-dir/mms-sms");

		context.startActivity(share);
	}
}
