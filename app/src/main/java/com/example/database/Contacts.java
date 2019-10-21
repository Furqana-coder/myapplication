package com.example.database;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class Contacts extends AppCompatActivity {

    private static final int RESULT_PICK_CONTACT = 1;
    public String tag = "MainActivity";
   // DatabaseContacts myDb;
    ListView listView;
    SearchView SearchView;
    TextView selectedItem;
    CheckedTextView CheckedTextView;
    boolean[] checkedItems;

    ArrayList<String> StoreContacts = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> ContactList = new ArrayList<>();
    Cursor cursor;
    String name, phonenumber;
    public static final int RequestPermissionCode = 1;
    public static final String Tag = "hello";
    Button button, select, load;
    EditText name1, number,editMessage;
    ProgressDialog progressDialog;
    Handler progresshandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        listView = (ListView) findViewById(R.id.contactsView);
        editMessage = (EditText) findViewById(R.id.editMessage);
        EnableRuntimePermission();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Messages.. Please wait!");

        progresshandler = new Handler() {
            public void handleMessage(Message msg) {
                progressDialog.dismiss();
                Toast.makeText(Contacts.this, "Messages Sent",
                        Toast.LENGTH_LONG).show();
            }
        };
    }


    public void showMessage(String title, final String Message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();

    }





    public void GetContactsIntoArrayList() {

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            StoreContacts.add(name + " " + ":" + " " + phonenumber);
            //  StoreContacts.add(phonenumber);
            //myDb.insertData(name,phonenumber);
           // myDb.insertData(name,phonenumber);
           // Log.d("list", myDb.toString());

        }
        if (cursor != null) {
            cursor.close();
        }

   /*     cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor != null && cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    String phone = cursor.getString(cursor.getColumnIndex(phonenumber));
                    //ContentResolver contentResolver = ctx.getContentResolver();
                    boolean hasPhoneNumberFlag = false;

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(phonenumber)));

                    if (hasPhoneNumber > 0) {

                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                        // Query and loop for every phone number of the contact
                        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, phone + " = ?", new String[]{phone}, null);
                        int count = 0;
                        if (phoneCursor != null && phoneCursor.getCount() != 0) {
                            int c = phoneCursor.getCount();
                            while (phoneCursor.moveToNext()) {
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(phone));
                            }

                        }
                        if (phoneCursor != null) {
                            phoneCursor.close();
                        }

                        // insert in database
                    }

                }

            }

            if (cursor != null) {
                cursor.close();
            }

        }
*/


    }
    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Contacts.this,
                Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(Contacts.this, "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(Contacts.this, new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    Contacts.this,
                    Manifest.permission.READ_SMS)) {

                Toast.makeText(Contacts.this, "CONTACTS permission allows us to Access Messages app", Toast.LENGTH_LONG).show();

            } else {

                ActivityCompat.requestPermissions(Contacts.this, new String[]{
                        Manifest.permission.READ_SMS}, RequestPermissionCode);

            }
        }




    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(Contacts.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(Contacts.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
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

    public void sendMessages (View v){
        if (editMessage.getText().toString().length() > 0) {
            Contacts.SendMessagesThread thread = new Contacts.SendMessagesThread(progresshandler);
            thread.start();
            progressDialog.show();
        } else {
            Toast.makeText(this, "Please enter message!", Toast.LENGTH_LONG)
                    .show();
        }
    }
}