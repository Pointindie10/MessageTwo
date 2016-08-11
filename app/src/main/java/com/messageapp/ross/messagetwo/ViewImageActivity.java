package com.messageapp.ross.messagetwo;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;


public class ViewImageActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_view_image);

        ImageView imageView=(ImageView)findViewById((R.id.imageView));

        Uri imageUri=getIntent().getData();
        Picasso.with(this).load(imageUri.toString()).into(imageView);

        Timer time=new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        },15*1000);
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
