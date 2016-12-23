package htmltest.ignite.com.rendertest;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    WebView webView;
    static String TAG = "MainActivity";
    Button searchButton;
    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = (Button) findViewById(R.id.searchButton);
        searchEditText = (EditText) findViewById(R.id.searchTextBox);
        searchButton.setOnClickListener(this);

        //Load some sample html data into the web view
        webView = (WebView) findViewById(R.id.testView);
        String inputHtml = "<html><head></head><body>Example <b>html</b> page here.</body></html>";
        webView.loadData(inputHtml,"text/html",null);

        //Read through user's SMS history (as an example)
        checkSMS();
    }

    /**
     * Read through all of user's past SMS messages
     */
    private String checkSMS() {
        //Query the SMS inbox
        Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
        List<TextMessage> messages = new ArrayList<TextMessage>();

        //Iterate over result data with a cursor
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(mSmsQueryUri, null, null, null, null);
            if (cursor == null) {
                //Something bad happened
                Log.i(TAG, "cursor is null. uri: " + mSmsQueryUri);
            }

            //Search through every entry in the result set
            for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {

                //Load data from the result set into a nice class for us to work with
                final String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                final long dateLong = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
                TextMessage newMessage = new TextMessage();
                newMessage.body = body;
                newMessage.date = new Date(dateLong);

                //Ignore all messages older than one hour
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                Date hourDate = calendar.getTime();

                if(newMessage.date.compareTo(hourDate) > 0) {
                    messages.add(newMessage);
                }

                //TODO- Filter messages so that they are in order and only for this specific query
                //TODO- You can send a unique code with each search. For example you might get three texts like this:

                //TODO- [01234,0,3] This is the first message
                //TODO- [01234,2,3] This is the second message
                //TODO- [01234,3,3] This is the third message

                //TODO- So you can filter by some unique ID code per search, and then also order the messages correctly
            }
        } catch (Exception e) {
            //Something bad happened
            Log.e(TAG, e.getMessage());
        } finally {
            //Close the cursor
            if(cursor != null) {
                cursor.close();
            }
        }

        //Print text messages to the debug console
        for(TextMessage message : messages) {
            Log.d(TAG,"["+message.date.toString()+"] "+message.body);
        }

        //Concat these messages into a single string to be returned
        StringBuilder stringBuilder = new StringBuilder();
        for(TextMessage message : messages) {
            stringBuilder.append(message.body);
        }

        //TODO- This is the data which you could render in your html view
        return stringBuilder.toString();

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG,"okay");
        Toast.makeText(this,"Searching!",Toast.LENGTH_SHORT).show();

        //TODO- Send SMS message here
        //TODO- Wait a few seconds
        //TODO- Call checkSMS()
    }

    private class TextMessage {
        public String body;
        public Date date;
    }
}
