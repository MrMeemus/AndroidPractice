package com.practice.rquan24.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by rquan24 on 12/5/14.
 */
public class CrimePagerActivity extends FragmentActivity
{
    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /*
            creating a view in code rather than XML.
         */
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mCrimes = CrimeLab.get(this).getCrimes();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm)
        {
            /*
                The adapter manages convo between the items and the
                view pager. It needs to use the fragment manager
                because it has to add the fragment to your activity,
                since FM has access to activities and fragments

                FM was always used to add fragments to activities with corresponding views
                so think of it like that homie.

             */
            @Override
            public Fragment getItem(int i)
            {
                Crime c = mCrimes.get(i);
                return CrimeFragment.newInstance(c.getId());
            }

            @Override
            public int getCount()
            {
                return mCrimes.size();
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                Crime crime = mCrimes.get(position);
                if(crime.getTitle() != null)
                {
                    setTitle(crime.getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        /*
            By default viewPager always displays the first item in the list. In order
            to counter this and make it more appropriate to the item that was clicked,
            We have to loop through and set the view pagers current item to be the clicked item.

            The id of the crime was added by the CrimeListFragment as an extra. We can retrieve
            it statically.
         */

        UUID crime_id = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        for(int i = 0; i < mCrimes.size(); i++)
        {
            Crime current = mCrimes.get(i);
            if(current.getId().equals(crime_id))
            {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
