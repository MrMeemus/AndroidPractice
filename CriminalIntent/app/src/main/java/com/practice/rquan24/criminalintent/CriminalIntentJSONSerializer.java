package com.practice.rquan24.criminalintent;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by rquan24 on 12/15/14.
 * Helper class used to serialize and deserialize Crimes into a JSON Format.
 */
public class CriminalIntentJSONSerializer
{
    private Context mContext;
    private String mFilename;

    public CriminalIntentJSONSerializer(Context c, String s)
    {
        mContext = c;
        mFilename = s;
    }

    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException
    {
        JSONArray array = new JSONArray();

        for(Crime c: crimes)
        {
            array.put(c.toJSON());
        }

        Writer writer = null;

        try
        {
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        }
        finally
        {
            if(writer != null)
            {
                writer.close();
            }
        }
    }

    public ArrayList<Crime> loadCrimes() throws JSONException, IOException
    {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;

        try
        {
            // Open the file and read it into string builder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null)
            {
                jsonString.append(line);
            }
            // Parse the JSON using JSON tokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            // build the array of crimes from JSONobjects
            for(int i = 0; i < array.length(); i++)
            {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        }
        catch(FileNotFoundException f)
        {
            //ignore this one it happens when starting fresh
        }
        finally
        {
            if(reader != null)
            {
                reader.close();
            }
        }

        return crimes;
    }
}



















