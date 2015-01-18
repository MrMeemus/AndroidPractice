package com.practice.rquan24.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Layout;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by rquan24 on 12/4/14.
 */
public class CrimeListFragment extends ListFragment
{
    private static final String TAG = "CrimeListFragment";
    private boolean mSubtitleVisible;

    private ArrayList<Crime> mCrimes;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mSubtitleVisible = false;

        // getActivity is a convenience method that allows fragment to access hosting activity
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();

        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);
    }

    /*
        Checking after you retained the fragment if the subtitle was shown
        in the orientation before, if so..reset it.
     */
    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, parent, savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            if(mSubtitleVisible)
            {
                getActivity().getActionBar().setSubtitle(R.string.subtitle);
            }
        }

        ListView listView = (ListView) v.findViewById(android.R.id.list);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            registerForContextMenu(listView);
        }
        else
        {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
            {
                @Override
                public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b)
                {

                }

                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
                {
                    MenuInflater inflater = actionMode.getMenuInflater();
                    inflater.inflate(R.menu.crime_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
                {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
                {
                    switch (menuItem.getItemId())
                    {
                        case R.id.menu_item_delete_crime:
                             CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
                             CrimeLab crimeLab = CrimeLab.get(getActivity());
                             for(int i = adapter.getCount(); i >= 0; i--)
                             {
                               if(getListView().isItemChecked(i))
                               {
                                    crimeLab.deleteCrime(adapter.getItem(i));
                               }
                             }

                             actionMode.finish();
                             adapter.notifyDataSetChanged();
                             return true;
                        default:
                             return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode)
                {

                }
            });
        }

        return v;
    }
    /*
        When a crime item in the list gets clicked, we start the
        CrimePagerActivity because we want to be able to swipe the crimes.
        We send an intent to accomplish this. CrimePagerActivity
        from there will call crimeFragment to display data. I could be wrong.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id )
    {
        Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);

        //Start crime pager activity
        Intent i = new Intent(getActivity(),CrimePagerActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        Log.d(TAG, c.getId().toString());
        startActivity(i);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisible && showSubtitle != null)
        {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
                startActivityForResult(i, 0);
                return true;
            case R.id.menu_item_show_subtitle:
                if(getActivity().getActionBar().getSubtitle() == null)
                {
                    getActivity().getActionBar().setSubtitle(R.string.subtitle);
                    item.setTitle(R.string.hide_subtitle);
                    mSubtitleVisible = true;
                }
                else
                {
                    getActivity().getActionBar().setSubtitle(null);
                    item.setTitle(R.string.show_subtitle);
                    mSubtitleVisible = false;
                }
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

     @Override
     public boolean onContextItemSelected(MenuItem item)
     {
         AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
         int position = info.position;
         CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
         Crime crime = adapter.getItem(position);

         switch(item.getItemId())
         {
             case R.id.menu_item_delete_crime:
                    CrimeLab.get(getActivity()).deleteCrime(crime);
                    adapter.notifyDataSetChanged();
                    return true;
         }
         return super.onContextItemSelected(item);
     }
    /*
        Creating a custom adapter by subclassing.
        The getView() function is where you create the custom view so that the
        adapter call communicate that back to the listView
     */
    private class CrimeAdapter extends ArrayAdapter<Crime>
    {
        public CrimeAdapter(ArrayList<Crime> crimes)
        {
            super(getActivity(), 0, crimes);
        }

        // convertView is view recycled for each item in list, instead of creating new one everytime
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
            }

            Crime c = getItem(position);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());

            TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
            dateTextView.setText(c.getDate().toString());

            CheckBox solvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }
    }

}
