package com.lkc.distributedassignment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.lkc.distributedassignment.activity_send_contact.NAME;
import static com.lkc.distributedassignment.activity_send_contact.HOME;
import static com.lkc.distributedassignment.activity_send_contact.MOBILE;
import static com.lkc.distributedassignment.activity_send_contact.EMAIL;

public class activity_receive_contact extends FragmentActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private ArrayList<String> dataReceived = new ArrayList<>();
    private boolean hasPermission;

    private TextView waitingText;
    private Button addContact;
    private PendingIntent nfcPendingIntent;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_contact);
        EnableRuntimePermission();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null) {
            Toast.makeText(this, "NFC not available on this device", Toast.LENGTH_SHORT).show();
            finish();
        }

        waitingText = (TextView) findViewById(R.id.waiting);
        addContact = (Button) findViewById(R.id.addContact);
        addContact.setVisibility(View.INVISIBLE);
        addContact.setEnabled(false);
        waitingText.setVisibility(View.VISIBLE);
        waitingText.setText("Waiting to Recieve Contact");
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        handleNfcIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleNfcIntent(intent);
    }

    public void addToContacts(View view) {
        if(hasPermission) {
            ContentValues values = new ContentValues();
            values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 001);
            values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            values.put(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, dataReceived.get(NAME));
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, dataReceived.get(MOBILE));
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, dataReceived.get(HOME));
            values.put(ContactsContract.CommonDataKinds.Email.DATA1, dataReceived.get(EMAIL));
            getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
            Toast.makeText(this, "Contact has been added.", Toast.LENGTH_LONG).show();
        }
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity_receive_contact.this,
                Manifest.permission.WRITE_CONTACTS)) {
            Toast.makeText(activity_receive_contact.this, "CONTACTS permission allows " +
                    "us to Access CONTACTS app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity_receive_contact.this, new String[]{
                    Manifest.permission.WRITE_CONTACTS}, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case REQUEST_PERMISSION_CODE:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;
                } else {
                    Toast.makeText(activity_receive_contact.this, "Permission Canceled, " +
                            "Application needs permission to access contact data and use NFC",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void handleNfcIntent(Intent nfcIntent) {
        if(nfcIntent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(nfcIntent.getAction())) {
            waitingText.setText("Recieving...");
            Parcelable[] receivedArray =
                    nfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                dataReceived.clear();
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for(NdefRecord record : attachedRecords) {
                    String payload = new String(record.getPayload());

                    if(payload.equals(getPackageName())) continue;
                    dataReceived.add(payload);
                }

                Toast.makeText(this, "Received " + dataReceived.get(0)
                        + "'s contact info", Toast.LENGTH_LONG).show();
                addContact.setVisibility(View.VISIBLE);
                addContact.setEnabled(true);
                waitingText.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(this, "Did not receive Data", Toast.LENGTH_LONG).show();
            }
        }
    }
}
