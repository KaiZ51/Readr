package pt.ismai.a26800.readr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListNewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // https://newsapi.org/v1/articles?source=techcrunch&apiKey={API_KEY}
        // key = 17f8ddef543c4c81a9df2beb60c2a478

        String url = "https://newsapi.org";

        // Retrofit implementation

        NewsAPI_Interface client = NewsAPI_Adapter.createService(NewsAPI_Interface.class);
        Call<NewsAPI> call = client.getData("techcrunch", "17f8ddef543c4c81a9df2beb60c2a478");

        call.enqueue(new Callback<NewsAPI>() {
            @Override
            public void onResponse(Call<NewsAPI> call, Response<NewsAPI> response) {
                if (response.body() != null) {
                    System.out.println("Status: " + response.body().status + "\n" +
                            "News source: " + response.body().source + "\n" +
                            "Articles object: " + response.body().articles + "\n \n");

                    for (Articles article : response.body().articles) {
                        System.out.println("Title: " + article.title + "\n" +
                                "Description: " + article.description + "\n \n");
                    }

                    ExpandableHeightGridView gv_content = (ExpandableHeightGridView) findViewById(R.id.gv_content);
                    NewsAdapter nAdapter = new NewsAdapter(ListNewsActivity.this,
                            R.layout.article_layout, response.body().articles);
                    gv_content.setAdapter(nAdapter);
                    gv_content.setExpanded(true);
                }
            }

            @Override
            public void onFailure(Call<NewsAPI> call, Throwable t) {
                System.out.println("An error ocurred!\n" +
                        "URL: " + call.request().url() + "\n" +
                        "Cause: " + t.getCause().toString());
            }
        });
    }
}