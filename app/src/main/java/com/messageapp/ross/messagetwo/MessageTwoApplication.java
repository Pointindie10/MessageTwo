package com.messageapp.ross.messagetwo;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Ross on 15/04/2015.
 */
public class MessageTwoApplication extends Application
{
@Override
    public void onCreate()
{
    // Enable Local Datastore.
    super.onCreate();
    Parse.enableLocalDatastore(this);

    Parse.initialize(this, "9MzhwmvBmsmhCjkKwEaWkgn1Z69WXkpj5h9SiYS4", "KzpJfp0KGFJGba6u8GJfutIAWj9y1orZK9m4HsjH");

}
}

