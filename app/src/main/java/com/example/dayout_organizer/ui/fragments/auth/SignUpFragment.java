package com.example.dayout_organizer.ui.fragments.auth;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.dayout_organizer.R;
import com.example.dayout_organizer.config.AppConstants;
import com.example.dayout_organizer.helpers.system.HttpRequestConverter;
import com.example.dayout_organizer.helpers.system.PermissionsHelper;
import com.example.dayout_organizer.helpers.view.ConverterImage;
import com.example.dayout_organizer.helpers.view.FN;

import com.example.dayout_organizer.models.profile.ProfileUser;
import com.example.dayout_organizer.ui.dialogs.notify.ErrorDialog;
import com.example.dayout_organizer.ui.dialogs.notify.LoadingDialog;
import com.example.dayout_organizer.ui.dialogs.notify.SuccessDialog;
import com.example.dayout_organizer.viewModels.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.example.dayout_organizer.config.AppConstants.AUTH_FRC;

@SuppressLint("NonConstantResourceId")
public class SignUpFragment extends Fragment {

    @BindView(R.id.signup_male)
    RadioButton maleRadioButton;

    @BindView(R.id.signup_female)
    RadioButton femaleRadioButton;

    @BindView(R.id.sign_up_first_name)
    TextInputEditText firstName;

    @BindView(R.id.signup_first_name_textlayout)
    TextInputLayout firstNameTextlayout;

    @BindView(R.id.sign_up_last_name)
    TextInputEditText lastName;

    @BindView(R.id.signup_last_name_textlayout)
    TextInputLayout lastNameTextlayout;

    @BindView(R.id.sign_up_password)
    TextInputEditText password;

    @BindView(R.id.sign_up_password_textlayout)
    TextInputLayout passwordTextlayout;

    @BindView(R.id.sign_up_confirm_password)
    TextInputEditText confirmPassword;

    @BindView(R.id.sign_up_confirm_password_textlayout)
    TextInputLayout confirmPasswordTextlayout;

    @BindView(R.id.sign_up_phone_number)
    TextInputEditText phoneNumber;

    @BindView(R.id.sign_up_phone_number_textlayout)
    TextInputLayout phoneNumberTextlayout;

    @BindView(R.id.sign_up_button)
    Button signUpButton;

    @BindView(R.id.sign_up_to_login)
    TextView signUpToLogin;

    @BindView(R.id.sign_up_radio_group)
    RadioGroup radioGroup;

    @BindView(R.id.signup_upload_image)
    ImageButton upload_image_button;

    @BindView(R.id.sign_up_check_icon)
    ImageButton signUpCheckIcon;

    @BindView(R.id.signup_edit_id_image)
    ImageButton signUpEditIdImage;

    @BindView(R.id.sign_up_email)
    TextInputEditText signUpEmail;

    @BindView(R.id.sign_up_email_textlayout)
    TextInputLayout signUpEmailTextlayout;

    View view;

    Uri credentialPhoto;


    LoadingDialog loadingDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        initViews();

        if (credentialPhoto != null)
            adjustVisibilities();

