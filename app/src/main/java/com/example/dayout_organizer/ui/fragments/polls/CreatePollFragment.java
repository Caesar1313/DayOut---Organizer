package com.example.dayout_organizer.ui.fragments.polls;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.dayout_organizer.R;
import com.example.dayout_organizer.helpers.view.FN;
import com.example.dayout_organizer.helpers.view.NoteMessage;
import com.example.dayout_organizer.models.poll.PollChoice;
import com.example.dayout_organizer.models.poll.PollData;
import com.example.dayout_organizer.ui.activities.MainActivity;
import com.example.dayout_organizer.ui.dialogs.notify.ErrorDialog;
import com.example.dayout_organizer.ui.dialogs.notify.LoadingDialog;
import com.example.dayout_organizer.ui.dialogs.notify.WarningDialog;
import com.example.dayout_organizer.ui.fragments.home.HomeFragment;
import com.example.dayout_organizer.viewModels.PollViewModel;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dayout_organizer.config.AppConstants.MAIN_FRC;

@SuppressLint("NonConstantResourceId")
public class CreatePollFragment extends Fragment {

    View view;

    @BindView(R.id.new_poll_discard)
    TextView discard_TV;

    @BindView(R.id.new_poll_description)
    EditText description;

    @BindView(R.id.new_poll_options_layout)
    LinearLayout optionsLayout;

    @BindView(R.id.new_poll_add_option_button)
    Button addOptionButton;

    @BindView(R.id.new_poll_publish_button)
    Button publishButton;

    @BindView(R.id.new_poll_title)
    TextView title;

    @BindView(R.id.pick_poll_end_date)
    ImageButton pickPollEndDate;

    @BindView(R.id.expired_date)
    TextView expiredDate;

    String expiredDateString;
    LoadingDialog loadingDialog;

    ArrayList<PollChoice> options;


    @Override
    public void onStart() {
        ((MainActivity) requireActivity()).hideBottomBar();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_poll, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    private void initViews() {
        loadingDialog = new LoadingDialog(requireContext());
        options = new ArrayList<>();
        discard_TV.setOnClickListener(onDiscardClicked);
        addOptionButton.setOnClickListener(onAddOptionClicked);
        publishButton.setOnClickListener(onPublishClicked);
        pickPollEndDate.setOnClickListener(onPickPollEndDate);
    }

    private void createPoll() {
        PollViewModel.getINSTANCE().createPoll(getNewPollData());
        PollViewModel.getINSTANCE().successfulMutableLiveData.observe(requireActivity(), successfulObserver);
    }

    private final Observer<Pair<Boolean, String>> successfulObserver = new Observer<Pair<Boolean, String>>() {
        @Override
        public void onChanged(Pair<Boolean, String> pollDataStringPair) {
            loadingDialog.dismiss();
            if (pollDataStringPair != null) {
                if (pollDataStringPair.first != null) {
                    NoteMessage.showSnackBar(requireActivity(), getResources().getString(R.string.poll_published));
                    FN.replaceFadeFragment(MAIN_FRC, requireActivity(), new HomeFragment());
                } else
                    new ErrorDialog(requireContext(), pollDataStringPair.second).show();
            } else
                new ErrorDialog(requireContext(), getResources().getString(R.string.error_connection)).show();
        }
    };

    private PollData getNewPollData() {
        PollData poll = new PollData();
        poll.title = title.getText().toString();
        poll.description = description.getText().toString();
        poll.choices = options;
        poll.expire_date = expiredDateString;
        return poll;
    }

    private void removeView(View v) {
        optionsLayout.removeView(v);
    }

    private boolean validPoll() {
        return hasTitle() && hasDescription() && validOptions() && expiredDateString != null && !expiredDateString.isEmpty();
    }

    private boolean hasDescription() {
        if (description.getText().toString().isEmpty()) {
            NoteMessage.showSnackBar(requireActivity(), getResources().getString(R.string.description_is_empty));
            return false;
        }
        return true;
    }

    private boolean hasTitle() {
        if (title.getText().toString().isEmpty()) {
            NoteMessage.showSnackBar(requireActivity(), getResources().getString(R.string.title_is_empty));
            return false;
        }
        return true;
    }

    private boolean validOptions() {
        return !lessThanTwoOptions() && !hasEmptyOption();
    }

    private boolean lessThanTwoOptions() {
        if (optionsLayout.getChildCount() < 2) {
            NoteMessage.showSnackBar(requireActivity(), getResources().getString(R.string.at_least_two));
            return true;
        }
        return false;
    }

    private boolean hasEmptyOption() {
        for (int i = 0; i < optionsLayout.getChildCount(); i++) {
            View view = optionsLayout.getChildAt(i);
            EditText title = (EditText) view.findViewById(R.id.single_option_title);
            if (title.getText().toString().isEmpty()) {
                NoteMessage.showSnackBar(requireActivity(), getResources().getString(R.string.empty_option));
                return true;
            }
        }
        return false;
    }

    private final View.OnClickListener onDiscardClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new WarningDialog(requireContext(), getResources().getString(R.string.deleting_poll), true).show();
        }
    };

    private final View.OnClickListener onAddOptionClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final View optionView = getLayoutInflater().inflate(R.layout.single_poll_option, null, false);

            ImageButton deleteOptionButton = (ImageButton) optionView.findViewById(R.id.single_option_delete_icon);

            deleteOptionButton.setOnClickListener(v1 -> removeView(optionView));

            optionsLayout.addView(optionView);
        }
    };

    private final View.OnClickListener onPublishClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            options.clear();
            if (validPoll()) {
                loadingDialog.show();
                for (int i = 0; i < optionsLayout.getChildCount(); i++) {
                    View optionView = optionsLayout.getChildAt(i);
                    EditText optionTitle = optionView.findViewById(R.id.single_option_title);
                    options.add(new PollChoice(optionTitle.getText().toString()));
                }
                createPoll();
            }
        }
    };

    private final View.OnClickListener onPickPollEndDate = v -> openDateTimePickerDialog();

    private void openDateTimePickerDialog() {

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.MaterialCalendarTheme, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (checkCurrentDate(year, (month + 1), dayOfMonth)) {
                    NoteMessage.showSnackBar(requireActivity(), getCurrentDate() + " " + getResources().getString(R.string.not_valid));
                } else {
                    TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        expiredDate.setText(getCorrectDate(year,(month+1),dayOfMonth)+"  "+getCorrectTime(hourOfDay,minute));
                        expiredDateString = getCorrectDate(year,(month+1),dayOfMonth)+"  "+getCorrectTime(hourOfDay,minute);
                    };

                    TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), R.style.MaterialCalendarTheme, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
                    timePickerDialog.show();
                }
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setText(R.string.ok);
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setText(R.string.cancel);
    }

    private boolean checkCurrentDate(int year, int month, int day) {
        return getCurrentDate().equals(getCorrectDate(year, month, day));
    }

    private String getCurrentDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private String getCorrectTime(int hour, int minute) {
        String _hour, _minute;

        if (hour <= 9) _hour = "0" + hour;
        else _hour = String.valueOf(hour);

        if (minute <= 9) _minute = "0" + minute;
        else _minute = String.valueOf(minute);

        return _hour + ":" + _minute + ":00";
    }

    private String getCorrectDate(int year, int month, int day) {
        String _month, _day;

        if (month < 9) _month = "0" + month;
        else _month = String.valueOf(month);

        if (day < 9) _day = "0" + day;
        else _day = String.valueOf(day);

        return year + "-" + _month + "-" + _day;
    }

}
