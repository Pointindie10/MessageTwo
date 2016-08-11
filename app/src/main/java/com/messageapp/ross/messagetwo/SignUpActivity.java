package com.messageapp.ross.messagetwo;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.Window;
import android.widget.EditText;

import com.andexert.library.RippleView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpActivity extends ActionBarActivity
{

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected RippleView mSignUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText)findViewById(R.id.userName);
        mPassword = (EditText)findViewById(R.id.password);
        mEmail= (EditText)findViewById(R.id.email);
        mSignUpButton =(RippleView)findViewById(R.id.buttonReg);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                username = username.trim();
                password = password.trim();
                email = email.trim();

                if (username.isEmpty()|| password.isEmpty() || email.isEmpty())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)//error message
                      .setTitle(R.string.sign_up_error_title)
                      .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                    else
                {
                    //create the new user

                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null)
                            {
                               //success
                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                                    else
                            {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                        builder.setMessage(e.getMessage())
                                            .setTitle(R.string.sign_up_error_title)
                                            .setPositiveButton(android.R.string.ok, null);

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                            }

                        }
                    });

                }

            }

        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}