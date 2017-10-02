package com.cryptochat.theguys.cryptochat.Utils;

import android.os.Build;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by kyle on 4/7/17.
 */

public class WSClient {

    public  static WebSocketClient  webSocketClient;

    public WSClient(){

        if ("google_sdk".equals( Build.PRODUCT )) {
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }

        try {
            webSocketClient = new WebSocketClient(new URI(Utils.WS_URL)){

                @Override
                public void onMessage( String message ) {
                    //Parses the response from the server
                    JSONObject response;
                    try{
                        response = new JSONObject(message);
                        String from = (String) response.get("From");
                        message = (String) response.get("Message");
                        Utils.receivedMessage(from,message);
                    } catch (JSONException e){
                        e.printStackTrace();
                        return;
                    }
                }

                @Override
                public void onOpen( ServerHandshake handshake ) {
                    JSONObject outgoingMessage = new JSONObject();
                    try {
                        outgoingMessage.put("To", "server");
                        outgoingMessage.put("From", Utils.USERNAME);
                        outgoingMessage.put("MessageType", "initiation");
                        outgoingMessage.put("Message", "Init");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    send(outgoingMessage.toString());
                }

                @Override
                public void onClose( int code, String reason, boolean remote ) {

                }

                @Override
                public void onError( Exception ex ) {
                    Utils.output("WSClient " + ex);
                }
            };

            webSocketClient.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String recipient, String message, String sender){
        JSONObject outgoingMessage = new JSONObject();
        try {
            outgoingMessage.put("To", recipient);
            outgoingMessage.put("From", sender);
            outgoingMessage.put("MessageType", "regular");
            outgoingMessage.put("Message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webSocketClient.send(outgoingMessage.toString());
    }
}
