package com.lkc.distributedassignment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * class to compose a new message
 */
public class ComposeSMSActivity extends AppCompatActivity implements View.OnClickListener{
    Button buttonSend;
    EditText etPhoneNumber,etMessageContent;
    String phoneNumber, messageContent;
    private static final int REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_sms);

        buttonSend = (Button)findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(this);
        etMessageContent = (EditText)findViewById(R.id.editTextMessageDetail);
        etPhoneNumber = (EditText)findViewById(R.id.editTextPhoneNumber);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonSend){
            phoneNumber = etPhoneNumber.getText().toString();
            messageContent = etMessageContent.getText().toString();
            //avoid empty message body
            if(messageContent.isEmpty()){
                messageContent = " ";
            }
            //avoid no phone number input
            if(phoneNumber.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please enter phone number",Toast.LENGTH_SHORT).show();
            }

            else if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
                }else{
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},REQUEST_SEND_SMS);
                }
            }else{//send the message
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, messageContent, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults){
        switch(requestCode){
            case REQUEST_SEND_SMS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){//send the message
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNumber, null, messageContent, null, null);
                        Toast.makeText(getApplicationContext(), "SMS sent",Toast.LENGTH_SHORT).show();
                        finish();
                }else {
                    Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_SHORT).show();
                }
            break;

        }
    }
}
