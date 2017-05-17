package com.lkc.distributedassignment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * Created by USER on 5/17/2017.
 */

public class SMSReceiver extends BroadcastReceiver {
    final SmsManager sms = SmsManager.getDefault();
    @Override
    public void onReceive(Context context, Intent intent) {
        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            Object[] pdusObj = (Object[]) bundle.get("pdus");

            for (int i = 0; i < pdusObj.length; i++) {
                // This will create an SmsMessage object from the received pdu
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                // Get sender phone number
                String sender = sms.getDisplayOriginatingAddress();
                String message = sms.getDisplayMessageBody();
                String combineText = "From: "+sender+"\n"+message+"\n";
                //String formattedText = String.format(context.getResources().getString(R.string.sms_message), sender, message);
//                // Display the SMS message in a Toast
//                Toast.makeText(context, formattedText, Toast.LENGTH_LONG).show();
//                MainActivity inst = MainActivity.instance();
//                inst.updateList(formattedText);
            }

            //Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }


    }
}
