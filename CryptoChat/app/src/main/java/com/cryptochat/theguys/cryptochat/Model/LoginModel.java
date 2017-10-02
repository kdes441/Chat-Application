package com.cryptochat.theguys.cryptochat.Model;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;

import com.cryptochat.theguys.cryptochat.Controller.MainActivity;
import com.cryptochat.theguys.cryptochat.R;
import com.cryptochat.theguys.cryptochat.Utils.HttpService;
import com.cryptochat.theguys.cryptochat.Utils.Utils;
import com.cryptochat.theguys.cryptochat.Utils.Validator;
import com.cryptochat.theguys.cryptochat.Utils.WSClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kyle on 3/11/17.
 */

public class LoginModel{

    /*
        Global Variables
     */
    private String username;
    private String password;
    private Context c;
    private ProgressBar progressBar;


    /*
        Constructor
     */
    public LoginModel(String username, String password, Context c,ProgressBar progressBar){
        this.username = username;
        this.password = password;
        this.c = c;
        this.progressBar = progressBar;
    }

    public boolean validate(){
        boolean successful = false;

        Validator validator = new Validator();

        if (!validator.usernameCheck(username)){
            Utils.toast(String.valueOf(R.string.error_invalid_username), c);
            Utils.output("Username is invalid");

        } else if (!validator.passwordCheck(password)){
            Utils.toast(String.valueOf(R.string.error_invalid_password), c);
            Utils.output("Password is invalid");

        } else{
            Utils.output("Login input successfully made it through validation");
            successful = true;
        }

        return successful;
    }



    //Attempts to create a user account
    public void attemptLogin(){

        //Creates a JSON object to be sent to server
        JSONObject userInformation = new JSONObject();
        try{
            userInformation.put("Username",this.username);
            userInformation.put("Password",this.password);

        } catch (JSONException e){
            e.printStackTrace();
        }

        //Creates parameter for the post request
        RequestParams params = new RequestParams();
        params.put("UserCreds", userInformation.toString());


        HttpService.post(Utils.LOGIN_ROUTE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                JSONObject res;
                boolean success;
                String returnMessage;


                try {
                    res = new JSONObject(response).getJSONObject("LoginAttempt");
                    success = res.getBoolean("SuccessfulAttempt");
                    returnMessage = res.getString("ReturnMessage");

                    //Checks if its successful which means the json will have other information
                    if(success) {
                        Utils.USERNAME = res.getString("Username");
                        Utils.USER_ID = res.getString("UserID");

                        //Creates socket connection with server and listens for incoming messages
                        //ChatClient.username = Utils.USERNAME;
                        //ChatClient.startClient();

                        //Creates a webSocket with the server
                        new WSClient();

                        //TEST
                        ///////////////////////////////////////
//                        Stack testStack = new Stack();
//                        String msg1 = Utils.messageToJSON("Deems","Don't know");
//                        String msg2 = Utils.messageToJSON("Kyle","What don't you know");
//                        String msg3 = Utils.messageToJSON("Deems","A lot manne");
//                        testStack.add(msg1);
//                        testStack.add(msg2);
//                        testStack.add(msg3);
//                        Utils.SAVED_CHATS.put("Deems",testStack);
//
//                        Stack testStack2 = new Stack();
//                        String msg4 = Utils.messageToJSON("Chris","Don't know");
//                        String msg5 = Utils.messageToJSON("Kyle","What don't you know");
//                        String msg6 = Utils.messageToJSON("Chris","Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor");
//                        testStack2.add(msg4);
//                        testStack2.add(msg5);
//                        testStack2.add(msg6);
//                        Utils.SAVED_CHATS.put("Chris",testStack2);
                        ///////////////////////////////////////

                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.INVISIBLE);
                        //If successful go to MainActivity
                        Intent intent = new Intent(c, MainActivity.class);
                        c.startActivity(intent);
                    } else {
                        Utils.toast(returnMessage, c);
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                } catch (JSONException ex){
                    ex.printStackTrace();
                    Utils.toast(String.valueOf(R.string.error_network), c);
                }

            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress((int) totalSize);
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Utils.toast(String.valueOf(R.string.error_network), c);
                Utils.output(responseBody.toString());
            }

        });
    }

}
