package com.practice.rquan24.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by rquan24 on 1/8/15.
 */
public class CrimeCameraActivity extends SingleFragmentActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // hide the window tittle
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Hide the status bar and other os level chrom
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }
    @Override
    protected Fragment createFragment()
    {
        return new CrimeCameraFragment();
    }
}
