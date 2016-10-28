package pt.ismai.a26800.readr.newsapi.Articles;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsAPI_Interface {
    @GET("/v1/articles")
    Call<NewsAPI_Map> getData(@Query("source") String source, @Query("apiKey") String api_key);
}