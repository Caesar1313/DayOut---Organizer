package com.example.dayout_organizer.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.util.Pair;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;

import com.example.dayout_organizer.R;
import com.example.dayout_organizer.config.AppSharedPreferences;
import com.example.dayout_organizer.helpers.system.PermissionsHelper;
import com.example.dayout_organizer.helpers.system.RealPathUtil;
import com.example.dayout_organizer.helpers.view.ConverterImage;
import com.example.dayout_organizer.helpers.view.FN;
import com.example.dayout_organizer.models.trip.create.CreateTripPhoto;
import com.example.dayout_organizer.room.notificationRoom.INotification;
import com.example.dayout_organizer.room.notificationRoom.databases.NotificationDataBase;
import com.example.dayout_organizer.room.passengersRoom.database.PassengersDataBase;
import com.example.dayout_organizer.room.passengersRoom.interfaces.IPassengers;
import com.example.dayout_organizer.room.placeRoom.interfaces.IPlaces;
import com.example.dayout_organizer.room.placeRoom.databases.PlaceDataBase;
import com.example.dayout_organizer.room.pollRoom.database.PollDataBase;
import com.example.dayout_organizer.room.pollRoom.interfaces.IPoll;
import com.example.dayout_organizer.room.roadMapRoom.databases.RoadMapDatabase;
import com.example.dayout_organizer.room.roadMapRoom.interfaces.IRoadMap;
import com.example.dayout_organizer.room.tripRoom.databases.TripDataBases;
import com.example.dayout_organizer.room.tripRoom.interfaces.ITrip;
import com.example.dayout_organizer.ui.dialogs.notify.LoadingDialog;
import com.example.dayout_organizer.ui.fragments.drawer.DrawerFragment;
import com.example.dayout_organizer.ui.fragments.home.HomeFragment;
import com.example.dayout_organizer.ui.fragments.polls.CreatePollFragment;
import com.example.dayout_organizer.ui.fragments.profile.ProfileFragment;
import com.example.dayout_organizer.ui.fragments.trips.createTrip.CreateImageTripFragment;
import com.example.dayout_organizer.ui.fragments.trips.createTrip.CreateTripFragment;
import com.example.dayout_organizer.ui.fragments.trips.createTrip.CreateTripPlaceFragment;
import com.example.dayout_organizer.ui.fragments.trips.createTrip.CreateTripTypeFragment;
import com.example.dayout_organizer.viewModels.TripViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.example.dayout_organizer.config.AppConstants.MAIN_FRC;
import static com.example.dayout_organizer.config.AppSharedPreferences.CACHE_LAN;
import static com.example.dayout_organizer.config.AppSharedPreferences.GET_ACC_TOKEN;
import static com.example.dayout_organizer.config.AppSharedPreferences.InitSharedPreferences;


public class MainActivity extends AppCompatActivity {


    @BindView(R.id.drawer_btn)
    ImageButton drawerButton;
    @BindView(R.id.profile_btn)
    ImageButton profileButton;
    @BindView(R.id.bottom_bar)
    CardView bottomBar;
    @BindView(R.id.create_trip_btn)
    LinearLayout createTripButton;
    @BindView(R.id.create_poll_btn)
    LinearLayout createPollButton;

    private boolean isDrawerOpen = false;
    public int tripId;

    public IPlaces iPlaces;
    public IPoll iPoll;
    public ITrip iTrip;
    public IRoadMap iRoadMap;
    public IPassengers iPassengers;
    public INotification iNotification;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        initRoomDB();


        InitSharedPreferences(this);

        if (AppSharedPreferences.GET_CACHE_LAN().equals("ar")) changeLanguage("ar", false);
        else if (AppSharedPreferences.GET_CACHE_LAN().equals("en")) changeLanguage("en", false);

        FN.addFixedNameFadeFragment(MAIN_FRC, this, new HomeFragment());

