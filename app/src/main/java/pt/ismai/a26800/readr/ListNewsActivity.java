package pt.ismai.a26800.readr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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
        // key = f9a74579d1734cde94d00ab567c1f206

        final TextView tvTest = (TextView) findViewById(R.id.tvTest);
        String url = "https://newsapi.org";

        // Retrofit implementation

        NewsAPI_Interface client = NewsAPI_Adapter.createService(NewsAPI_Interface.class);
        Call<NewsAPI> call = client.getData("techcrunch", "f9a74579d1734cde94d00ab567c1f206");

        call.enqueue(new Callback<NewsAPI>() {
            @Override
            public void onResponse(Call<NewsAPI> call, Response<NewsAPI> response) {
                if (response.body() != null) {
                    tvTest.setText("Status: " + response.body().status + "\n" +
                            "News source: " + response.body().source + "\n \n");

                    for (Articles article : response.body().articles) {
                        tvTest.append("Title: " + article.title + "\n" +
                                "Description: " + article.description + "\n \n");
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsAPI> call, Throwable t) {
                tvTest.setText("An error ocurred!\n" +
                        "URL: " + call.request().url() + "\n" +
                        "Cause: " + t.getCause().toString());
            }
        });
    }
}