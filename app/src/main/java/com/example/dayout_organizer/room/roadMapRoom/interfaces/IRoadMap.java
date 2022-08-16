package com.example.dayout_organizer.room.roadMapRoom.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dayout_organizer.config.AppConstants;
import com.example.dayout_organizer.models.profile.ProfileData;
import com.example.dayout_organizer.models.roadMap.RoadMapData;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.example.dayout_organizer.config.AppConstants.PROFILE_DATA;
import static com.example.dayout_organizer.config.AppConstants.ROAD_MAP_DATA;

@Dao
public interface IRoadMap {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertRoadMap(RoadMapData roadMapData);

    @Query("select * from " + ROAD_MAP_DATA)
    Single<RoadMapData> getRoadMap();

    @Query("delete from "+ROAD_MAP_DATA)
    Single<Void>   deleteAll();
}