        Log.e("ACC_TOKEN", "onCreate: " + GET_ACC_TOKEN());
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fr_c);
        if (currentFragment instanceof HomeFragment) finish();
        else if (checkCreateTripFragments()) Log.w("MainActivity", "Can't press back button");
        else super.onBackPressed();
    }

    private boolean checkCreateTripFragments() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fr_c);
        if (currentFragment instanceof CreateTripPlaceFragment
                || currentFragment instanceof CreateTripTypeFragment
                || currentFragment instanceof CreateImageTripFragment) {
            if (tripId != 0) {
                loadingDialog = new LoadingDialog(this);
                loadingDialog.show();
                deleteTrip(tripId);
            }
            return true;
        }
        return false;
    }

    private void deleteTrip(int tripId) {
        TripViewModel.getINSTANCE().deleteTrip(tripId);
        TripViewModel.getINSTANCE().successfulMutableLiveData.observe(this, deleteTripObserver);
    }

    private final Observer<Pair<Boolean, String>> deleteTripObserver = new Observer<Pair<Boolean, String>>() {
        @Override
        public void onChanged(Pair<Boolean, String> booleanStringPair) {
            loadingDialog.dismiss();
            if (booleanStringPair != null) {
                if (booleanStringPair.first != null) {
                    FN.replaceFadeFragment(MAIN_FRC, MainActivity.this, new CreateTripFragment());
                }
            }
        }
    };

    private void initRoomDB() {
        iPlaces = PlaceDataBase.getINSTANCE(this).iPlaces();
        iPoll = PollDataBase.getINSTANCE(this).iPoll();
        iTrip = TripDataBases.getINSTANCE(this).iTrip();
        iRoadMap = RoadMapDatabase.getINSTANCE(this).iRoadMap();
        iPassengers = PassengersDataBase.getINSTANCE(this).iPassengers();
        iNotification = NotificationDataBase.getINSTANCE(this).iNotification();
    }

    public void deleteAllRoomDB(){
        iPlaces.deleteAll();
        iPoll.deleteAll();
        iTrip.deleteAll();
        iPassengers.deleteAll();
        iRoadMap.deleteAll();
        iNotification.deleteAll();
    }

    private void initView() {
        createPollButton.setOnClickListener(onCreatePollClicked);
        createTripButton.setOnClickListener(onCreateTripClicked);
        drawerButton.setOnClickListener(onDrawerClicked);
        profileButton.setOnClickListener(onProfileClicked);
    }

    private final View.OnClickListener onCreatePollClicked = v -> {
        FN.addFixedNameFadeFragment(MAIN_FRC, MainActivity.this, new CreatePollFragment());
    };

    private final View.OnClickListener onCreateTripClicked = v -> {
        FN.addFixedNameFadeFragment(MAIN_FRC, MainActivity.this, new CreateTripFragment());
    };

    private final View.OnClickListener onDrawerClicked = v -> {
        FN.addSlideLRFragmentUpFragment(MAIN_FRC, MainActivity.this, new DrawerFragment(), "drawer");
        isDrawerOpen = !isDrawerOpen;
    };

    private final View.OnClickListener onProfileClicked = v -> FN.addFixedNameFadeFragment(MAIN_FRC, MainActivity.this, new ProfileFragment());

    public void showBottomBar() {
        drawerButton.setEnabled(true);
        bottomBar.setVisibility(View.VISIBLE);
        bottomBar.animate().setDuration(400).alpha(1);
    }

    public void hideBottomBar() {
        drawerButton.setEnabled(false);
        bottomBar.animate().setDuration(400).alpha(0);

        new Handler(getMainLooper()).postDelayed(() -> {
            bottomBar.setVisibility(View.GONE);
        }, 450);
    }

    public void showDrawerButton() {
        drawerButton.setVisibility(View.VISIBLE);
        profileButton.setVisibility(View.VISIBLE);
        drawerButton.animate().setDuration(400).alpha(1);
        profileButton.animate().setDuration(400).alpha(1);
    }

    public void hideDrawerButton() {
        drawerButton.animate().setDuration(400).alpha(0);
        profileButton.animate().setDuration(400).alpha(0);
        hideBottomBar();
        new Handler(getMainLooper()).postDelayed(() -> {
            drawerButton.setVisibility(View.GONE);
            profileButton.setVisibility(View.GONE);
        }, 450);
    }

    private void refreshActivity() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(getIntent());
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void changeLanguage(String lang, boolean refresh) {
        Resources resources = this.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = new Locale(lang.toLowerCase());
        Locale.setDefault(locale);
        config.setLocale(locale);
        resources.updateConfiguration(config, displayMetrics);
        if (refresh)
            refreshActivity();
        CACHE_LAN(lang);
    }


}
