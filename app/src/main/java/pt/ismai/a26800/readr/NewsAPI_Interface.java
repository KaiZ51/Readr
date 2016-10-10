package pt.ismai.a26800.readr;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsAPI_Interface {
    // https://newsapi.org/v1/articles?source=google-news&sortBy=top&apiKey=f9a74579d1734cde94d00ab567c1f206
    // https://newsapi.org/v1/sources
    // https://newsapi.org/v1/articles?source=techcrunch&apiKey={API_KEY}
    // key = f9a74579d1734cde94d00ab567c1f206
    // https://newsapi.org/v1/articles?source=techcrunch&apiKey=f9a74579d1734cde94d00ab567c1f206

    /*@GET("/2.2/questions?order=desc&sort=creation&site=stackoverflow")
    Call<NewsAPI> loadQuestions(@Query("tagged") String tags);*/

    /*@GET("v1/sources")
    Call<NewsAPI> getData(@Path("id") String id,
                          @Path("name") String name,
                          @Path("description") String description,
                          @Path("language") String language);*/

    /*@GET("/v1/articles?source=techcrunch")
    Call<List<NewsAPI>> getData(@Query("api_Key") String api_key);*/

    @GET("/v1/articles")
    Call<List<NewsAPI>> getData(@Query("source") String source, @Query("api_Key") String api_key);
}