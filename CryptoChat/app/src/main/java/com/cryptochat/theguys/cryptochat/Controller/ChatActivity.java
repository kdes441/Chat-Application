package com.cryptochat.theguys.cryptochat.Controller;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cryptochat.theguys.cryptochat.Model.ChatModel;
import com.cryptochat.theguys.cryptochat.R;
import com.cryptochat.theguys.cryptochat.Utils.Utils;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by kyle on 3/27/17.
 */

public class ChatActivity extends AppCompatActivity {

    private Context c;
    private EditText messageInput;
    private ChatModel chatModel;
    private ListView listView;
    private ChatAdapter adapter;
    //Updates UI when a new message arrives
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String response = (String) msg.obj;
            adapter.updateData(response);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(Utils.CURRENT_CHAT);
        setContentView(R.layout.activity_chat);

        c = this;
        chatModel = new ChatModel(Utils.CURRENT_CHAT);
        chatModel.retrieveConversations();
        messageInput = (EditText) findViewById(R.id.editText_messageInput);
        listView = (ListView) findViewById(R.id.listView_chat);

        //Create an adapter for the listView and sets the data in it
        adapter = new ChatAdapter(getApplicationContext(),chatModel.retrieveConversations());
        listView.setAdapter(adapter);

        //Hides keyboard if it isn't already
        Utils.hideSoftKeyboard(this);

        //Assigns the handler to a handler in utils to update ui
        Utils.handlerChatWindow = handler;
    }

    //Handles back button press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.CURRENT_CHAT = "";
        switch (item.getItemId()) {
            case android.R.id.home:
                adapter.saveData();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Utils.CURRENT_CHAT = "";
        adapter.saveData();
        super.onBackPressed();
    }

    //This method is called when the users presses the send button(arrow)
    public void sendMessage(View view) {
        Utils.hideSoftKeyboard(this);
        String message = String.valueOf(messageInput.getText());

        //Checks if the message field is empty
        if(message.isEmpty())
            Utils.toast("No message",c);
        else{
            chatModel.sendMessage(Utils.USERNAME,message);
            adapter.updateData(Utils.messageToJSON(Utils.USERNAME, message));
            messageInput.setText("");
        }

    }

    //Loads and updates the listView
    public class ChatAdapter extends BaseAdapter{
        private final ArrayList mData;
        private Context mContext;

        //Constructor
        public ChatAdapter(Context context,Stack objects) {
            mData = new ArrayList();
            mData.addAll(objects);
            mContext = context;
        }

        //Updates the listView and adds a new message
        public void updateData(String newMessage){
            mData.add(newMessage);
            notifyDataSetChanged();
        }

        //Saves messages to the main storage
        public void saveData(){
            if (!mData.isEmpty()) {
                Stack conversation = new Stack();
                conversation.addAll(mData);
                Utils.SAVED_CHATS.put(chatModel.getChatParticipant(), conversation);
            }
        }

        @Override
        public int getCount() {
                return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            String item = getItem(position).toString();
            View rowView;
            TextView message;

            String messageContents[] = Utils.messageFromJSON(item);

            if(messageContents[0].equals(Utils.USERNAME)) {
                rowView = View.inflate(mContext, R.layout.message_blue, null);
                message = (TextView) rowView.findViewById(R.id.textView_messageBlue);
            } else {
                rowView = View.inflate(mContext, R.layout.message_white, null);
                message = (TextView) rowView.findViewById(R.id.textView_messageWhite);
            }
            message.setText(messageContents[1]);
            return rowView;
        }
    }
}
