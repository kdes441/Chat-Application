package com.cryptochat.theguys.cryptochat.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by kyle on 3/20/17.
 */

public class ChatClient {


    public static String username;
    //Global variables
    private static Socket connection;
    private static PrintStream output;
    private static BufferedReader input;
    private static JSONObject outgoingMessage;
    private static JSONObject incomingMessage;

    public static void sendMessage(String receiverName, String message) {

        //Format for messages using JSON
        outgoingMessage = new JSONObject();
        try {
            outgoingMessage.put("To", receiverName);
            outgoingMessage.put("From", username);
            outgoingMessage.put("MessageType", "regular");
            outgoingMessage.put("Message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Try to send the message_white
                try {
                    output.print(outgoingMessage.toString() + "\n");
                    output.flush();
                    Utils.output(outgoingMessage.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();

    }

    /*
     * Launch the application.
     */
    public static void startClient() {
        Thread incomingMessages = new Thread(new incomingMessages());
        incomingMessages.start();
    }

    //Class which handles incoming messages on a new thread
    private static class incomingMessages implements Runnable{


        //Listens for new incoming messages
        @Override
        public void run(){
            Boolean dontRun = true;
            //Had to create the connection here because andorid stoped it on main thread
            try {
                connection = new Socket(Utils.CHAT_URL,Utils.CHAT_PORT);
                output = new PrintStream(connection.getOutputStream());
                Utils.output("Server - Now connected to the server as "+ username +"\n");
            } catch (IOException e1) {
                e1.printStackTrace();
                //Setting doesn't Run to false stops the while loop from running
                dontRun = false;
            }

            //Format for messages using JSON
            outgoingMessage = new JSONObject();
            try {
                outgoingMessage.put("To", "Server");
                outgoingMessage.put("From", username);
                outgoingMessage.put("MessageType", "initiation");
                outgoingMessage.put("Message", "Init");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Try to send the message
            try {
                output.print(outgoingMessage.toString()+"\n");
                output.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String message = null;
            while(dontRun){
                //Waits for a message to come in
                try {
                    input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    message = input.readLine();
                    System.out.print(message);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


                //Parses the response from the server
                JSONObject response;
                try{
                    response = new JSONObject(message);
                    incomingMessage = response;
                    String from = (String) incomingMessage.get("From");
                    message = (String) incomingMessage.get("Message");
                    Utils.receivedMessage(from,message);
                } catch (JSONException e){
                    e.printStackTrace();
                    return;
                }

            }
        }

    }

}
