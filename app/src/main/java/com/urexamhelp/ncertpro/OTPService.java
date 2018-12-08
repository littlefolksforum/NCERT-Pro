package com.urexamhelp.ncertpro;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface OTPService {

    String LOCATION_SEARCH_URL = Constants.LOCATION_SEARCH_URL;
    String FEEDBACK_URL = Constants.FEEDBACK_URL;

    @FormUrlEncoded
    @POST("user_login/")
    Call<ResponseBody> SendPhoneNo(@Field("phone_number") String phone_number);

    @GET("profile/")
    Call<ResponseBody> profile(@Query("phone_number") String phone_number);

    @FormUrlEncoded
    @POST("update_profile/")
    Call<ResponseBody> UpdateUser(@Field("name") String name, @Field("phone_number") String phone_number, @Field("class_id") String class_id);

    @GET("place/autocomplete/json")
    Call<ResponseBody> getPlaces(@Query("key") String key, @Query("type") String type, @Query("input") String input, @Query("components") String components);

    @FormUrlEncoded
    @POST("register_user/")
    Call<ResponseBody> RegisterUser(@Field("name") String name, @Field("phone_number") String phone_number, @Field("class_id") String class_id, @Field("address") String address, @Field("address_id") String address_id, @Field("from_app") int from_app);

    @GET("questions/")
    Call<ResponseBody> getAllQuesionsResponseBody(@Query("chapter_id") String chapter_id, @Query("page")  int page_num);

    @GET("send_feedback")
    Call<ResponseBody> send_feedback(@Query("title") String title, @Query("text") String text, @Query("phone_num") String phone_num);
}
