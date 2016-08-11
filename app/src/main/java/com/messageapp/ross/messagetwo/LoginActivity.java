package com.messageapp.ross.messagetwo;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;


public class LoginActivity extends ActionBarActivity
{
    protected EditText mUsername;//member variable
    protected EditText mPassword;//member variable
    protected RippleView mLoginButton;//member variable
    protected TextView mSignUpTextView;//member variable
    protected MediaPlayer mp;//member variable

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

            mp=MediaPlayer.create(LoginActivity.this,R.raw.office_theme);//Music plays on the log on screen
            mp.setLooping(false);
            mp.start();

                mSignUpTextView =(TextView)findViewById(R.id.buttonReg);
                mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//on click listener

                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);//reference activity to get context
                    startActivity(intent);
                    mp.stop();
         }
        });
                        mUsername = (EditText)findViewById(R.id.userHint);//run Username against database
                        mPassword = (EditText)findViewById(R.id.passwordHint);//run password against database
                        mLoginButton =(RippleView)findViewById(R.id.buttonLog);
                        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                            String username = mUsername.getText().toString();//getting the username
                            String password = mPassword.getText().toString();//getting the password

                                username = username.trim();//removes unwanted spaces in the username
                                password = password.trim();//removes unwanted spaces in the password


                if (username.isEmpty()|| password.isEmpty())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message)//reminder message for users
                            .setTitle(R.string.login_error_title)//error message for user
                            .setPositiveButton(android.R.string.ok, null);

                                AlertDialog dialog = builder.create();
                                dialog.show();
                }

                else
                {

                    mp.stop();//ends music after login

                    ParseUser.logInInBackground(username,password,new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {

                            if (e==null)
                            {
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        startActivity(intent);
                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage())
                                    .setTitle(R.string.login_error_title)//alert prompt
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

        //Id if statement for users
        if (id == R.id.action_settings)
        {
            return true;
        }

                return super.onOptionsItemSelected(item);
    }

}

