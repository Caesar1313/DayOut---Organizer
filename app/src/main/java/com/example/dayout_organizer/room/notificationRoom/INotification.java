package com.example.dayout_organizer.room.notificationRoom;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.dayout_organizer.models.notification.NotificationData;
import com.example.dayout_organizer.models.place.PlaceData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.example.dayout_organizer.config.AppConstants.NOTIFICATION_DATA;


@Dao
public interface INotification {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertNotification(NotificationData notificationData);


    @Query("select * from "+ NOTIFICATION_DATA)
    Single<List<NotificationData>> getNotifications();

    @Query("delete from "+NOTIFICATION_DATA)
    Single<Void>   deleteAll();
}
