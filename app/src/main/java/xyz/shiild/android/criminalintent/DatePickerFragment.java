package xyz.shiild.android.criminalintent;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Stephen Hildebrand
 * @version 7/16/2016
 */
public class DatePickerFragment extends DialogFragment {
    /** Id for the date argument.  */
    private static final String ARG_DATE = "date";
    /** The DatePicker object. */
    private DatePicker mDatePicker;

    /**
     * To get data into the DatePickerFragment, the date is stashed in its arguments bundle
     * where it is able to be accessed. The arguments are created and set in the newInstance()
     * method which replaces the fragment constructor.
     *
     # Adobe Blocker
     127.0.0.1 lmlicenses.wip4.adobe.com
     127.0.0.1 lm.licenses.adobe.com
     127.0.0.1 na1r.services.adobe.com
     127.0.0.1 hlrcv.stage.adobe.com
     127.0.0.1 practivate.adobe.com
     127.0.0.1 activate.adobe.com

     # Malwarebytes Blocker
     0.0.0.0 keystone.mwbsys.com
     *
     * @param date The date of the crime.
     * @return The Fragment now containing the bundle in its arguments.
     */
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Creates the alert dialog. The AlertDialog.Builder class provides a fluent interface for
     * constructing an AlterDialog instance. First pass a Context into the AlertDialog.Builder
     * constructor, which returns an instance of AlertDialog.Builder. Next call the setTitle
     * and setPositiveButton methods to configure the dialog. setPositiveButton accepts a string
     * resource and an object that implements DialogInterface.OnClickListener. This button is
     * what the user should press to select the dialog's primary action.
     *
     * @param savedInstanceState    The previously saved instance
     * @return  The newly created alert dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Initialize the DatePicker using the timestamp info held in the Date.
        Date date = (Date)getArguments().getSerializable(ARG_DATE);
        // Create calendar object using the Date to configure it.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // Retrieve the needed info from the calendar.
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Inflate the view
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        // Initialize the DatePicker using the Date from the arguments and a Calendar.
        mDatePicker = (DatePicker)v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setView(v) // Set the dialog's View.
                .setTitle(R.string.date_picker_title)   // Set the dialog's Title.
                .setPositiveButton(android.R.string.ok, null) // Set the dialog's positive Button.
                .create();  // Create the dialog.


    }
}