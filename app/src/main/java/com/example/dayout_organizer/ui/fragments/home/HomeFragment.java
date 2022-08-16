package com.example.dayout_organizer.ui.fragments.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.dayout_organizer.R;
import com.example.dayout_organizer.adapter.recyclers.HomePlaceAdapter;

import com.example.dayout_organizer.helpers.view.FN;
import com.example.dayout_organizer.models.place.PlaceData;
import com.example.dayout_organizer.models.place.PlaceModel;
import com.example.dayout_organizer.room.placeRoom.databases.PlaceDataBase;
import com.example.dayout_organizer.ui.activities.MainActivity;
import com.example.dayout_organizer.ui.dialogs.notify.ErrorDialog;
import com.example.dayout_organizer.ui.dialogs.notify.LoadingDialog;
import com.example.dayout_organizer.ui.fragments.drawer.DrawerFragment;
import com.example.dayout_organizer.viewModels.PlaceViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("NonConstantResourceId")
public class HomeFragment extends Fragment {

    View view;

    @BindView(R.id.home_place_rc)
    RecyclerView homePlaceRc;

    HomePlaceAdapter homePlaceAdapter;

    LoadingDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        initView();
        getDataFromApi();
        return view;
    }

    @Override
    public void onStart() {
        if(FN.getCurrentFragment(requireActivity()) instanceof DrawerFragment) FN.popTopStack(requireActivity());
        ((MainActivity)requireActivity()).showBottomBar();
        ((MainActivity)requireActivity()).showDrawerButton();
        super.onStart();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(requireContext());
        initRc();
    }

    private void initRc(){
        homePlaceRc.setHasFixedSize(true);
        homePlaceRc.setLayoutManager(new LinearLayoutManager(requireContext()));
        homePlaceAdapter = new HomePlaceAdapter(new ArrayList<>(),requireContext());
        homePlaceRc.setAdapter(homePlaceAdapter);
    }

    private void getDataFromApi(){
        loadingDialog.show();
        PlaceViewModel.getINSTANCE().getPopularPlace();
        PlaceViewModel.getINSTANCE().popularPlaceMutableLiveData.observe(requireActivity(),popularPlaceObserver);
    }

    private void getDataFromRoom() {
        PlaceDataBase.getINSTANCE(requireContext())
                .iPlaces()
                .getPlaces()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<PlaceData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull List<PlaceData> data) {
                        homePlaceAdapter.refresh(data);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("getter popular place roomDB", "onError: " + e.toString());
                    }
                });
    }

    private final Observer<Pair<PlaceModel,String>> popularPlaceObserver =  new Observer<Pair<PlaceModel, String>>() {
        @Override
        public void onChanged(Pair<PlaceModel, String> popularPlaceStringPair) {
            loadingDialog.dismiss();
            if (popularPlaceStringPair != null){
                if (popularPlaceStringPair.first != null){
                    ((MainActivity)requireActivity()).deleteAllRoomDB();
                    homePlaceAdapter.refresh(popularPlaceStringPair.first.data);
                }
                else {
                    getDataFromRoom();
                    new ErrorDialog(requireContext(),popularPlaceStringPair.second).show();
                }
            }
            else {
                getDataFromRoom();
                new ErrorDialog(requireContext(),getResources().getString(R.string.error_connection)).show();
            }
        }
    };
}