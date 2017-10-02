package com.cryptochat.theguys.cryptochat.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.cryptochat.theguys.cryptochat.Model.LoginModel;
import com.cryptochat.theguys.cryptochat.R;


/**
 * Created by kyle on 3/10/17.
 */


public class LoginActivity extends AppCompatActivity {

    private Context c;
    private EditText usernameInput;
    private EditText passwordInput;
    private LoginModel loginModel;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        c = this;
        usernameInput = (EditText) findViewById(R.id.edit_username);
        passwordInput = (EditText) findViewById(R.id.edit_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_login);
        progressBar.setVisibility(View.INVISIBLE);
    }

    //Sends user to registration page
    public void registerAction(View view) {
        Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

    //Tries to log user in
    public void loginAction(View view){
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        loginModel = new LoginModel(username,password,c,progressBar);
        boolean successfulValidation= loginModel.validate();

        if(successfulValidation){
            loginModel.attemptLogin();
        }
    }
}
