package com.example.dayout_organizer.room.tripRoom.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dayout_organizer.models.profile.ProfileData;
import com.example.dayout_organizer.models.trip.TripData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.example.dayout_organizer.config.AppConstants.PROFILE_DATA;
import static com.example.dayout_organizer.config.AppConstants.TRIP_DATA;

@Dao
public interface ITrip {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertTripData(TripData tripData);


    @Query("select * from " + TRIP_DATA +" where isUpcoming = 1")
    Single<List<TripData>> getUpComingTripData();

    @Query("select * from " + TRIP_DATA +" where isActive = 1")
    Single<List<TripData>> getActiveTripData();


    @Query("select * from " + TRIP_DATA +" where isUpcoming = 0 AND isActive = 0")
    Single<List<TripData>> getHistoryTripData();


    @Query("select * from " + TRIP_DATA +" where id =:tripId")
    Single<TripData> getTripDataById(int tripId);

    @Query("delete from "+TRIP_DATA)
    Single<Void>   deleteAll();
}
