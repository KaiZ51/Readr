package pt.ismai.a26800.readr.notifications;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import pt.ismai.a26800.readr.newsapi.Articles.Articles_Map;
import pt.ismai.a26800.readr.newsapi.Articles.NewsAPI_Interface;
import pt.ismai.a26800.readr.newsapi.Articles.NewsAPI_Map;
import pt.ismai.a26800.readr.newsapi.Retrofit_Service;
import pt.ismai.a26800.readr.newsapi.Sources.Sources_Content;
import pt.ismai.a26800.readr.newsapi.Sources.Sources_Interface;
import pt.ismai.a26800.readr.newsapi.Sources.Sources_Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends IntentService {
    public NotificationService() {
        super("Article Notifications");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        System.out.println("fdsfs");

        // parameters for Sources endpoint
        String category = "sport";
        String language = "en";
        String country = "us";

        // Sources endpoint
        Sources_Interface client_sources = Retrofit_Service.createService(Sources_Interface.class);
        Call<Sources_Map> call_sources = client_sources.getData(category, language, country);

        call_sources.enqueue(new Callback<Sources_Map>() {
            @Override
            public void onResponse(Call<Sources_Map> call_sources, Response<Sources_Map> response) {
                if (response.body() != null) {
                    /*final NewsAdapter nAdapter = new NewsAdapter(ListNewsActivity.this,
                            R.layout.article_layout);*/
                    final AbstractArticlesList<Articles_Map> articlesList = new AbstractArticlesList<>();

                    for (final Sources_Content source : response.body().sources) {
                        // Articles endpoint
                        NewsAPI_Interface client = Retrofit_Service.createService(NewsAPI_Interface.class);
                        Call<NewsAPI_Map> call = client.getData(source.id, "17f8ddef543c4c81a9df2beb60c2a478");

                        call.enqueue(new Callback<NewsAPI_Map>() {
                            @Override
                            public void onResponse(Call<NewsAPI_Map> call, Response<NewsAPI_Map> response) {
                                if (response.body() != null) {
                                    /*ExpandableHeightGridView gv_content = (ExpandableHeightGridView) findViewById(R.id.gv_content);
                                    nAdapter.addAll(response.body().articles);

                                    gv_content.setAdapter(nAdapter);
                                    gv_content.setExpanded(true);*/

                                    articlesList.addAll(response.body().articles);
                                    //articlesList.sort();
                                }
                            }

                            @Override
                            public void onFailure(Call<NewsAPI_Map> call, Throwable t) {
                                System.out.println("An error ocurred!\n" +
                                        "URL: " + call.request().url() + "\n" +
                                        "Cause: " + t.getCause().toString());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Sources_Map> call_sources, Throwable t) {
                System.out.println("An error ocurred!");
            }
        });
    }
}
