package com.example.arapc.sms;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedSMSFragment extends Fragment {



    public static ReceivedSMSFragment newInstance() {
        return new ReceivedSMSFragment();
    }


    public ReceivedSMSFragment() {
        // Required empty public constructor
    }


    Context context;
    ArrayList<String> smsMessagesList = new ArrayList<>();
    ListView messages;
    ArrayAdapter arrayAdapter;
    EditText input;
    SmsManager smsManager = SmsManager.getDefault();
    private static ReceivedSMSFragment inst;

    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    public static ReceivedSMSFragment instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_received_sms, container, false);

        context = getActivity();

        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, smsMessagesList);
        messages = view.findViewById(R.id.messages);

        messages.setAdapter(arrayAdapter);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            refreshSmsInbox();
        }

        return view;
    }

    public void updateInbox(final String smsMessage) {
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }

    public void onSendClick(View view) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            smsManager.sendTextMessage("+8801558958288", null, input.getText().toString(), null, null);
            Toast.makeText(context, "Message sent!", Toast.LENGTH_SHORT).show();
        }
    }

    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_SMS)) {
                    Toast.makeText(context, "Please allow permission!", Toast.LENGTH_SHORT).show();
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS},
                        READ_SMS_PERMISSIONS_REQUEST);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(context, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }



    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexID = smsInboxCursor.getColumnIndex("_id");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
//messages.setSelection(arrayAdapter.getCount() - 1);
    }




}
