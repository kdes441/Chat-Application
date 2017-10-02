package com.cryptochat.theguys.cryptochat.Model;


import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;

import com.cryptochat.theguys.cryptochat.Controller.Fragment.ContactsFragment;
import com.cryptochat.theguys.cryptochat.Utils.HttpService;
import com.cryptochat.theguys.cryptochat.Utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kyle on 3/11/17.
 */

public class ContactsModel{

    //TODO change the logic
    // instead of having an array of chats just have a hashMap with name and online status
    // save time and space instead of creating a new object for every contact
    // So I also have to change the adapter inside the ContactAdpater class to accpect hashMap and update the logic in there
    public List<ContactsModel> contactsModels = new ArrayList<>();
    private String contactUsername;
    private String onlineStatus;
    private ContactsFragment.ContactAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;


    //Constructor
    public ContactsModel(String contactUsername, String onlineStatus){
        this.contactUsername = contactUsername;
        this.onlineStatus = onlineStatus;
    }

    public ContactsModel() {

    }

    //Get username
    public String getUsername(){
        return this.contactUsername;
    }

    //Get online Status
    public String getOnlineStatus(){
        return this.onlineStatus;
    }

    //Gets the list of contacts
    public List<ContactsModel> getContactsModels(){
        return contactsModels;
    }

    //Sets the adapter, context and the swipeLayout
    public void setVariables(ContactsFragment.ContactAdapter adapter,Context context, SwipeRefreshLayout swipeRefreshLayout){
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.adapter = adapter;
        this.context = context;
    }


    //Sets the online status
    public String setOnlineStatus(String onlineStatus){
        this.onlineStatus = onlineStatus;
        return this.onlineStatus;
    }


    //Attempts to get user's contacts
    public void getContacts(){

        //Creates parameter for the post request
        RequestParams params = new RequestParams();
        params.put("UserID", Utils.USER_ID);

        HttpService.post(Utils.CONTACTS_ROUTE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                JSONObject json;
                JSONArray contacts;
                try {
                    json = new JSONObject(response);
                    contacts = json.getJSONArray("Contacts");

                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject row;
                        try {
                            row = contacts.getJSONObject(i);
                            contactsModels.add(new ContactsModel(row.getString("Username"), row.getString("OnlineStatus")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                //Utils.toast("Contacts refreshed", context);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Toast.makeText(c, R.string.error_network,Toast.LENGTH_SHORT).show();
                Utils.output(responseBody.toString());
            }

        });
    }

    //Clears out list
    public void clear(){
        adapter.clear();
    }
}
