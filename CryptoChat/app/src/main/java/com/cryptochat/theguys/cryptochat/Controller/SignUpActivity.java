package com.cryptochat.theguys.cryptochat.Controller;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cryptochat.theguys.cryptochat.Model.SignUpModel;
import com.cryptochat.theguys.cryptochat.R;

import java.util.ArrayList;

/**
 * Created by kyle on 3/10/17.
 */

public class SignUpActivity extends AppCompatActivity {

    Context c;
    EditText fName;
    EditText lName;
    EditText emailInput;
    EditText usernameInput;
    EditText passwordInput;
    EditText passwordConfirmInput;
    Button registerBtn;
    SignUpModel signUpModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        c = this;
        fName = (EditText) findViewById(R.id.edit_fName);
        lName = (EditText) findViewById(R.id.edit_lName);
        emailInput = (EditText) findViewById(R.id.edit_email);
        usernameInput = (EditText) findViewById(R.id.edit_username);
        passwordInput = (EditText) findViewById(R.id.edit_password);
        passwordConfirmInput = (EditText) findViewById(R.id.edit_passwordConfirm);
        registerBtn = (Button) findViewById(R.id.button_registers);

        signUpModel = new SignUpModel(c);
    }

    //Handles the register button press
    public void registerAction(View view){
        ArrayList<String> userInformation = new ArrayList<>();

        userInformation.add(fName.getText().toString());
        userInformation.add(lName.getText().toString());
        userInformation.add(emailInput.getText().toString());
        userInformation.add(usernameInput.getText().toString());
        userInformation.add(passwordInput.getText().toString());
        userInformation.add(passwordConfirmInput.getText().toString());

        signUpModel.fillModel(userInformation);

        boolean successfulValidation = signUpModel.validate();

        if(successfulValidation){
            signUpModel.attemptSignUp();
        }

    }

}
