package com.messageapp.ross.messagetwo;

/**
 * Created on 20/04/2015.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;


public class SectionsPagerAdapter extends FragmentPagerAdapter
{

    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        mContext = context;

    }

    @Override
    public Fragment getItem(int position)
    {
        // getItem is called to instantiate the fragment for the given page.

        switch (position)
        {
            case 0:
                return new InboxFragment();
            case 1:
                return new FriendsFragment();
        }
        return null;

    }

    @Override
    public int getCount()
    {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        Locale l = Locale.getDefault();
        switch (position)
        {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
        }
            return null;
        }
    }
