package com.urexamhelp.ncertpro;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SubjectInterface {

    @GET("subject/")
    Call<ResponseBody> getSubject(@Query(("class_id")) String class_id);
}
