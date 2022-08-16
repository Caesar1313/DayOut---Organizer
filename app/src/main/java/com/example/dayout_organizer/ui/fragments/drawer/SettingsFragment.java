package com.example.dayout_organizer.ui.fragments.drawer;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.fragment.app.Fragment;


import com.example.dayout_organizer.R;
import com.example.dayout_organizer.config.AppConstants;
import com.example.dayout_organizer.config.AppSharedPreferences;
import com.example.dayout_organizer.helpers.view.FN;
import com.example.dayout_organizer.helpers.view.NoteMessage;
import com.example.dayout_organizer.ui.activities.AuthActivity;
import com.example.dayout_organizer.ui.activities.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.dayout_organizer.api.ApiClient.BASE_URL;
import static com.example.dayout_organizer.config.AppSharedPreferences.CACHE_BASE_URL;
import static com.example.dayout_organizer.config.AppSharedPreferences.CLEAR_DATA;


public class SettingsFragment extends Fragment {


    View view;
    @BindView(R.id.back_arrow)
    ImageButton backArrow;
    @BindView(R.id.base_url)
    EditText baseUrl;
    @BindView(R.id.confirm_button)
    Button confirmButton;
    @BindView(R.id.language_switch)
    Switch languageSwitch;
    @BindView(R.id.clear_data)
    Button clearDataButton;

    int type;

    public SettingsFragment(int type) {
        this.type = type;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        baseUrl.setText(BASE_URL);
        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLEAR_DATA();
                openAuthActivity();
            }
        });
        confirmButton.setOnClickListener(onConfirmClicked);
        backArrow.setOnClickListener(v -> FN.popTopStack(requireActivity()));
        languageSwitch.setChecked(AppSharedPreferences.GET_CACHE_LAN().equals("en"));
        languageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (type != 1) {
                if (isChecked) {
                    ((MainActivity) requireActivity()).changeLanguage("en", true);
                    //language = "EN";
                } else {
                    ((MainActivity) requireActivity()).changeLanguage("ar", true);
                    //language = "AR";
                }
            }
            else {
                if (isChecked) {
                    ((AuthActivity) requireActivity()).changeLanguage("en", true);
                    //language = "EN";
                } else {
                    ((AuthActivity) requireActivity()).changeLanguage("ar", true);
                    //language = "AR";
                }
            }
        });
    }
    private void openAuthActivity(){
        FN.popAllStack(requireActivity());
        requireActivity().startActivity(new Intent(requireContext(), AuthActivity.class));
        ((MainActivity)requireActivity()).finish();
    }

    private final View.OnClickListener onConfirmClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CACHE_BASE_URL(baseUrl.getText().toString());
            NoteMessage.showSnackBar(requireActivity(),baseUrl.getText().toString());
            FN.popTopStack(requireActivity());
        }
    };


}