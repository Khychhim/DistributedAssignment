package com.lkc.distributedassignment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Inbox class for searching for sms
 */
public class InboxActivity extends AppCompatActivity{
    private static final String INBOX_URI = "content://sms/inbox";
    ListView listViewInbox;
    ArrayAdapter<String> adapter;
    ArrayList<String> inboxList;
    private static InboxActivity activity;
    private static final int REQUEST_RECEIVE_SMS = 0;

    @Override
    public void onStart(){
        super.onStart();
        activity = this;
    }
    //used in SMSReceiver class for updating the adapter
    public static InboxActivity instance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        requestSmsPermission();
        inboxList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, inboxList);
        listViewInbox = (ListView)findViewById(R.id.listViewInbox);
        listViewInbox.setAdapter(adapter);

    }

    /**
     * method to read all sms currently store in phone
     */
    public void readSMS() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse(INBOX_URI), null, null, null, null);
        int senderIndex = smsInboxCursor.getColumnIndex("address");
        int messageIndex = smsInboxCursor.getColumnIndex("body");
        if (messageIndex < 0 || !smsInboxCursor.moveToFirst()) return;
        adapter.clear();
        do {
            String sender = smsInboxCursor.getString(senderIndex);
            String message = smsInboxCursor.getString(messageIndex);
            String combineMessage = "From: "+sender+"\n"+message+"\n";
            adapter.add(combineMessage);
        } while (smsInboxCursor.moveToNext());
    }

    private void requestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, REQUEST_RECEIVE_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults){
        switch(requestCode){
            case REQUEST_RECEIVE_SMS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    readSMS();
                }else {
                }
                break;

        }
    }

    //method to update the new receive message
    public void update(String result) {
        adapter.insert(result,0);
        adapter.notifyDataSetChanged();
    }


}