        return view;
    }

    private void initViews() {
        firstName.addTextChangedListener(firstNameWatcher);
        lastName.addTextChangedListener(lastNameWatcher);
        password.addTextChangedListener(passwordWatcher);
        confirmPassword.addTextChangedListener(passwordConfirmationWatcher);
        phoneNumber.addTextChangedListener(phoneNumberWatcher);
        signUpEmail.addTextChangedListener(emailWatcher);
        signUpToLogin.setOnClickListener(onToLoginClicked);
        signUpButton.setOnClickListener(onSignUpBtnClicked);
        upload_image_button.setOnClickListener(onUploadImageClicked);
        signUpEditIdImage.setOnClickListener(onEditImageClicked);
        loadingDialog = new LoadingDialog(requireContext());
    }

    private void selectImage() {
        if (PermissionsHelper.getREAD_EXTERNAL_STORAGE(requireActivity()))
            launcher.launch("image/*");
    }

    private final ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            credentialPhoto = result;
            if (credentialPhoto != null)
                adjustVisibilities();
        }
    });

    private void adjustVisibilities() {
        upload_image_button.setVisibility(View.GONE);
        signUpEditIdImage.setVisibility(View.VISIBLE);
        signUpCheckIcon.setVisibility(View.VISIBLE);
    }

    private boolean checkInfo() {

        boolean firstNameValidation = isFirstNameValid();
        boolean lastNameValidation = isLastNameValid();
        boolean passwordValidation = isPasswordValid();
        boolean emailValidation = isEmailValid();
        boolean phoneNumberValidation = isPhoneNumberValid();
        boolean idImageValidation = idImageNotEmpty();

        return firstNameValidation && lastNameValidation && passwordValidation && emailValidation && phoneNumberValidation && idImageValidation;
    }

    private RequestBody getRequestBody(String body) {
        return HttpRequestConverter.createStringAsRequestBody("multipart/form-data", body);
    }

    private MultipartBody.Part getPhotoAsRequestBody() {
        String path = ConverterImage.createImageFilePath(requireActivity(), credentialPhoto);
        File file = new File(path);
        RequestBody photoBody = HttpRequestConverter.createFileAsRequestBody("multipart/form-data", file);
        return HttpRequestConverter.createFormDataFile("credential_photo", file.getName(), photoBody);
    }


    private boolean isFirstNameValid() {

//        Matcher firstNameMatcher = AppConstants.EN_NAME_REGEX.matcher(firstName.getText().toString());

        boolean ok = true;


        if (firstName.getText().toString().isEmpty()) {
            firstNameTextlayout.setErrorEnabled(true);
            firstNameTextlayout.setError(getResources().getString(R.string.empty_field));
            ok = false;

        } else if (firstName.getText().toString().charAt(0) == ' ') {
            firstNameTextlayout.setErrorEnabled(true);
            firstNameTextlayout.setError(getResources().getString(R.string.no_space));

            ok = false;

        }
//        else if (!firstNameMatcher.matches()) {
//            firstNameTextlayout.setErrorEnabled(true);
//            firstNameTextlayout.setError(getResources().getString(R.string.name_does_not_match));
//
//            ok = false;
//        }

        return ok;
    }

    private boolean isLastNameValid() {

//        Matcher lastNameMatcher = AppConstants.EN_NAME_REGEX.matcher(lastName.getText().toString());

        boolean ok = true;

        if (lastName.getText().toString().isEmpty()) {
            lastNameTextlayout.setErrorEnabled(true);
            lastNameTextlayout.setError(getResources().getString(R.string.empty_field));

            ok = false;

        } else if (lastName.getText().toString().charAt(0) == ' ') {
            lastNameTextlayout.setErrorEnabled(true);
            lastNameTextlayout.setError(getResources().getString(R.string.no_space));

            ok = false;

        }
//        else if (!lastNameMatcher.matches()) {
//            lastNameTextlayout.setErrorEnabled(true);
//            lastNameTextlayout.setError(getResources().getString(R.string.name_does_not_match));
//
//            ok = false;
//        }

        return ok;
    }

    private boolean isPasswordValid() {

        boolean ok = true;

        if (password.getText().toString().length() < 6) {
            passwordTextlayout.setErrorEnabled(true);
            passwordTextlayout.setError(getResources().getString(R.string.password_limit));

            ok = false;

        } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            confirmPasswordTextlayout.setErrorEnabled(true);
            confirmPasswordTextlayout.setError(getResources().getString(R.string.does_not_match_password));

            ok = false;
        }

        return ok;
    }

    private boolean isEmailValid() {

        Matcher emailMatcher = AppConstants.EMAIL_REGEX.matcher(signUpEmail.getText().toString());

        boolean ok = true;

        if (signUpEmail.getText().toString().isEmpty()) {
            signUpEmailTextlayout.setErrorEnabled(true);
            signUpEmailTextlayout.setError("This Field can not be empty");
            ok = true;
        } else if (!emailMatcher.matches()) {
            signUpEmailTextlayout.setErrorEnabled(true);
            signUpEmailTextlayout.setError(getResources().getString(R.string.not_an_email_address));

            ok = false;
        }
        return ok;
    }

    private boolean isPhoneNumberValid() {

        Matcher phoneNumberMatcher = AppConstants.PHONE_NUMBER_REGEX.matcher(phoneNumber.getText().toString());

        boolean ok = true;

        if (!phoneNumberMatcher.matches()) {
            phoneNumberTextlayout.setErrorEnabled(true);
            phoneNumberTextlayout.setError(getResources().getString(R.string.not_a_phone_number));

            ok = false;
        }

        return ok;
    }

    private boolean idImageNotEmpty() {
        return credentialPhoto != null;
    }

    private final View.OnClickListener onSignUpBtnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (checkInfo()) {
                loadingDialog.show();

                RequestBody requestBody = getRequestBody("Male");
                if (radioGroup.getCheckedRadioButtonId() == maleRadioButton.getId()) {
                    requestBody = getRequestBody("Male");
                } else if (radioGroup.getCheckedRadioButtonId() == femaleRadioButton.getId()) {
                    requestBody = getRequestBody("Female");
                }
                AuthViewModel.getINSTANCE().registerOrganizer(
                        getRequestBody(firstName.getText().toString()),
                        getRequestBody(lastName.getText().toString()),
                        getRequestBody(signUpEmail.getText().toString()),
                        getRequestBody(password.getText().toString()),
                        getRequestBody(phoneNumber.getText().toString()),
                        requestBody, getPhotoAsRequestBody()
                );
                AuthViewModel.getINSTANCE().registerMutableLiveData.observe(requireActivity(), signUpObserver);
            }
        }
    };


    private final Observer<Pair<ProfileUser, String>> signUpObserver = new Observer<Pair<ProfileUser, String>>() {
        @Override
        public void onChanged(Pair<ProfileUser, String> registerModelStringPair) {
            loadingDialog.dismiss();
            if (registerModelStringPair != null) {
                if (registerModelStringPair.first != null) {
                    FN.addFixedNameFadeFragment(AUTH_FRC, requireActivity(), new LoginFragment());
                    new SuccessDialog(requireContext(), getResources().getString(R.string.signup_success_message)).show();
                } else
                    new ErrorDialog(requireContext(), registerModelStringPair.second).show();
            } else
                new ErrorDialog(requireContext(), getResources().getString(R.string.error_connection)).show();
        }
    };

    private final View.OnClickListener onUploadImageClicked = view -> selectImage();

    private final View.OnClickListener onEditImageClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            credentialPhoto = null;
            selectImage();
        }
    };

    private final View.OnClickListener onToLoginClicked = view -> FN.addFixedNameFadeFragment(AUTH_FRC, requireActivity(), new LoginFragment());


    private final TextWatcher firstNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            firstNameTextlayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private final TextWatcher lastNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            lastNameTextlayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private final TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            passwordTextlayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private final TextWatcher passwordConfirmationWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            confirmPasswordTextlayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private final TextWatcher phoneNumberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            phoneNumberTextlayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private final TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            signUpEmailTextlayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}