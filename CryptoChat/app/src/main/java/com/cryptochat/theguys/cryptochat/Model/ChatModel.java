package com.cryptochat.theguys.cryptochat.Model;


import com.cryptochat.theguys.cryptochat.Utils.Utils;
import com.cryptochat.theguys.cryptochat.Utils.WSClient;

import java.util.Stack;

/**
 * Created by kyle on 3/11/17.
 */

public class ChatModel {

    private String chatParticipant;

    //Constructor
    public ChatModel(String chatParticipant){
        this.chatParticipant = chatParticipant;
    }

    //Sends message to user
    public void sendMessage(String sender, String message){
        WSClient.sendMessage(chatParticipant,message,sender);
    }

    //Gets chat participant
    public String getChatParticipant(){
        return this.chatParticipant;
    }

    //TODO Maybe I should encrypt the hashmap first or w.e
    //Store the users conversation
//    public void storeConversation(){
//        Utils.SAVED_CHATS.put(chatParticipant,conversation);
//        conversation.clear();
//    }

    //Gets the users conversation with someone
    public Stack retrieveConversations(){
        //Checks if there is an open chat with the particular
        //If they do than retrieve the convo else do nothing
        if(Utils.SAVED_CHATS.containsKey(chatParticipant))
            return Utils.SAVED_CHATS.get(chatParticipant);
        else{
            return new Stack();
        }

    }
}
