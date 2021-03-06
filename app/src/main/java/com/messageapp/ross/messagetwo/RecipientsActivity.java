package com.messageapp.ross.messagetwo;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends ListActivity
{
    public  static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);


        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

    }
    public void onResume()
    {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e){
                if (e == null)
                {

                    mFriends = friends;

                    String[] username = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends)
                    {
                        username[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_checked, username);
                    setListAdapter(adapter);
                }
                    else
                    {
                        Log.e(TAG, e.getMessage());
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                        builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:

            NavUtils.navigateUpFromSameTask(this);
            return true;
            case R.id.action_send:
            ParseObject message = createMessage();

                if (message == null)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.error_selecting_file)
                         .setTitle(R.string.error_selecting_file_title)
                         .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                    else
                        {
                            send(message);
                            finish();

                        }
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        if (l.getCheckedItemCount() > 0)
        {
            mSendMenuItem.setVisible(true);
        }
            else
            {
                mSendMenuItem.setVisible(false);
            }
    }
    protected ParseObject createMessage()
    {
       ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
       message.put(ParseConstants.KEY_SENDER_IDS,ParseUser.getCurrentUser().getObjectId());
       message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
       message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientsIds());
       message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        if (fileBytes == null)
        {
            return null;
        }

            else
            {
                if (mFileType.equals(ParseConstants.TYPE_IMAGE))
            {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String filename = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(filename, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);

            return message;
        }

    }

    protected ArrayList<String>getRecipientsIds()
    {
     ArrayList<String> recipientsIds = new ArrayList<String>();
        for (int i =0; i < getListView().getCount(); i++)
        {
            if (getListView().isItemChecked(i))
            {
             recipientsIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientsIds;
    }


    protected  void send(ParseObject message)
    {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e== null)
                {
                    Toast.makeText(RecipientsActivity.this,R.string.success_message, Toast.LENGTH_LONG).show();
                }

                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                            builder.setMessage(R.string.error_sending_message)
                                .setTitle(R.string.error_selecting_file_title)
                                .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
            }
        });
    }
}