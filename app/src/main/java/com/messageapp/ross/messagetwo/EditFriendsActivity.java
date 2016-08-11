package com.messageapp.ross.messagetwo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ListActivity
{

    public static final  String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected  ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);

       getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }



    @Override
    protected void onResume()
    {
       super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);//orders username numerically
        query.setLimit(1000);//this will set the limit of friends that can be displayed
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {

                if (e == null)
                {
                   //success
                   mUsers = users;
                   String[] username = new String[mUsers.size()];
                   int i = 0;

                        for(ParseUser user : mUsers)
                        {
                            username[i] = user.getUsername();//adds 1 more friend to the users friend list
                            i++;
                        }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, username);

                            setListAdapter(adapter);
                            addFriendCheckmarks();
               }

                        else
                        {
                            Log.e(TAG, e.getMessage());
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                            builder.setMessage(e.getMessage())
                                .setTitle(R.string.error_title)//message alert if a problem occurs with the app
                                .setPositiveButton(android.R.string.ok, null);

                                AlertDialog dialog = builder.create();
                                dialog.show();
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

   @Override
   protected void onListItemClick(ListView l, View v, int position, long id)
   {
       super.onListItemClick(l, v, position, id);
       if (getListView().isItemChecked(position))
       {
          //adds friend to the friend list
           mFriendsRelation.add(mUsers.get(position));
       }

            else
            {
            //removes a friend from the friends list
            mFriendsRelation.remove(mUsers.get(position));
            }


       mCurrentUser.saveInBackground(new SaveCallback() {
           @Override
           public void done(ParseException e) {

               if (e != null)
               {
                   Log.e(TAG, e.getMessage());
               }
           }
       });

    }

    private  void addFriendCheckmarks()  //check marks beside 'friends' names
    {
      mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
          @Override
          public void done(List<ParseUser> friends, ParseException e) {
              if (e==null)
              {
                  //displays green check when friends match
                  for (int i = 0; i < mUsers.size(); i++)
                  {
                    ParseUser user = mUsers.get(i);

                      for (ParseUser friend : friends)
                      {
                          if (friend.getObjectId().equals(user.getObjectId()))
                          {
                              getListView().setItemChecked(i, true);
                          }
                      }
                  }
              }

                    else
                    {
                        Log.e(TAG, e.getMessage());
                    }
          }
      });
    }
}
