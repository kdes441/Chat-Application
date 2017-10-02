package com.cryptochat.theguys.cryptochat.Controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.cryptochat.theguys.cryptochat.R;
import com.cryptochat.theguys.cryptochat.Utils.HttpService;
import com.cryptochat.theguys.cryptochat.Utils.Utils;
import com.cryptochat.theguys.cryptochat.Utils.Validator;
import com.cryptochat.theguys.cryptochat.Utils.WSClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kyle on 4/10/17.
 */

public class SettingsActivity extends AppCompatActivity {

    private Context c;
    private AlertDialog dialog;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        c = this;
    }

    //Handles the action of pressing the logout button
    public void logoutAction(View view) {
        //Creates parameter for the post request
        RequestParams params = new RequestParams();
        params.put("UserID", Utils.USER_ID);
        //Updates db that the user has logged out
        HttpService.post(Utils.LOGOUT_ROUTE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Utils.toast(String.valueOf(R.string.error_network), c);
                Utils.output(responseBody.toString());
            }
        });

        //TODO check if can using the onClose message on server or I have to send a close message
//        WSClient.sendMessage("Server","Close",Utils.USERNAME);
        WSClient.webSocketClient.close();
        Utils.SAVED_CHATS = new HashMap<>();
        Utils.USERNAME = null;
        Utils.USER_ID = null;
        Utils.CURRENT_CHAT = null;
        Utils.handlerChatPreview = null;
        Utils.handlerChatWindow = null;
        Intent intent = new Intent(c,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //Handles the action of pressing the update password button
    public void updatePasswordAction(View view) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);

        //Creates the visuals for the dialog
        builder.setTitle("Update Password");

        View mView = getLayoutInflater().inflate(R.layout.dialog_update_password, null);
        builder.setView(mView);

        final EditText newPassword = (EditText) mView.findViewById(R.id.editText_new_password);
        final EditText oldPassword = (EditText) mView.findViewById(R.id.editText_old_password);
        final EditText confirmPassword = (EditText) mView.findViewById(R.id.editText_confirm_new_password);
        progressBar = (ProgressBar) mView.findViewById(R.id.progressBar_add_contact);
        progressBar.setVisibility(View.INVISIBLE);

        //Creates and houses the cancel button action
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newPassword.setText("");
                confirmPassword.setText("");
                oldPassword.setText("");
            }
        });
        //Creates the update button
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //Creates the dialog and displays it
        dialog = builder.create();
        dialog.show();

        //Handles the update button action
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                if (wantToCloseDialog)
                    dialog.dismiss();

                String newPass = newPassword.getText().toString().trim();
                String confirmPass = confirmPassword.getText().toString().trim();
                String oldPass = oldPassword.getText().toString().trim();

                Validator validator = new Validator();
                if (!validator.passwordCheck(newPass))
                    Utils.toast("New password must be at least 6 characters long", c);
                else if (!validator.passwordCheck(confirmPass))
                    Utils.toast("Confirmation password doesn't match new password", c);
                else if (!validator.passwordCheck(oldPass))
                    Utils.toast("Old password is incorrect", c);
                else if (!newPass.equals(confirmPass))
                    Utils.toast(getResources().getString(R.string.error_invalid_password_match), c);
                else
                    sendPassword(newPass, oldPass);
            }
        });

        //Changes the text colors of the buttons
        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(getResources().getColor(R.color.primary));
        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(getResources().getColor(R.color.primary));
    }

    //Handles the action of pressing the add contact button
    public void addContactAction(View view) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        //Creates the visuals for the dialog
        builder.setTitle("Add Contact");

        View mView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
        builder.setView(mView);

        final EditText contactName = (EditText) mView.findViewById(R.id.editText_add_contact);
        progressBar = (ProgressBar) mView.findViewById(R.id.progressBar_add_contact);
        progressBar.setVisibility(View.INVISIBLE);

        //Creates and houses the cancel button action
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                contactName.setText("");
            }
        });
        //Creates the update button
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //Creates the dialog and displays it
        dialog = builder.create();
        dialog.show();

        //Handles the update button action
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                if (wantToCloseDialog)
                    dialog.dismiss();

                String name = contactName.getText().toString().trim();
                if (!name.isEmpty() && name.length() > 3)
                    sendContact(name);
                else
                    Utils.toast("Contacts name must be at least 4 characters long", c);
            }
        });

        //Changes the text colors of the buttons
        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(getResources().getColor(R.color.primary));
        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(getResources().getColor(R.color.primary));
    }

    //Method sends the data to the server
    private void sendContact(String contact) {

        //Creates parameter for the post request
        RequestParams params = new RequestParams();
        params.put("ContactName", contact);
        params.put("UserID", Utils.USER_ID);

        HttpService.post(Utils.ADD_CONTACT_ROUTE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                JSONObject res;
                boolean success;
                String returnMessage;

                //Parses JSON message
                try {
                    res = new JSONObject(response).getJSONObject("AddContactAttempt");
                    success = res.getBoolean("SuccessfulAttempt");
                    returnMessage = res.getString("ReturnMessage");

                    //Checks if its successful which means the json will have other information
                    if (success) {
                        dialog.hide();
                        Utils.toast(returnMessage, c);

                    } else
                        Utils.toast(returnMessage, c);


                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Utils.toast(String.valueOf(R.string.error_network), c);
                } finally {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar.setProgress(0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Utils.toast(String.valueOf(R.string.error_network), c);
                Utils.output(responseBody.toString());
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress((int) totalSize);
                super.onProgress(bytesWritten, totalSize);
            }
        });
    }

    //Method sends the data to the server
    private void sendPassword(String newPassword, String oldPassword) {
        //Creates parameter for the post request
        RequestParams params = new RequestParams();
        params.put("NewPassword", newPassword);
        params.put("OldPassword", oldPassword);
        params.put("UserID", Utils.USER_ID);

        HttpService.post(Utils.UPDATE_PASSWORD_ROUTE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                JSONObject res;
                boolean success;
                String returnMessage;

                //Parses JSON message
                try {
                    res = new JSONObject(response).getJSONObject("ChangePassword");
                    success = res.getBoolean("SuccessfulAttempt");
                    returnMessage = res.getString("ReturnMessage");

                    //Checks if its successful which means the json will have other information
                    if (success) {
                        dialog.hide();
                        Utils.toast(returnMessage, c);

                    } else
                        Utils.toast(returnMessage, c);


                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Utils.toast(String.valueOf(R.string.error_network), c);
                } finally {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar.setProgress(0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Utils.toast(String.valueOf(R.string.error_network), c);
                Utils.output(responseBody.toString());
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress((int) totalSize);
                super.onProgress(bytesWritten, totalSize);
            }
        });
    }

//    public void updateUsernameAction(View view){
//        // 1. Instantiate an AlertDialog.Builder with its constructor
//        AlertDialog.Builder builder = new AlertDialog.Builder(c);
//
//        //Creates the visuals for the dialog
//        builder.setTitle("Update Username");
//
//        View mView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
//        builder.setView(mView);
//        //Creates the dialog and displays it
//        dialog = builder.create();
//        dialog.show();
//
//        Button addButton = (Button) mView.findViewById(R.id.button_add_contact);
//        Button cancelButton = (Button) mView.findViewById(R.id.button_cancel_contact);
//        final EditText contactName = (EditText) mView.findViewById(R.id.editText_add_contact);
//        progressBar = (ProgressBar) mView.findViewById(R.id.progressBar_add_contact);
//        progressBar.setVisibility(View.INVISIBLE);
//
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = contactName.getText().toString().trim();
//                if(!name.isEmpty() && name.length() > 3)
//                    addContact(name);
//                else
//                    Utils.toast("Please enter a name",c);
//            }
//        });
//
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                contactName.setText("");
//                dialog.hide();
//            }
//        });
//    }
}
