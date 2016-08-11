package com.messageapp.ross.messagetwo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * Created on 20/04/2015.
 */
public class InboxFragment extends ListFragment
{

    protected List<ParseObject> mMessages;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)

    {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
            return rootView;
    }
    @Override

    public void onResume()
    {
        super.onResume();


        ParseQuery<ParseObject> query = new ParseQuery(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                if (e == null)
                {

                    mMessages = messages;

                    String[] username = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages)

                    {
                        username[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }

                        if(getListView().getAdapter()== null)

                        {
                            MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                            setListAdapter(adapter);
                        }

                            else
                                {
                                    ((MessageAdapter)getListView().getAdapter()).refill(mMessages);//refilling the adapter
                                }

                }

            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {

        super.onListItemClick(l, v, position, id);

            ParseObject message=mMessages.get(position);
                String messageType=message.getString(ParseConstants.KEY_FILE_TYPE);
                    ParseFile file=message.getParseFile((ParseConstants.KEY_FILE));
                        Uri fileUri=Uri.parse(file.getUrl());

                        if(messageType.equals(ParseConstants.TYPE_IMAGE))

                        {
                            //viewing the image
                            Intent intent=new Intent(getActivity(),ViewImageActivity.class);
                            intent.setData(fileUri);
                            startActivity(intent);

                        }

                            else

                            {
                                // viewing a video
                                Intent intent=new Intent(Intent.ACTION_VIEW,fileUri);
                                intent.setDataAndType(fileUri,"video/*");//specific to the video feature
                                startActivity(intent);
                            }

                            // Deleting the Message
                            List<String> ids= message.getList(ParseConstants.KEY_RECIPIENT_IDS);

                                if(ids.size()==1)
                                {
                                    //last recipient can delete message
                                    message.deleteInBackground();
                                }

                                    else

                                    {
                                        // remove recipient from the friends list
                                        ids.remove(ParseUser.getCurrentUser().getObjectId());
                                        ArrayList<String>idsToRemove= new ArrayList<String>();
                                        idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

                                        message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
                                        message.saveInBackground();
                                    }
    }
}
