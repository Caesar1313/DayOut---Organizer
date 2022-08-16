package com.example.dayout_organizer.room.placeRoom.interfaces;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dayout_organizer.models.place.PlaceData;

import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Single;

import static com.example.dayout_organizer.config.AppConstants.NOTIFICATION_DATA;
import static com.example.dayout_organizer.config.AppConstants.PLACE_DATA;


@Dao
public interface IPlaces {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPlace(PlaceData popularPlaceDB);


    @Query("select * from "+ PLACE_DATA)
    Single<List<PlaceData>> getPlaces();


    @Query("select * from "+ PLACE_DATA+" where id =:placeId")
    Single<PlaceData> getPlaceById(int placeId);


    @Query("delete from "+PLACE_DATA)
    Single<Void>  deleteAll();
}
