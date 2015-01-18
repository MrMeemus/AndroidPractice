package com.practice.rquan24.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by rquan24 on 12/4/14.
 */
public class CrimeListActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new CrimeListFragment();
    }
}
