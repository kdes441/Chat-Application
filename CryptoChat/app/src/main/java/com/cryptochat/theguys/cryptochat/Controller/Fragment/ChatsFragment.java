package com.cryptochat.theguys.cryptochat.Controller.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cryptochat.theguys.cryptochat.Controller.ChatActivity;
import com.cryptochat.theguys.cryptochat.Controller.MainActivity;
import com.cryptochat.theguys.cryptochat.Model.ChatModel_Fragment;
import com.cryptochat.theguys.cryptochat.R;
import com.cryptochat.theguys.cryptochat.Utils.Utils;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private MainActivity mainActivity;
    private ChatModel_Fragment chatModel;
    private ChatAdapter adapter;
    private String messageParticipant;
    private MyReceiver r;
    private ListView listView;
    private TextView emptyText;
    //Updates UI when a new message arrives
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refresh();
        }
    };
    private OnFragmentInteractionListener mListener;

    public ChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        listView = (ListView) view.findViewById(R.id.listView_chat_fragment);
        mainActivity = (MainActivity) getActivity();
        chatModel= new ChatModel_Fragment();


        adapter = new ChatAdapter(mainActivity.getApplicationContext(),chatModel.getChats());
        listView.setAdapter(adapter);

        //Sets the listView to show default text if it is empty
        emptyText = (TextView) view.findViewById(R.id.empty_chat_list);
        //listView.setEmptyView(emptyText);

        //Creates a context menu for rows in the listView
        registerForContextMenu(listView);

        //Listens for click on contact in the contact menu
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                shortClick(view);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                messageParticipant = ((Map.Entry<String, String>) adapter.getItem(position)).getKey();
                return false;
            }
        });

        final SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_chat);

        //Listening for pull down refresh
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                chatModel.refresh();
                adapter.updateData(chatModel.getChats());
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        Utils.handlerChatPreview = handler;

        return view;
    }

    //Refreshes the message listView
    public void refresh(){
        adapter.clear();
        chatModel.refresh();
        adapter.updateData(chatModel.getChats());
        if (adapter.getCount() == 0) {
            listView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, v.getId(), 0, "Delete Chat");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            Utils.SAVED_CHATS.remove(messageParticipant);
            refresh();
            return true;
        }
        return false;
    }

    //Handles the short click on chat to open chat
    public void shortClick(View view){
        TextView contact = (TextView) view.findViewById(R.id.textView_chatParticipant);

        String contactName = (String) contact.getText();
        Utils.CURRENT_CHAT = contactName;

        //If successful go to MainActivity
        Intent intent = new Intent(mainActivity, ChatActivity.class);
        mainActivity.startActivity(intent);

    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onChatsFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mainActivity.getBaseContext()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(mainActivity.getBaseContext()).registerReceiver(r,
                new IntentFilter("TAG_REFRESH"));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onChatsFragmentInteraction(Uri uri);
    }

    public class ChatAdapter extends BaseAdapter {
        private final ArrayList mData;
        private Context mContext;

        //Constructor
        public ChatAdapter(Context context,Map<String,String> objects) {
            mData = new ArrayList();
            mData.addAll(objects.entrySet());
            mContext = context;
        }

        //Clears out current list content
        public  void clear(){
            mData.clear();
        }

        //Updates the listView and adds a new message
        //TODO I think the problem lies here where the most recent message bubble is changed
        public void updateData(Map<String,String> object){
            mData.addAll(0,object.entrySet());
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(getCount() - position - 1);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Map.Entry<String,String> item = (Map.Entry<String,String>) getItem(position);
            View rowView = View.inflate(mContext, R.layout.row_chat, null);

            TextView recentMessage = (TextView) rowView.findViewById(R.id.textView_recentMessage);
            TextView contact = (TextView) rowView.findViewById(R.id.textView_chatParticipant) ;

            String messageContents[] = Utils.messageFromJSON(item.getValue());
            recentMessage.setText(messageContents[1]);
            contact.setText(item.getKey());

            return rowView;
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }
}
