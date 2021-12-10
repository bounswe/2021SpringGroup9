package com.example.postory.fragments;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import com.example.postory.R;
import com.example.postory.utils.HourMinuteHandler;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeChooserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeChooserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    private Button startTimePicker;
    private Button endTimePicker;

    View monthOuterLayout;
    View monthInnerLayout;
    View dayOuterLayout;
    View dayInnerLayout;
    View timeOuterLayout;
    View timeInnerLayout;
    private SwitchCompat monthSwitch;
    private SwitchCompat daySwitch;
    private SwitchCompat timeSwitch;

    private TextView startTimeText;
    private TextView endTimeText;

    int startYear;
    int endYear;

    int startMonth;
    int endMonth;

    int startDay;
    int endDay;

    int startHour;
    int endHour;

    int startMinute;
    int endMinute;

    private Calendar calendar;
    public TimeChooserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeChooserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeChooserFragment newInstance(String param1, String param2) {
        TimeChooserFragment fragment = new TimeChooserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_chooser, container, false);
        monthOuterLayout = view.findViewById(R.id.monthOuterLayout);
        monthInnerLayout = view.findViewById(R.id.monthInnerLayout);
        dayOuterLayout = view.findViewById(R.id.dayOuterLayout);
        dayInnerLayout = view.findViewById(R.id.dayInnerLayout);
        timeOuterLayout = view.findViewById(R.id.timeOuterLayout);
        timeInnerLayout = view.findViewById(R.id.timeInnerLayout);

        monthSwitch = view.findViewById(R.id.monthSwitch);
        daySwitch = view.findViewById(R.id.daySwitch);
        timeSwitch = view.findViewById(R.id.timeSwitch);

        monthSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                monthSwitchController(b);
            }
        });

        daySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                daySwitchController(b);
            }
        });

        timeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                timeSwitchController(b);
            }
        });

        startTimePicker = view.findViewById(R.id.startTimePicker);
        endTimePicker = view.findViewById(R.id.endTimePicker);
        startTimeText = view.findViewById(R.id.startTimeText);
        endTimeText = view.findViewById(R.id.endTimeText);
        calendar = Calendar.getInstance();

        startTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tp1 = new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        startHour = i;
                        startMinute = i1;
                        startTimeText.setText(HourMinuteHandler.combine(i,i1));
                        Log.d("TAGG",HourMinuteHandler.combine(i,i1));
                    }

                }, startHour, startMinute, true);
                tp1.setButton(TimePickerDialog.BUTTON_POSITIVE, "Pick", tp1);
                tp1.setButton(TimePickerDialog.BUTTON_NEGATIVE, "Cancel", tp1);
                tp1.show();
            }
        });

        endTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tp1 = new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        endHour = i;
                        endMinute = i1;
                        endTimeText.setText(HourMinuteHandler.combine(i,i1));
                    }
                }, endHour, endMinute, true);
                tp1.setButton(TimePickerDialog.BUTTON_POSITIVE, "Pick", tp1);
                tp1.setButton(TimePickerDialog.BUTTON_NEGATIVE, "Cancel", tp1);
                tp1.show();
            }
        });

        return view;
    }

    private void monthSwitchController(boolean isChecked){
        if (isChecked){
            monthInnerLayout.setVisibility(View.VISIBLE);
            dayOuterLayout.setVisibility(View.VISIBLE);
            dayInnerLayout.setVisibility(View.INVISIBLE);
        }
        else{
            monthInnerLayout.setVisibility(View.INVISIBLE);
            dayOuterLayout.setVisibility(View.INVISIBLE);
            daySwitch.setChecked(false);
        }


    }

    private void daySwitchController(boolean isChecked){
        if (isChecked){
            dayInnerLayout.setVisibility(View.VISIBLE);
            timeOuterLayout.setVisibility(View.VISIBLE);
        }
        else{
            dayInnerLayout.setVisibility(View.INVISIBLE);
            timeOuterLayout.setVisibility(View.INVISIBLE);
            timeSwitch.setChecked(false);
        }
    }

    private void timeSwitchController(boolean isChecked){
        if (isChecked){
            timeInnerLayout.setVisibility(View.VISIBLE);
            timeOuterLayout.setVisibility(View.VISIBLE);
        }
        else{
            timeInnerLayout.setVisibility(View.INVISIBLE);
        }
    }
}