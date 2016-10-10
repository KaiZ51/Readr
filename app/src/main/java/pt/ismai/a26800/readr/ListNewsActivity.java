package pt.ismai.a26800.readr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

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

        // https://newsapi.org/v1/articles?source=google-news&sortBy=top&apiKey=f9a74579d1734cde94d00ab567c1f206
        // https://newsapi.org/v1/sources
        // https://newsapi.org/v1/articles?source=techcrunch&apiKey={API_KEY}
        // key = f9a74579d1734cde94d00ab567c1f206

        final TextView tvTest = (TextView) findViewById(R.id.tvTest);
        String url = "https://newsapi.org";
        String urlVolley = "https://newsapi.org/v1/articles?source=techcrunch&apiKey=f9a74579d1734cde94d00ab567c1f206";

        // Retrofit implementation

        /*Retrofit retrofit = new Retrofit.Builder().baseUrl(url).build();
        NewsAPI_Interface api = retrofit.create(NewsAPI_Interface.class);

        tvTest.setText(api.getData());*/


        // Create a very simple REST adapter which points the GitHub API endpoint.
        NewsAPI_Interface client = NewsAPI_Adapter.createService(NewsAPI_Interface.class);

        // Fetch and print a list of the contributors to this library.
        Call<List<NewsAPI>> call = client.getData("techcrunch", "f9a74579d1734cde94d00ab567c1f206");

        /*try {
            List<NewsAPI> getData = call.execute().body();

            for (NewsAPI contributor : getData) {
                System.out.println(contributor.source + " (" + contributor.status + ")");
                tvTest.setText(contributor.source + " (" + contributor.status + ")");
            }
        } catch (IOException e) {
            // handle errors
            tvTest.setText("An error ocurred!");
        }*/

        call.enqueue(new Callback<List<NewsAPI>>() {
            @Override
            public void onResponse(Call<List<NewsAPI>> call, Response<List<NewsAPI>> response) {
                //List<NewsAPI> myItem=response.body();
                if (response.body() != null) {
                    tvTest.setText(response.body().toString());
                } else {
                    tvTest.setText("It's null, " + response.errorBody().toString() + ", " + call.request().url());
                }
                /*for (NewsAPI contributor : response.body()) {
                    System.out.println(contributor.source + " (" + contributor.status + ")");
                    tvTest.setText(contributor.source + " (" + contributor.status + ")");
                }*/
            }

            @Override
            public void onFailure(Call<List<NewsAPI>> call, Throwable t) {
                //Handle failure
                tvTest.setText("An error ocurred!");
            }
        });


        // Volley implementation

        /*JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        tvTest.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvTest.setText("An error ocurred!");
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);*/
    }
}