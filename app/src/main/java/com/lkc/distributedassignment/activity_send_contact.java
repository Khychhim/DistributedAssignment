package com.lkc.distributedassignment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class activity_send_contact extends FragmentActivity
        implements NfcAdapter.OnNdefPushCompleteCallback,
                   NfcAdapter.CreateNdefMessageCallback{
    private static final int PICK_CONTACT = 1;
    private static final int REQUEST_PERMISSION_CODE = 1;
    static final int NAME = 0;
    static final int MOBILE = 1;
    static final int HOME = 2;
    static final int EMAIL = 3;

    private EditText contactName;
    private EditText mobileNumber;
    private EditText homePhone;
    private EditText email;
    private Button sendContact;

    private boolean hasPermission = false;

    private ArrayList<String> dataToSend = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_contact);

        contactName = (EditText) findViewById(R.id.contactName);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        homePhone = (EditText) findViewById(R.id.homePhone);
        email = (EditText) findViewById(R.id.email);
        sendContact = (Button) findViewById(R.id.send);
        EnableRuntimePermission();
    }

    public void send(View view) {

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if(!dataToSend.isEmpty()) dataToSend.clear();
        contactName.setText("");
        mobileNumber.setText("");
        homePhone.setText("");
        email.setText("");

        switch(reqCode) {
            case (PICK_CONTACT):
                if(resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getContentResolver().
                            query(contactData,
                                    new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null,null);
                    if(cursor!=null){
                        while(cursor.moveToNext()){
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            contactName.setText(name);

                            Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                            ContactsContract.CommonDataKinds.Phone.TYPE},
                                    " DISPLAY_NAME = '"+name+"'", null, null);

                            while(c.moveToNext()){
                                switch(c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))){
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :
                                        String mobile = c.getString(c.getColumnIndex(
                                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        mobileNumber.setText(mobile);
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME :
                                        String homeNumber = c.getString(c.getColumnIndex(
                                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        homePhone.setText(homeNumber);
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK :
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER :
                                }
                            }

                            c = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS},
                                    " DISPLAY_NAME = '"+name+"'", null, null);
                            if(c.moveToNext()) {
                                String emailAddress = c.getString(c.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Email.DATA1));
                                email.setText(emailAddress);
                            }
                            break;
                        }
                    }
                    dataToSend.add(NAME, contactName.getText().toString());
                    dataToSend.add(MOBILE, mobileNumber.getText().toString());
                    dataToSend.add(HOME, homePhone.getText().toString());
                    dataToSend.add(EMAIL, email.getText().toString());

                    sendContact.setEnabled(true);
                }
        }
    }

    public void searchContacts(View view) {
        //EnableRuntimePermission();
        if(hasPermission) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }
    }

    public void EnableRuntimePermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(
                activity_send_contact.this,
                Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(activity_send_contact.this,"CONTACTS permission allows " +
                    "us to Access CONTACTS app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity_send_contact.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case REQUEST_PERMISSION_CODE:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;
                } else {
                    Toast.makeText(activity_send_contact.this,"Permission Canceled, " +
                            "Application needs permission to access contact data", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private NdefRecord[] createRecords() {
        NdefRecord[] records = new NdefRecord[dataToSend.size()+1];

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            for(int i = 0; i < dataToSend.size(); i++) {
                byte[] payload = dataToSend.get(i).getBytes(Charset.forName("UTF-8"));
                NdefRecord record = new NdefRecord(
                        NdefRecord.TNF_WELL_KNOWN,
                        NdefRecord.RTD_TEXT,
                        new byte[0],
                        payload
                );
                records[i] = record;
            }
        } else {
            for(int i = 0; i < dataToSend.size(); i++) {
                byte[] payload = dataToSend.get(i).getBytes(Charset.forName("UTF-8"));
                NdefRecord record = NdefRecord.createMime("text/plain", payload);
                records[i] = record;
            }
        }
        records[dataToSend.size()] = NdefRecord.createApplicationRecord(getPackageName());

        return records;
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        if(dataToSend.size() == 0) return null;

        NdefRecord[] records = createRecords();

        return new NdefMessage(records);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        dataToSend.clear();
    }
}
