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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.postory.R;
import com.example.postory.activities.CreatePostActivity;
import com.example.postory.utils.HourMinuteHandler;
import com.example.postory.utils.TimeController;

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
    private Button confirmButton;
    private View monthOuterLayout;
    private View monthInnerLayout;
    private View dayOuterLayout;
    private View dayInnerLayout;
    private View timeOuterLayout;
    private View timeInnerLayout;
    private SwitchCompat monthSwitch;
    private SwitchCompat daySwitch;
    private SwitchCompat timeSwitch;

    private EditText startYearEditText;
    private EditText endYearEditText;
    private EditText startMonthEditText;
    private EditText endMonthEditText;
    private EditText startDayEditText;
    private EditText endDayEditText;

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
        confirmButton = view.findViewById(R.id.confirmButton);

        monthSwitch = view.findViewById(R.id.monthSwitch);
        daySwitch = view.findViewById(R.id.daySwitch);
        timeSwitch = view.findViewById(R.id.timeSwitch);

        startYearEditText = view.findViewById(R.id.startYear);
        endYearEditText = view.findViewById(R.id.endYear);
        startMonthEditText = view.findViewById(R.id.startMonth);
        endMonthEditText = view.findViewById(R.id.endMonth);
        startDayEditText = view.findViewById(R.id.startDay);
        endDayEditText = view.findViewById(R.id.endDay);

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

        startTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimePickerClick();
            }
        });

        endTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimePickerClick();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmButtonClick();
            }
        });

        return view;
    }
    private void startTimePickerClick(){
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
    private void endTimePickerClick(){
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
    private void confirmButtonClick(){
        TimeController t = null;
        boolean correctInput = false;
        if(timeSwitch.isChecked()){
            try {
                startYear = Integer.parseInt(startYearEditText.getText().toString());
                endYear = Integer.parseInt(endYearEditText.getText().toString());
                startMonth = Integer.parseInt(startMonthEditText.getText().toString());
                endMonth = Integer.parseInt(endMonthEditText.getText().toString());
                startDay = Integer.parseInt(startDayEditText.getText().toString());
                endDay = Integer.parseInt(endDayEditText.getText().toString());
                t= new TimeController(startYear,endYear,startMonth,
                        endMonth,startDay,endDay,startHour,endHour,startMinute,endMinute);
                t.createDate();
                if(t.checkValidity())
                    correctInput = true;
                else{
                    Toast toast = Toast.makeText(getContext(),"The end date cannot be earlier than the start date.",Toast.LENGTH_LONG);
                    toast.show();
                }
            }catch (Exception e){
                Toast toast = Toast.makeText(getContext(),"Fill all fields according to the selected precision level.",Toast.LENGTH_LONG);
                toast.show();
            }

        }
        else if(daySwitch.isChecked()){
            try {
                startYear = Integer.parseInt(startYearEditText.getText().toString());
                endYear = Integer.parseInt(endYearEditText.getText().toString());
                startMonth = Integer.parseInt(startMonthEditText.getText().toString());
                endMonth = Integer.parseInt(endMonthEditText.getText().toString());
                startDay = Integer.parseInt(startDayEditText.getText().toString());
                endDay = Integer.parseInt(endDayEditText.getText().toString());
                t= new TimeController(startYear,endYear,startMonth,
                        endMonth,startDay,endDay);
                t.createDate();
                if(t.checkValidity())
                    correctInput = true;
                else{
                    Toast toast = Toast.makeText(getContext(),"The end date cannot be earlier than the start date.",Toast.LENGTH_LONG);
                    toast.show();
                }
            }catch (Exception e){
                Toast toast = Toast.makeText(getContext(),"Fill all fields according to the selected precision level.",Toast.LENGTH_LONG);
                toast.show();
            }
        }
        else if(monthSwitch.isChecked()){
            try {
                startYear = Integer.parseInt(startYearEditText.getText().toString());
                endYear = Integer.parseInt(endYearEditText.getText().toString());
                startMonth = Integer.parseInt(startMonthEditText.getText().toString());
                endMonth = Integer.parseInt(endMonthEditText.getText().toString());

                t= new TimeController(startYear,endYear,startMonth,
                        endMonth);
                t.createDate();
                if(t.checkValidity())
                    correctInput = true;
                else{
                    Toast toast = Toast.makeText(getContext(),"The end date cannot be earlier than the start date.",Toast.LENGTH_LONG);
                    toast.show();
                }
            }catch (Exception e){
                Toast toast = Toast.makeText(getContext(),"Fill all fields according to the selected precision level.",Toast.LENGTH_LONG);
                toast.show();
            }
        }
        else{
            try {
                startYear = Integer.parseInt(startYearEditText.getText().toString());
                endYear = Integer.parseInt(endYearEditText.getText().toString());

                t= new TimeController(startYear,endYear);
                t.createDate();
                if(t.checkValidity())
                    correctInput = true;
                else{
                    Toast toast = Toast.makeText(getContext(),"The end date cannot be earlier than the start date.",Toast.LENGTH_LONG);
                    toast.show();
                }
            }catch (Exception e){
                Toast toast = Toast.makeText(getContext(),"Fill all fields according to the selected precision level.",Toast.LENGTH_LONG);
                toast.show();
            }

        }
        if (correctInput){
            ((CreatePostActivity)getActivity()).setT(t);
        }

        ((CreatePostActivity)getActivity()).stdLayout.setVisibility(View.VISIBLE);
        ((CreatePostActivity)getActivity()).mapContainer.setVisibility(View.GONE);

        ((CreatePostActivity)getActivity()).getSupportFragmentManager().beginTransaction().remove(TimeChooserFragment.this).commit();

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
            startMonthEditText.setText("");
            endMonthEditText.setText("");
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
            startDayEditText.setText("");
            endDayEditText.setText("");
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