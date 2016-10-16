package pt.ismai.a26800.readr;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Sources_Interface {
    @GET("/v1/sources")
    Call<Sources_Map> getData(@Query("category") String category,
                              @Query("language") String language,
                              @Query("country") String country);
}