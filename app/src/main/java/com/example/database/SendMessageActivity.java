package com.example.database;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.database.Contact;
import com.example.database.R;

public class SendMessageActivity extends Activity {
    ListView listView;
    EditText editMessage;
    ProgressDialog progressDialog;
    Handler progresshandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listcontacts);
        listView = (ListView) findViewById(R.id.contactsView);
        editMessage = (EditText) findViewById(R.id.editMessage);

        ArrayList<Contact> contacts = new ArrayList<Contact>();


        listView.setAdapter(new ContactsAdapter(this, contacts));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Messages.. Please wait!");

        progresshandler = new Handler() {
            public void handleMessage(Message msg) {
                progressDialog.dismiss();
                Toast.makeText(SendMessageActivity.this, "Messages Sent",
                        Toast.LENGTH_LONG).show();
            }
        };
    }

    class SendMessagesThread extends Thread {
        Handler handler;

        public SendMessagesThread(Handler handler) {
            this.handler = handler;
        }

        public void run() {
            SmsManager smsManager = SmsManager.getDefault();
            // Find out which contacts are selected
            for (int i = 0; i < listView.getCount(); i++) {
                View item = (View) listView.getChildAt(i);
                boolean selected = ((CheckBox) item.findViewById(R.id.selected)).isChecked();
                if (selected) {
                    String mobile = ((TextView) item.findViewById(R.id.mobile)).getText().toString();
                    try {
                        smsManager.sendTextMessage(mobile, null, editMessage.getText().toString(), null, null);
                    } catch (Exception ex) {
                        Log.d("Mobile", "Could not send message to " + mobile);
                    }
                }
            }
            Message m = handler.obtainMessage();
            handler.sendMessage(m);
        } // run
    } // Thread

    public void sendMessages(View v) {
        if (editMessage.getText().toString().length() > 0) {
            SendMessagesThread thread = new SendMessagesThread(progresshandler);
            thread.start();
            progressDialog.show();
        } else {
            Toast.makeText(this, "Please enter message!", Toast.LENGTH_LONG)
                    .show();
        }
    }

}