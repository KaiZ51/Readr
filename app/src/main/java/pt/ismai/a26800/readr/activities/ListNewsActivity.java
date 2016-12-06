package pt.ismai.a26800.readr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import pt.ismai.a26800.readr.custom_views.ExpandableHeightGridView;
import pt.ismai.a26800.readr.newsapi.Retrofit_Service;
import pt.ismai.a26800.readr.newsapi.Articles.NewsAPI_Interface;
import pt.ismai.a26800.readr.newsapi.Articles.NewsAPI_Map;
import pt.ismai.a26800.readr.newsapi.Sources.Sources_Content;
import pt.ismai.a26800.readr.newsapi.Sources.Sources_Interface;
import pt.ismai.a26800.readr.newsapi.Sources.Sources_Map;
import pt.ismai.a26800.readr.adapters.NewsAdapter;
import pt.ismai.a26800.readr.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListNewsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // creates the side navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // content shown is based on the category selected on the previous activity
        String category = getIntent().getStringExtra("category");

        switch (category) {
            case "general":
                setTitle(R.string.cat_general);
                toolbarLayout.setBackground(getDrawable(R.drawable.general_toolbar));
                break;
            case "sport":
                setTitle(R.string.cat_sports);
                toolbarLayout.setBackground(getDrawable(R.drawable.sports_toolbar));
                break;
            case "business":
                setTitle(R.string.cat_business);
                toolbarLayout.setBackground(getDrawable(R.drawable.business_toolbar));
                break;
            case "entertainment":
                setTitle(R.string.cat_entertainment);
                toolbarLayout.setBackground(getDrawable(R.drawable.entertainment_toolbar));
                break;
            case "music":
                setTitle(R.string.cat_music);
                toolbarLayout.setBackground(getDrawable(R.drawable.music_toolbar));

                break;
            case "technology":
                setTitle(R.string.cat_technology);
                toolbarLayout.setBackground(getDrawable(R.drawable.technology_toolbar));
                break;
            case "gaming":
                setTitle(R.string.cat_gaming);
                toolbarLayout.setBackground(getDrawable(R.drawable.gaming_toolbar));
                break;
            case "science-and-nature":
                setTitle(R.string.cat_science_and_nature);
                toolbarLayout.setBackground(getDrawable(R.drawable.science_toolbar));
                break;
        }

        // Retrofit implementation

        // parameters for Sources endpoint
        String language = "en";
        String country = "us";

        // Sources endpoint
        Sources_Interface client_sources = Retrofit_Service.createService(Sources_Interface.class);
        Call<Sources_Map> call_sources = client_sources.getData(category, language, country);

        call_sources.enqueue(new Callback<Sources_Map>() {
            @Override
            public void onResponse(Call<Sources_Map> call_sources, Response<Sources_Map> response) {
                if (response.body() != null) {
                    //System.out.println("Content here: " + response.body().sources);
                    final NewsAdapter nAdapter = new NewsAdapter(ListNewsActivity.this,
                            R.layout.article_layout);

                    for (final Sources_Content source : response.body().sources) {
                        /*System.out.println("ID: " + source.id + "\n" +
                                "Category: " + source.category + "\n" +
                                "Language: " + source.language + "\n" +
                                "Country: " + source.country + "\n" +
                                "Array: " + source.sortBysAvailable + "\n \n");*/

                        // Articles endpoint
                        NewsAPI_Interface client = Retrofit_Service.createService(NewsAPI_Interface.class);
                        Call<NewsAPI_Map> call = client.getData(source.id, "17f8ddef543c4c81a9df2beb60c2a478");

                        call.enqueue(new Callback<NewsAPI_Map>() {
                            @Override
                            public void onResponse(Call<NewsAPI_Map> call, Response<NewsAPI_Map> response) {
                                if (response.body() != null) {
                                    /*System.out.println("Status: " + response.body().status + "\n" +
                                            "News source: " + response.body().source + "\n" +
                                            "Articles_Map object: " + response.body().articles + "\n \n");*/

                                    /*for (Articles_Map article : response.body().articles) {
                                        System.out.println("Title: " + article.title + "\n" +
                                                "Description: " + article.description + "\n" +
                                                "Date: " + article.publishedAt + "\n");
                                    }*/

                                    ExpandableHeightGridView gv_content = (ExpandableHeightGridView) findViewById(R.id.gv_content);
                                    nAdapter.addAll(response.body().articles, source.category);

                                    /*System.out.println("Source ID: " + source.id + "\n" +
                                            "Adapter count: " + nAdapter.getCount() + "\n" +
                                            "Response body: " + response.body().articles + "\n" +
                                            "Articles count: " + nAdapter.getCount() + "\n");*/

                                    /*for (int i = 0; i < nAdapter.getCount(); i++) {
                                        System.out.println("Source ID: " + source.id + "\n" +
                                                "Adapter content: " + nAdapter.getItem(i).publishedAt);
                                    }*/

                                    gv_content.setAdapter(nAdapter);
                                    gv_content.setExpanded(true);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_general) {
            goToCategory("general");
        } else if (id == R.id.nav_sports) {
            goToCategory("sport");
        } else if (id == R.id.nav_business) {
            goToCategory("business");
        } else if (id == R.id.nav_entertainment) {
            goToCategory("entertainment");
        } else if (id == R.id.nav_music) {
            goToCategory("music");
        } else if (id == R.id.nav_technology) {
            goToCategory("technology");
        } else if (id == R.id.nav_gaming) {
            goToCategory("gaming");
        } else if (id == R.id.nav_science_and_nature) {
            goToCategory("science-and-nature");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToCategory(String category) {
        Intent intent = new Intent(ListNewsActivity.this, ListNewsActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}