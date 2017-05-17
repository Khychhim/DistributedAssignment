package com.lkc.distributedassignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * class for sms service
 */
public class SMS_Service_Activity extends AppCompatActivity implements View.OnClickListener {
    Button composeMessage, inbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_service);
        composeMessage = (Button)findViewById(R.id.buttonCompose);
        inbox = (Button)findViewById(R.id.buttonInbox);

        composeMessage.setOnClickListener(this);
        inbox.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == composeMessage){
            Intent intent = new Intent(this, ComposeSMSActivity.class);
            startActivity(intent);
        }

        else if(v == inbox){
            Intent intent = new Intent(this, InboxActivity.class);
            startActivity(intent);
        }
    }

}
