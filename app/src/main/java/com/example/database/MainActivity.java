package com.example.database;

import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String ASYNC_TASK_TAG = "ASYNC_TASK";

    private Button executeAsyncTaskButton;
    private Button cancelAsyncTaskButton;
    private ProgressBar asyncTaskProgressBar;
    private TextView asyncTaskLogTextView;

    private MyAsyncTask myAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setTitle("dev2qa.com - AsyncTask Example");

        this.executeAsyncTaskButton = (Button)findViewById(R.id.executeAsyncTaskButton);
        this.executeAsyncTaskButton.setEnabled(true);

        this.cancelAsyncTaskButton = (Button)findViewById(R.id.cancelAsyncTaskButton);
        this.cancelAsyncTaskButton.setEnabled(false);

        this.asyncTaskProgressBar = (ProgressBar)findViewById(R.id.asyncTaskProgressBar);
        this.asyncTaskLogTextView = (TextView)findViewById(R.id.asyncTaskLogTextView);

        executeAsyncTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Need to create a new MyAsyncTask instance for each call,
                // otherwise there will through an exception.
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute(Integer.parseInt("10"));

                executeAsyncTaskButton.setEnabled(false);
                cancelAsyncTaskButton.setEnabled(true);
            }
        });

        cancelAsyncTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancel a running task, then MyAsyncTask's onCancelled(String result) method will be invoked.
                myAsyncTask.cancel(true);
            }
        });
    }

    // MyAsyncTask is used to demonstrate async task process.
    private class MyAsyncTask extends AsyncTask<Integer, Integer, String>{

        // onPreExecute() is used to do some UI operation before performing background tasks.
        @Override
        protected void onPreExecute() {
            asyncTaskLogTextView.setText("Loading");
            Log.i(ASYNC_TASK_TAG, "onPreExecute() is executed.");
        }

        // doInBackground(String... strings) is used to execute background task, can not modify UI component in this method.
        // It return a String object which can be used in onPostExecute() method.
        @Override
        protected String doInBackground(Integer... inputParams) {

            StringBuffer retBuf = new StringBuffer();
            boolean loadComplete = false;

            try
            {
                Log.i(ASYNC_TASK_TAG, "doInBackground(" + inputParams[0] + ") is invoked.");

                int paramsLength = inputParams.length;
                if(paramsLength > 0) {
                    Integer totalNumber = inputParams[0];
                    int totalNumberInt = totalNumber.intValue();

                    for(int i=0;i < totalNumberInt; i++)
                    {
                        // First calculate progress value.
                        int progressValue = (i * 100 ) / totalNumberInt;

                        //Call publishProgress method to invoke onProgressUpdate() method.
                        publishProgress(progressValue);

                        // Sleep 0.2 seconds to demo progress clearly.
                        Thread.sleep(200);
                    }

                    loadComplete = true;
                }
            }catch(Exception ex)
            {
                Log.i(ASYNC_TASK_TAG, ex.getMessage());
            }finally {
                if(loadComplete) {
                    // Load complete display message.
                    retBuf.append("Load complete.");
                }else
                {
                    // Load cancel display message.
                    retBuf.append("Load canceled.");
                }
                return retBuf.toString();
            }
        }

        // onPostExecute() is used to update UI component and show the result after async task execute.
        @Override
        protected void onPostExecute(String result) {
            Log.i(ASYNC_TASK_TAG, "onPostExecute(" + result + ") is invoked.");
            // Show the result in log TextView object.
            asyncTaskLogTextView.setText(result);

            asyncTaskProgressBar.setProgress(100);

            executeAsyncTaskButton.setEnabled(true);
            cancelAsyncTaskButton.setEnabled(false);
        }

        // onProgressUpdate is used to update async task progress info.
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i(ASYNC_TASK_TAG, "onProgressUpdate(" + values + ") is called");
            asyncTaskProgressBar.setProgress(values[0]);
            asyncTaskLogTextView.setText("loading..." + values[0] + "%");
        }

        // onCancelled() is called when the async task is cancelled.
        @Override
        protected void onCancelled(String result) {
            Log.i(ASYNC_TASK_TAG, "onCancelled(" + result + ") is invoked.");
            // Show the result in log TextView object.
            asyncTaskLogTextView.setText(result);
            asyncTaskProgressBar.setProgress(0);

            executeAsyncTaskButton.setEnabled(true);
            cancelAsyncTaskButton.setEnabled(false);
        }
    }
}
/*

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_PERMISSION_KEY = 1;
    ArrayList<HashMap<String, String>> smsList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpList = new ArrayList<HashMap<String, String>>();
    static MainActivity inst;
    LoadSms loadsmsTask;
    InboxAdapter adapter, tmpadapter;;
    ListView listView;
    FloatingActionButton fab_new;
    ProgressBar loader;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CacheUtils.configureCache(this);

        listView = (ListView) findViewById(R.id.listView);
        loader = (ProgressBar) findViewById(R.id.loader);
        fab_new = (FloatingActionButton) findViewById(R.id.fab_new);

        listView.setEmptyView(loader);


        fab_new.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewSmsActivity.class));
            }
        });








    }




    public void init()
    {
        smsList.clear();
        try{
            tmpList = (ArrayList<HashMap<String, String>>)Function.readCachedFile  (MainActivity.this, "smsapp");
            tmpadapter = new InboxAdapter(MainActivity.this, tmpList);
            listView.setAdapter(tmpadapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    loadsmsTask.cancel(true);
                    Intent intent = new Intent(MainActivity.this, Chat.class);
                    intent.putExtra("name", tmpList.get(+position).get(Function.KEY_NAME));
                    intent.putExtra("address", tmpList.get(+position).get(Function.KEY_PHONE));
                    intent.putExtra("thread_id", tmpList.get(+position).get(Function.KEY_THREAD_ID));
                    startActivity(intent);
                }
            });
        }catch(Exception e) {}

    }









    class LoadSms extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            smsList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            try {
                Uri uriInbox = Uri.parse("content://sms/inbox");

                Cursor inbox = getContentResolver().query(uriInbox, null, "address IS NOT NULL) GROUP BY (thread_id", null, null); // 2nd null = "address IS NOT NULL) GROUP BY (address"
                Uri uriSent = Uri.parse("content://sms/sent");
                Cursor sent = getContentResolver().query(uriSent, null, "address IS NOT NULL) GROUP BY (thread_id", null, null); // 2nd null = "address IS NOT NULL) GROUP BY (address"
                Cursor c = new MergeCursor(new Cursor[]{inbox,sent}); // Attaching inbox and sent sms


                if (c.moveToFirst()) {
                    for (int i = 0; i < c.getCount(); i++) {
                        String name = null;
                        String phone = "";
                        String _id = c.getString(c.getColumnIndexOrThrow("_id"));
                        String thread_id = c.getString(c.getColumnIndexOrThrow("thread_id"));
                        String msg = c.getString(c.getColumnIndexOrThrow("body"));
                        String type = c.getString(c.getColumnIndexOrThrow("type"));
                        String timestamp = c.getString(c.getColumnIndexOrThrow("date"));
                        phone = c.getString(c.getColumnIndexOrThrow("address"));



                        CacheUtils.readFile(thread_id);
                        if(name == null)
                        {
                            name = Function.getContactbyPhoneNumber(getApplicationContext(), c.getString(c.getColumnIndexOrThrow("address")));
                            CacheUtils.writeFile(thread_id, name);
                        }


                        smsList.add(Function.mappingInbox(_id, thread_id, name, phone, msg, type, timestamp, Function.converToTime(timestamp)));
                        c.moveToNext();
                    }
                }
                c.close();

            }catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Collections.sort(smsList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging sms by timestamp decending
            ArrayList<HashMap<String, String>> purified = Function.removeDuplicates(smsList); // Removing duplicates from inbox & sent
            smsList.clear();
            smsList.addAll(purified);

            // Updating cache data
            try{
                Function.createCachedFile (MainActivity.this,"smsapp", smsList);
            }catch (Exception e) {}
            // Updating cache data

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            if(!tmpList.equals(smsList))
            {
                adapter = new InboxAdapter(MainActivity.this, smsList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            final int position, long id) {
                        Intent intent = new Intent(MainActivity.this, Chat.class);
                        intent.putExtra("name", smsList.get(+position).get(Function.KEY_NAME));
                        intent.putExtra("address", tmpList.get(+position).get(Function.KEY_PHONE));
                        intent.putExtra("thread_id", smsList.get(+position).get(Function.KEY_THREAD_ID));
                        startActivity(intent);
                    }
                });
            }



        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_PERMISSION_KEY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    init();
                    loadsmsTask = new LoadSms();
                    loadsmsTask.execute();
                } else
                {
                    Toast.makeText(MainActivity.this, "You must accept permissions.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        String[] PERMISSIONS = {Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
        if(!Function.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }else{

            init();
            loadsmsTask = new LoadSms();
            loadsmsTask.execute();
        }

    }




    @Override
    public void onStart() {
        super.onStart();


    }

}







class InboxAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap< String, String >> data;
    public InboxAdapter(Activity a, ArrayList < HashMap < String, String >> d) {
        activity = a;
        data = d;
    }
    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        InboxViewHolder holder = null;
        if (convertView == null) {
            holder = new InboxViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.conversation_list_item, parent, false);

            holder.inbox_thumb = (ImageView) convertView.findViewById(R.id.inbox_thumb);
            holder.inbox_user = (TextView) convertView.findViewById(R.id.inbox_user);
            holder.inbox_msg = (TextView) convertView.findViewById(R.id.inbox_msg);
            holder.inbox_date = (TextView) convertView.findViewById(R.id.inbox_date);

            convertView.setTag(holder);
        } else {
            holder = (InboxViewHolder) convertView.getTag();
        }
        holder.inbox_thumb.setId(position);
        holder.inbox_user.setId(position);
        holder.inbox_msg.setId(position);
        holder.inbox_date.setId(position);

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(position);
        try {
            holder.inbox_user.setText(song.get(Function.KEY_NAME));
            holder.inbox_msg.setText(song.get(Function.KEY_MSG));
            holder.inbox_date.setText(song.get(Function.KEY_TIME));

            String firstLetter = String.valueOf(song.get(Function.KEY_NAME).charAt(0));

        } catch (Exception e) {}
        return convertView;
    }
}


class InboxViewHolder {
    ImageView inbox_thumb;
    TextView inbox_user, inbox_msg, inbox_date;
}
*/