package com.messageapp.ross.messagetwo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.MalformedInputException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener
{
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST=0;
    public static final int TAKE_VIDEO_REQUEST=1;
    public static final int PICK_PHOTO_REQUEST=2;
    public static final int PICK_VIDEO_REQUEST=3;
    public static final int MEDIA_TYPE_IMAGE=4;
    public static final int MEDIA_TYPE_VIDEO=5;

    public static final int FILE_SIZE_LIMIT=1024*1024*10;//File size data is now limited to 10MB.

    protected Uri mMediaUri;



    protected DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                   switch(which)
                   {
                       case 0: //take picture
                           Intent takePhotoIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                           mMediaUri= getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                           if(mMediaUri==null)
                           {
                               // display error
                               Toast.makeText(MainActivity.this,R.string.error_external_storage,Toast.LENGTH_LONG).show();
                           }

                                else
                                {
                                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                    startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                                }
                                break;

                       case 1: // take video
                           Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                           mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                           Toast.makeText(MainActivity.this,R.string.video_size_warrning,Toast.LENGTH_LONG).show();//Data usage alert.

                           if(mMediaUri==null)
                           {
                               // display error
                               Toast.makeText(MainActivity.this,R.string.error_external_storage,Toast.LENGTH_LONG).show();//Data storage alert.
                           }

                               else
                               {
                                   videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                   videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);//Data video limit
                                   videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                   startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                               }
                               break;

                       case 2: //User Selects an Image
                            Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            choosePhotoIntent.setType("image/*");//IMAGE has been selected by the user.
                            startActivityForResult(choosePhotoIntent,PICK_PHOTO_REQUEST);
                            break;

                       case 3: //User Selects a Video
                            Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            chooseVideoIntent.setType("video/*");//VIDEO has been selected by the user.
                            Toast.makeText(MainActivity.this,R.string.video_file_size_warrning,Toast.LENGTH_LONG).show();
                            startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                            break;
                   }
                }
                private Uri getOutputMediaFileUri(int mediaType)
                {

                    if(isExternalStorageAvailable())
                    {
                        // get uri

                        // 1 get external storage directory
                        String appName= MainActivity.this.getString(R.string.app_name);
                        File mediaStorageDir= new File
                        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);

                        //2 creating our sub-directory
                        if(!mediaStorageDir.exists())
                        {

                            if(!mediaStorageDir.mkdir())
                           {
                                Log.e(TAG,"Failed to crate directory");
                                return null;
                           }
                        }

                        //3 creating a file
                        File mediaFile;
                        Date now= new Date();
                        String timestamp= new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(now);//setting the local time and date for the application.

                        String path=mediaStorageDir.getPath()+File.separator;

                        if (mediaType== MEDIA_TYPE_IMAGE)
                        {
                            mediaFile=new File(path+"IMG"+timestamp+".jpg");//
                        }

                            else if (mediaType== MEDIA_TYPE_VIDEO)
                            {
                                mediaFile=new File(path+"VID"+timestamp+".mp4");//
                            }

                                else
                                {
                                    return null;
                                }

                                Log.d(TAG,"fail" + Uri.fromFile(mediaFile)) ;


                                    //4 return file uri
                                    return Uri.fromFile(mediaFile);
                    }
                                    else
                                    {
                                        return null;
                                    }
                }

                private boolean isExternalStorageAvailable()
                {

                    String state= Environment.getExternalStorageState();

                        if(state.equals(Environment.MEDIA_MOUNTED))
                        {
                            return true;
                        }

                            else
                                {
                                    return false;
                                }

                }
            };

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser currentUser = ParseUser.getCurrentUser();

            if (currentUser == null)
            {
                navigateToLogin();
            }

                else
                {
                    Log.i(TAG, currentUser.getUsername());
                }


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.pager);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
                });


    }




    protected void onActivityResult(int requestCode, int resultCode , Intent data)

    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {

            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST)
            {

                if (data == null)
                {
                    Toast.makeText(this, getString(R.string.general_error),Toast.LENGTH_LONG).show();//error message
                }

                    else
                    {
                        mMediaUri = data.getData();
                    }

                    Log.i(TAG, "Media URI" + mMediaUri);

                    if (requestCode == PICK_VIDEO_REQUEST)
                    {
                        //file must be less than 10MB to be able to send
                        int fileSize =0;
                        InputStream inputStream = null;

                        try
                        {
                            inputStream = getContentResolver().openInputStream(mMediaUri);
                            fileSize = inputStream.available();
                        }

                            catch (FileNotFoundException e)
                            {
                                Toast.makeText(this, getString(R.string.error_opening_file),Toast.LENGTH_LONG).show();//file missing prompt, mew file must be selected
                                return;
                            }

                            catch (IOException e)
                            {
                                Toast.makeText(this, getString(R.string.error_opening_file),Toast.LENGTH_LONG).show();//file missing prompt, mew file must be selected
                                return;
                            }

                                finally
                                {
                                    try
                                    {
                                        inputStream.close();//closing the input stream
                                    }

                                       catch (IOException e)
                                        {
                                        // also used for closing the input stream.
                                        }
                            }

                    if (fileSize >= FILE_SIZE_LIMIT)
                    {
                        Toast.makeText(this, getString(R.string.error_file_size_too_large),Toast.LENGTH_LONG).show();//file size alert
                        return;
                    }

                    }

                }

                        else
                        {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(mMediaUri);
                            sendBroadcast(mediaScanIntent);
                        }

                        Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
                        recipientsIntent.setData(mMediaUri);
                        String fileType;

                            if (requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST)
                            {
                                fileType = ParseConstants.TYPE_IMAGE;
                            }

                                else
                                {
                                    fileType = ParseConstants.TYPE_VIDEO;
                                }


                                recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
                                startActivity(recipientsIntent);
        }

                                    else if (resultCode != RESULT_CANCELED)
                                    {
                                        Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();//error message alert
                                    }
    }

    private void navigateToLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflates the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        switch (itemId)
        {

            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;

                case R.id.action_edit_friends:
                    Intent intent = new Intent(this, EditFriendsActivity.class);
                    startActivity(intent);
                    break;

                    case R.id.action_camera:
                        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
                        builder.setItems(R.array.camera_Choices, mDialogListener);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
      mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {}
}



