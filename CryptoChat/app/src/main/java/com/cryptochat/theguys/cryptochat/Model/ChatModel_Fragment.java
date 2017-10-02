package com.cryptochat.theguys.cryptochat.Model;

import com.cryptochat.theguys.cryptochat.Utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by kyle on 3/28/17.
 */

public class ChatModel_Fragment {

    //Has all of the open chats
    private Map<String,String> chats = new HashMap<>();

    //Constructor
    public ChatModel_Fragment(){

        //Iterates through the chats in the hash map
        for(Map.Entry<String, Stack> entry : Utils.SAVED_CHATS.entrySet()) {
            Stack chat = entry.getValue();
            String mostRecent = (String) chat.peek();
            String contact = entry.getKey();

            //Adds the contacts name and the most recent message in the conversation to a map of chats
            chats.put(contact, mostRecent);
        }
    }
    //Refreshes the chats most recent message
    public void refresh(){
        //TODO maybe update individual chats instead of all of them
        HashMap<String,Stack> conversations = Utils.SAVED_CHATS;
        chats.clear();
        //Iterates through the chats in the hash map
        for(Map.Entry<String, Stack> entry : conversations.entrySet()){
            Stack chat = entry.getValue();
            String mostRecent = (String) chat.peek();
            String contact = entry.getKey();
            //Adds the contacts name and the most recent message in the conversation to a map of chats
            chats.put(contact,mostRecent);
        }
    }

    //Gets the chats
    public Map<String,String> getChats(){ return this.chats; }


}
