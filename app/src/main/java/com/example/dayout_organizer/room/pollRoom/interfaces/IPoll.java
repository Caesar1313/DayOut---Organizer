package com.example.dayout_organizer.room.pollRoom.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dayout_organizer.models.notification.NotificationData;
import com.example.dayout_organizer.models.poll.PollData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.example.dayout_organizer.config.AppConstants.NOTIFICATION_DATA;
import static com.example.dayout_organizer.config.AppConstants.POLL_DATA;

@Dao
public interface IPoll {
    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    Completable insertPoll(PollData pollData);


    @Query("select * from "+ POLL_DATA)
    Single<List<PollData>> getPollsData();

    @Query("delete from "+POLL_DATA)
    Single<Void>   deleteAll();
}
