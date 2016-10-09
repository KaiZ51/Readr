package pt.ismai.a26800.readr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import retrofit2.Retrofit;

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
        String url = "https://newsapi.org/v1/articles?source=techcrunch&apiKey=f9a74579d1734cde94d00ab567c1f206";

        // Retrofit implementation

        /*Retrofit retrofit = new Retrofit.Builder().baseUrl(url).build();
        NewsAPI_Interface api = retrofit.create(NewsAPI_Interface.class);

        tvTest.setText(api.getData());*/

        // Volley implementation

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        tvTest.setText("Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        tvTest.setText("An error ocurred!");
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}