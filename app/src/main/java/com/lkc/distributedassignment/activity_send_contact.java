package com.lkc.distributedassignment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class activity_send_contact extends FragmentActivity{
    private static final int PICK_CONTACT = 1;
    private EditText contactName;
    private EditText mobileNumber;
    private EditText homePhone;
    private static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_contact);

        contactName = (EditText) findViewById(R.id.contactName);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        homePhone = (EditText) findViewById(R.id.homePhone);
    }

    public void send(View view) {

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch(reqCode)
        {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK)
                {
                    Uri contactData = data.getData();
                    Cursor cursor = getContentResolver().
                            query( ContactsContract.Contacts.CONTENT_URI,
                                    new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null,null);
                    if(cursor!=null){
                        while(cursor.moveToNext()){
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            contactName.setText(name);
                            Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE},
                                    " DISPLAY_NAME = '"+name+"'", null, null);
                            while(c.moveToNext()){
                                switch(c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))){
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :
                                        String mobileNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        this.mobileNumber.setText(mobileNumber);
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME :
                                        String homeNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        homePhone.setText(homeNumber);
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK :
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER :
                                }
                            }
                        }
                    }
                }
        }
    }

    public void searchContacts(View view) {
        EnableRuntimePermission();
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity_send_contact.this,
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(activity_send_contact.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(activity_send_contact.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(activity_send_contact.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(activity_send_contact.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }
}
