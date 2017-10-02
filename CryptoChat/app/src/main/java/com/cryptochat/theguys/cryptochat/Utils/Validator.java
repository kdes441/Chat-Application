package com.cryptochat.theguys.cryptochat.Utils;

/**
 * Created by kyle on 3/11/17.
 * This class validates all loginModel input entered into the app
 */

public class Validator {

    public boolean firstNameCheck(String firstName){
        String pattern = "^[a-zA-Z]{4,25}$";
        return firstName.matches(pattern);
    }

    public boolean lastNameCheck(String lastName){
        String pattern = "^[a-zA-Z]{4,25}$";
        return lastName.matches(pattern);
    }

    public boolean emailCheck(String email){
        String pattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        return email.matches(pattern);
    }

    public boolean usernameCheck(String username){
        String pattern = "^[a-zA-Z0-9]{4,25}$";
        return username.matches(pattern);
    }

    public boolean passwordCheck(String password){
        //^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,25}$
        String pattern = "^[a-zA-Z0-9]{6,25}$";
        return password.matches(pattern);
    }
}
