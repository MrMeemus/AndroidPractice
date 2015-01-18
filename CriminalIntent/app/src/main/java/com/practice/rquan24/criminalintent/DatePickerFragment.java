package com.practice.rquan24.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by rquan24 on 12/5/14.
 * Dialog Fragment which pops up to allow users to edit the time of the crime
 * committed. It is started from the crime fragment.
 *
 * Crime Fragment sends data to this class through its singleton constructor passing
 * in the previous date info. Before it does that it registers CrimeFragment as the target
 * fragment to Dialog fragment.
 */

public class DatePickerFragment extends DialogFragment
{
    public static final String EXTRA_DATE = "com.practice.rquan24.criminalintent.date";
    private Date mDate;
    private int year, month, day;
    /*
        Pay attention here, BUILDER PATTERN!!!!!
        DP!! Thank you dave.
     */

    /*
        Here I am sending information between two fragments hosted by the same activity
        Calling getTargetFragment() gets the fragment that is waiting on my response or data
        I can access their on Activty result directly and pass in the information they are
        waiting on.
     */
    private void sendResult(int resultCode)
    {
        if(getTargetFragment() == null)
        {
            return;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
        // Create a calendar to get the year, month, and date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        DatePicker datePicker = (DatePicker) v.findViewById(R.id.dialog_datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener()
        {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i2, int i3)
            {
                // translate year, month, day into a Date object using a calendar
                mDate = new GregorianCalendar(i, i2, i3).getTime();
                // Update argument to preserve selected value on rotation
                getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.date_picker_title).setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        sendResult(Activity.RESULT_OK);
                    }
                }).create();
    }

    public static DatePickerFragment newInstance(Date date)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);

        DatePickerFragment dateFragment = new DatePickerFragment();
        dateFragment.setArguments(args);

        return dateFragment;
    }
}
