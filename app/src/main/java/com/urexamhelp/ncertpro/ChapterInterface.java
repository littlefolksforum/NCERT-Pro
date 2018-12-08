package com.urexamhelp.ncertpro;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ChapterInterface {

    @GET("chapter/")
    Call<ResponseBody> getChapter(@Query(("subject_id")) String subject_id);
}
