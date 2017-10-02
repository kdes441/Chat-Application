package com.cryptochat.theguys.cryptochat.Model;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cryptochat.theguys.cryptochat.Controller.LoginActivity;
import com.cryptochat.theguys.cryptochat.R;
import com.cryptochat.theguys.cryptochat.Utils.HttpService;
import com.cryptochat.theguys.cryptochat.Utils.Utils;
import com.cryptochat.theguys.cryptochat.Utils.Validator;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kyle on 3/25/17.
 */

public class SignUpModel {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String passwordConfirm;
    private Context c;

    public SignUpModel(Context c){
        this.c = c;
    }
    public void fillModel(ArrayList<String> userInformation){
        this.firstName = userInformation.get(0);
        this.lastName = userInformation.get(1);
        this.email = userInformation.get(2);
        this.username = userInformation.get(3);
        this.password = userInformation.get(4);
        this.passwordConfirm = userInformation.get(5);
    }

    public boolean validate(){

        boolean successful = false;

        Validator validator = new Validator();

        //Validates users input
        if(!validator.firstNameCheck(this.firstName)){
            Utils.toast(c.getResources().getString(R.string.error_invalid_fname), c);
            Utils.output("First name is invalid");

        } else if (!validator.lastNameCheck(this.lastName)){
            Utils.toast(c.getResources().getString(R.string.error_invalid_lname), c);
            Utils.output("Last name is invalid");

        } else if (!validator.emailCheck(this.email)){
            Utils.toast(c.getResources().getString(R.string.error_invalid_email), c);
            Utils.output("Email is invalid");

        } else if (!validator.usernameCheck(this.username)){
            Utils.toast(c.getResources().getString(R.string.error_invalid_username), c);
            Utils.output("Username is invalid");

        } else if (!validator.passwordCheck(this.password)){
            Utils.toast(c.getResources().getString(R.string.error_invalid_password), c);
            Utils.output("Password is invalid");

        } else if (!validator.passwordCheck(this.passwordConfirm)){
            Utils.toast(c.getResources().getString(R.string.error_invalid_password), c);
            Utils.output("Confirmation password is invalid");

        } else if(!this.password.equals(this.passwordConfirm)){
            Utils.toast(c.getResources().getString(R.string.error_invalid_password_match), c);
            Utils.output("Both passwords do not match.");

        } else{
            Utils.output("LoginModel input successfully made it through validation");
            successful = true;
        }

        return successful;
    }

    //Attempts to create a user account
    public void attemptSignUp(){

        //Creates a JSON object to be sent to server
        JSONObject userInformation = new JSONObject();
        try{
            userInformation.put("FirstName",this.firstName);
            userInformation.put("LastName",this.lastName);
            userInformation.put("Email",this.email);
            userInformation.put("Username",this.username);
            userInformation.put("Password",this.password);

        } catch (JSONException e){
            e.printStackTrace();
        }

        //Creates parameter for the post request
        RequestParams params = new RequestParams();
        params.put("UserInformation", userInformation.toString());

        HttpService.post(Utils.REGISTER_ROUTE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                JSONObject res;

                try {
                    res = new JSONObject(response).getJSONObject("RegisterAttempt");

                    if(res.getBoolean("SuccessfulAttempt")) {
                        //If successful go to LoginActivity
                        Intent intent = new Intent(c, LoginActivity.class);
                        c.startActivity(intent);

                    }

                    //Shows message_row on the screen
                    Toast.makeText(c,res.getString("ReturnMessage"),Toast.LENGTH_SHORT).show();


                } catch (JSONException ex){
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(c,R.string.error_network,Toast.LENGTH_SHORT).show();
                Utils.output(responseBody.toString());
            }

        });
    }

}
