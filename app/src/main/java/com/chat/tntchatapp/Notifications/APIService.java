package com.chat.tntchatapp.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
   @Headers({
           "Content-Type: application/json",
           "Authorisation:key=AAAAEIjgeHc:APA91bGdji_U6B6aJgWUM3xSGkGMceOMOhC380bDnCAe3Zoz-NOmTz8A0pzuS5_9n6F99WRJfuPJ0fAg8qq_apfRPyx3m-4Ixd21RgubSj0Q2Y-h6OQ5Lla-E6ELv527eZ3uV8hydg8u"
        }
   )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
