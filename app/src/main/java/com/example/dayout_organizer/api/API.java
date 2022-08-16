package com.example.dayout_organizer.api;

import androidx.cardview.widget.CardView;

import com.example.dayout_organizer.models.PhotoBase64;
import com.example.dayout_organizer.models.notification.NotificationModel;
import com.example.dayout_organizer.models.passenger.CheckPassengerModel;
import com.example.dayout_organizer.models.passenger.PassengerModel;
import com.example.dayout_organizer.models.poll.PollData;
import com.example.dayout_organizer.models.profile.ProfileUser;
import com.example.dayout_organizer.models.place.PlaceDetailsModel;
import com.example.dayout_organizer.models.roadMap.RoadMapModel;
import com.example.dayout_organizer.models.poll.PollPaginationModel;
import com.example.dayout_organizer.models.place.PlacePaginationModel;
import com.example.dayout_organizer.models.authModels.LoginModel;
import com.example.dayout_organizer.models.place.PlaceModel;

import com.example.dayout_organizer.models.profile.ProfileModel;
import com.example.dayout_organizer.models.trip.TripDetailsModel;
import com.example.dayout_organizer.models.trip.TripPaginationModel;
import com.example.dayout_organizer.models.trip.photo.TripPhotoModel;
import com.example.dayout_organizer.models.tripType.TripTypeModel;
import com.example.dayout_organizer.models.trip.create.CreateTripPhoto;
import com.example.dayout_organizer.models.trip.create.CreateTripPlace;
import com.example.dayout_organizer.models.trip.create.CreateTripType;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {


    /**
     * Get Request
     */

    @GET("api/place/popular/{id}")
    Call<PlaceModel> getPopularPlace(@Path("id") int id);

    @GET("api/organizer/profile/{id}")
    Call<ProfileModel> getOrganizerProfile(@Path("id") int id);

    @GET("api/place")
    Call<PlacePaginationModel> getPlaces(@Query("page") int page);

    @GET("api/trip/types")
    Call<TripTypeModel> getTripType();

    @GET("api/trip/{id}/details/organizer")
    Call<TripDetailsModel> getTripDetails(@Path("id") int id);

    @GET("api/notifications")
    Call<NotificationModel> getNotifications();

    @GET("api/trip/photo/{id}/base64")
    Call<PhotoBase64> getTripPhotoAsBase64(@Path("id") int id);

    @GET("api/bookings/trip/{id}")
    Call<PassengerModel> getBookingPassengersInTrip(@Path("id") int tripId);

    @GET("api/bookings/trip/{id}/passengers")
    Call<PassengerModel> getAllPassengersInTrip(@Path("id") int tripId);

    @GET("api/polls/organizer")
    Call<PollPaginationModel> getOrganizerPolls(@Query("page") int page);

    @GET("api/place/details/{id}")
    Call<PlaceDetailsModel> getPlaceDetails(@Path("id") int id);

    @GET("api/trip/road-map/{id}")
    Call<RoadMapModel> getRoadMap(@Path("id") int tripId);

    @GET("api/trip/{tripId}/photos")
    Call<TripPhotoModel> getTripPhotos(@Path("tripId") int tripId);


    /**
     * Post Request
     */
    @POST("api/user/login")
    Call<LoginModel> login(@Body JsonObject loginReqBody);


    @Multipart
    @POST("api/user/promotion/request")
    Call<ResponseBody> promotionRequest(@Part("phone_number") RequestBody phoneNumber,
                                        @Part("password") RequestBody password,
                                        @Part("description") RequestBody description,
                                        @Part MultipartBody.Part photo);

//    @POST("api/user/organizer/register")
//    Call<ProfileUser> registerOrganizer(@Body JsonObject profile);

    @Multipart
    @POST("api/user/organizer/register")
    Call<ProfileUser> registerOrganizer(@Part("first_name") RequestBody firstName,
                                         @Part("last_name") RequestBody lastName,
                                         @Part("email") RequestBody email,
                                         @Part("password") RequestBody password,
                                         @Part("phone_number") RequestBody phoneNumber,
                                         @Part("gender") RequestBody gender,
                                         @Part MultipartBody.Part photo);

    @POST("api/trip/create")
    Call<TripDetailsModel> createTrip(@Body JsonObject createTrip);


    @Multipart
    @POST("api/trip/create/add/photos")
    Call<TripDetailsModel> createTripPhoto(@Part("trip_id") RequestBody tripId,
                                           @Part MultipartBody.Part[] photos);

    @POST("api/trip/create/add/places")
    Call<TripDetailsModel> createTripPlace(@Body CreateTripPlace createTripPlace);

    @POST("api/trip/create/add/types")
    Call<TripDetailsModel> createTripType(@Body CreateTripType createTripType);


    @Multipart
    @POST("api/organizer/profile/edit")
    Call<ResponseBody> editProfile(@Part("_method") RequestBody methodName,
                                   @Part("first_name") RequestBody firstName,
                                   @Part("last_name") RequestBody lastName,
                                   @Part("bio") RequestBody bio,
                                   @Part("email") RequestBody email,
                                   @Part MultipartBody.Part photo);


    @POST("api/polls/create")
    Call<ResponseBody> createPoll(@Body PollData poll);

    @POST("api/trip/checkout")
    Call<ResponseBody> checkPassengers(@Body CheckPassengerModel model);

    @POST("api/user/report")
    Call<ResponseBody> reportUser(@Body JsonObject object);

    @POST("api/place/suggest")
    Call<ResponseBody> suggestPlace(@Body JsonObject place);

    @POST("api/trip/active/organizer")
    Call<TripPaginationModel> getActiveTrips(@Body JsonObject filterModel, @Query("page") int page);

    @POST("api/trip/upcoming/organizer")
    Call<TripPaginationModel> getUpcomingTrips(@Body JsonObject filterModel, @Query("page") int page);

    @POST("api/trip/history/organizer")
    Call<TripPaginationModel> getHistoryTrips(@Body JsonObject filterModel, @Query("page") int page);

    @POST("api/user/password/request")
    Call<ResponseBody> checkPhoneNumberExist(@Body JsonObject phoneNumber);

    @POST("api/user/password/reset")
    Call<ResponseBody> resetPassword(@Body JsonObject resetPassword);


    /**
     * Put Request
     */

    @PUT("api/trip/edit")
    Call<TripDetailsModel> editTrip(@Body JsonObject createTrip);


    @Multipart
    @POST("api/trip/edit/photos")
    Call<TripDetailsModel> editTripPhotos(@Part("trip_id") RequestBody tripId,
                                          @Part("_method") RequestBody methodName, // it must be PUT
                                          @Part MultipartBody.Part[] photos,
                                          @Part MultipartBody.Part[] ids);

    @PUT("api/trip/edit/places")
    Call<TripDetailsModel> editTripPlaces(@Body CreateTripPlace createTripPlace);

    @PUT("api/trip/edit/types/{id}")
    Call<TripDetailsModel> editTripTypes(@Path("id") int id, @Body CreateTripType createTripType);

    @PUT("api/trip/{id}/begin")
    Call<ResponseBody> beginTrip(@Path("id") int id);

    @PUT("api/trip/{id}/end")
    Call<ResponseBody> endTrip(@Path("id") int id);

    @PUT("api/trip/place-status/update/{tripId}/{placeId}")
    Call<ResponseBody> visitPlace(@Path("tripId") int tripId, @Path("placeId") int placeId);

    @PUT("api/bookings/customer/{customer_id}/trip/{trip_id}/confirm")
    Call<ResponseBody> confirmPassengerBooking(@Path("customer_id") int customerId, @Path("trip_id") int tripId);

    @PUT("api/bookings/{id}/organizer/cancel")
    Call<ResponseBody> cancelPassengerBooking(@Path("id") int id);


    /**
     * Delete Request
     */

    @DELETE("api/trip/{id}/delete")
    Call<ResponseBody> deleteTrip(@Path("id") int tripId);

    @DELETE("api/organizer/profile/delete/photo")
    Call<ResponseBody> deleteProfilePhoto();

    @DELETE("api/polls/delete/{id}")
    Call<ResponseBody> deletePolls(@Path("id")int pollId);
}
