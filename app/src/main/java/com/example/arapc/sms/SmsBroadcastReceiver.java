package com.example.arapc.sms;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

            if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {

                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                String sender = smsMessage.getDisplayOriginatingAddress();
                String phoneNumber = smsMessage.getDisplayOriginatingAddress();
                String senderNum = phoneNumber ;
                String messageBody = smsMessage.getMessageBody();


                /*String format = intentExtras.getString("format");
                SmsMessage smsMessage = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);
                }*/

                if(smsMessage != null){
                    String smsBody = smsMessage.getMessageBody().toString();
                    String address = smsMessage.getOriginatingAddress();
                    smsMessageStr += "SMS From: " + address + "\n";
                    smsMessageStr += smsBody + "\n";
                }

            }

                ReceivedSMSFragment inst = ReceivedSMSFragment.instance();
                inst.updateInbox(smsMessageStr);
        }
    }
}