package com.lkc.distributedassignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SMS_Service_Activity extends AppCompatActivity implements View.OnClickListener {
    Button composeMessage, inbox, sentMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_service);
        composeMessage = (Button)findViewById(R.id.buttonCompose);
        inbox = (Button)findViewById(R.id.buttonInbox);
        sentMessage = (Button)findViewById(R.id.buttonSentSMS);

        composeMessage.setOnClickListener(this);
        inbox.setOnClickListener(this);
        sentMessage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == composeMessage){

        }

        else if(v == inbox){

        }

        else if(v == sentMessage){

        }
    }

}
