package com.practice.rquan24.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by rquan24 on 12/3/14.
 * Singleton class which ensures there is only one instance of the class
 */
public class CrimeLab
{
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private static ArrayList<Crime> mCrimes;
    private CriminalIntentJSONSerializer mSerializer;

    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    //populating the list with dummy data
    private CrimeLab(Context context)
    {
        mAppContext = context;
        mSerializer = new CriminalIntentJSONSerializer(context, FILENAME);

       try
       {
           mCrimes = mSerializer.loadCrimes();
       }
       catch(Exception e)
       {
           mCrimes = new ArrayList<Crime>();
           Log.e(TAG, "Error loading crimes: ", e);
       }
    }
    // Convert context to getAppContext because it is global to the application
    public static CrimeLab get(Context context)
    {
        if(sCrimeLab == null)
        {
            sCrimeLab = new CrimeLab(context.getApplicationContext());
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c)
    {
        mCrimes.add(c);
    }

    public void deleteCrime(Crime c)
    {
        mCrimes.remove(c);
    }

    public boolean saveCrimes()
    {
        try
        {
            mSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "Crimes saved");
            return true;
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error saving crimes", e);
            return false;
        }
    }

    public ArrayList<Crime> getCrimes()
    {
        return mCrimes;
    }

    public Crime getCrime(UUID id)
    {
        for(Crime c: mCrimes)
        {
            if(c.getId().equals(id))
            {
                return c;
            }
        }
        return null;
    }
}
