package com.cryptochat.theguys.cryptochat.Controller.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptochat.theguys.cryptochat.Controller.ChatActivity;
import com.cryptochat.theguys.cryptochat.Controller.MainActivity;
import com.cryptochat.theguys.cryptochat.Model.ContactsModel;
import com.cryptochat.theguys.cryptochat.R;
import com.cryptochat.theguys.cryptochat.Utils.HttpService;
import com.cryptochat.theguys.cryptochat.Utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static ContactsModel contactsModel;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ContactAdapter adapter;
    private MainActivity mainActivity;
    private SwipeRefreshLayout swipeRefresh;
    private Context c;
    private String contactName;
    private int contactPosition;


    private OnFragmentInteractionListener mListener;

    public ContactsFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Contacts main view
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView_contacts);
        registerForContextMenu(listView);
        mainActivity = (MainActivity) getActivity();
        c = mainActivity.getBaseContext();

        contactsModel = new ContactsModel();
        contactsModel.getContacts();

        adapter = new ContactAdapter(mainActivity.getApplicationContext(),contactsModel.getContactsModels());
        listView.setAdapter(adapter);


        //Listens for click on contact in the contact menu
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                handleClick(view);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                contactName = ((ContactsModel) adapter.getItem(position)).getUsername();
                contactPosition = position;
                return false;
            }
        });

        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_contacts);

        //Listening for pull down refresh
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                contactsModel.getContacts();
            }
        });

        contactsModel.setVariables(adapter,mainActivity.getBaseContext(),swipeRefresh);

        return view;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Delete Contact");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (getUserVisibleHint()) {
            deleteContact();
            Utils.SAVED_CHATS.remove(contactName);
            //adapter.deleteRow();
            adapter.contactsModelList.remove(contactPosition);

            adapter.notifyDataSetChanged();
            //refresh();
            return true;
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Handles contacts click
    public void handleClick(View view){
        TextView contact = (TextView) view.findViewById(R.id.text_ContactName);
        TextView status = (TextView) view.findViewById(R.id.text_ContactOnlineStatus);

        String contactName = (String) contact.getText();
        String contactStatus = (String) status.getText();

        //If the user is online create a chat with the user
        if(contactStatus.equals("Online")){
            Utils.CURRENT_CHAT = contactName;

            //If successful go to MainActivity
            Intent intent = new Intent(mainActivity, ChatActivity.class);
            mainActivity.startActivity(intent);
        } else
            Toast.makeText(mainActivity.getBaseContext(),R.string.error_contact_offline,Toast.LENGTH_SHORT).show();
    }

    //Delete contact from users friends list
    private void deleteContact() {
        //Creates parameter for the post request
        RequestParams params = new RequestParams();
        params.put("ContactName", contactName);
        params.put("UserID", Utils.USER_ID);

        HttpService.post(Utils.DELETE_CONTACT_ROUTE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                JSONObject res;
                boolean success;
                String returnMessage;

                //Parses JSON message
                try {
                    res = new JSONObject(response).getJSONObject("DeleteContactAttempt");
                    success = res.getBoolean("SuccessfulAttempt");
                    returnMessage = res.getString("ReturnMessage");

                    //Checks if its successful which means the json will have other information
                    if (success) {
                        Utils.toast(returnMessage, c);

                    } else
                        Utils.toast(returnMessage, c);


                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Utils.toast(String.valueOf(R.string.error_network), c);
                }
                swipeRefresh.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Utils.toast(String.valueOf(R.string.error_network), c);
                Utils.output(responseBody.toString());
            }

            @Override
            public void onStart() {
                swipeRefresh.setRefreshing(true);
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onContactsFragmentInteraction(Uri uri);
    }

    //TODO besides fuck android check for update ever so often for contacts list but no polling
    public class ContactAdapter extends BaseAdapter{

        public List<ContactsModel> contactsModelList;
        private Context mContext;

        public ContactAdapter(Context context,List<ContactsModel> objects) {
            contactsModelList = objects;
            mContext = context;
        }

        @Override
        public int getCount() {
            return contactsModelList.size();
        }

        @Override
        public Object getItem(int position) {
            return contactsModelList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        //Clears out current list content
        public  void clear(){
            contactsModelList.clear();
        }

        public View getView(int position, View convertView, ViewGroup parent){
            View rowView = View.inflate(mContext, R.layout.row_contact, null);

            TextView contactName = (TextView)rowView.findViewById(R.id.text_ContactName);
            TextView contactOnlineStatus = (TextView)rowView.findViewById(R.id.text_ContactOnlineStatus);
            ImageView contactAvatar = (ImageView)rowView.findViewById(R.id.image_ContactAvatar);
            contactAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_person));
            contactName.setText(contactsModelList.get(position).getUsername());

            //Change color for online status
            if(contactsModelList.get(position).getOnlineStatus().equals("Online")){
                contactOnlineStatus.setTextColor(getResources().getColor(R.color.online));
            } else {
                contactOnlineStatus.setTextColor(getResources().getColor(R.color.offline));
            }
            contactOnlineStatus.setText(contactsModelList.get(position).getOnlineStatus());
            return rowView;
        }

    }
}
