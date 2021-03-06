package com.lkc.distributedassignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void smsService(View view) {
        Intent intent = new Intent(this, SMS_Service_Activity.class);
        startActivity(intent);
    }

    public void sendContact(View view) {
        Intent intent = new Intent(this, activity_send_contact.class);
        startActivity(intent);
    }

    public void receiveContact(View view) {
        Intent intent = new Intent(this, activity_receive_contact.class);
        startActivity(intent);
    }
}
