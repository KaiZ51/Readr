package com.pedro.readr.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashSet;

import com.pedro.readr.R;
import com.pedro.readr.adapters.NewsAdapter;
import com.pedro.readr.asynctasks.LoadOfflineArticles;
import com.pedro.readr.custom_views.ExpandableHeightGridView;
import com.pedro.readr.newsapi.Articles.Articles_Map;
import com.pedro.readr.newsapi.Articles.NewsAPI_Interface;
import com.pedro.readr.newsapi.Articles.NewsAPI_Map;
import com.pedro.readr.newsapi.Retrofit_Service;
import com.pedro.readr.newsapi.Sources.Sources_Content;
import com.pedro.readr.newsapi.Sources.Sources_Interface;
import com.pedro.readr.newsapi.Sources.Sources_Map;
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

        View headerView = navigationView.getHeaderView(0);
        ImageButton header = (ImageButton) headerView.findViewById(R.id.settings_drawer);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListNewsActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // content shown is based on the category selected on the previous activity
        final String category = getIntent().getStringExtra("category");

        switch (category) {
            case "general":
                setTitle(R.string.cat_general);
                toolbarLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.general_toolbar));
                break;
            case "sport":
                setTitle(R.string.cat_sports);
                toolbarLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.sports_toolbar));
                break;
            case "business":
                setTitle(R.string.cat_business);
                toolbarLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.business_toolbar));
                break;
            case "entertainment":
                setTitle(R.string.cat_entertainment);
                toolbarLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.entertainment_toolbar));
                break;
            case "music":
                setTitle(R.string.cat_music);
                toolbarLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.music_toolbar));
                break;
            case "technology":
                setTitle(R.string.cat_technology);
                toolbarLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.technology_toolbar));
                break;
            case "gaming":
                setTitle(R.string.cat_gaming);
                toolbarLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.gaming_toolbar));
                break;
            case "science-and-nature":
                setTitle(R.string.cat_science_and_nature);
                toolbarLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.science_toolbar));
                break;
        }

        SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipelayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(category);
            }
        });

        loadData(category);
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

    private void loadData(String category) {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipelayout);
        swipeLayout.setRefreshing(true);

        // if internet connection is available
        if (activeNetwork != null && activeNetwork.isConnected()) {
            // Retrofit implementation

            // parameters for Sources endpoint
            String language = "en";

            // Sources endpoint
            Sources_Interface client_sources = Retrofit_Service.createService(Sources_Interface.class);
            Call<Sources_Map> call_sources = client_sources.getData(category, language);

            call_sources.enqueue(new Callback<Sources_Map>() {
                @Override
                public void onResponse(Call<Sources_Map> call_sources, Response<Sources_Map> response) {
                    if (response.body() != null) {
                        final NewsAdapter nAdapter = new NewsAdapter(ListNewsActivity.this,
                                R.layout.article_layout);
                        final HashSet<Articles_Map> articlesSet = new HashSet<>();
                        final ArrayList<Sources_Content> sources = response.body().getSources();

                        for (final Sources_Content source : response.body().getSources()) {
                            // Articles endpoint
                            NewsAPI_Interface client = Retrofit_Service.createService(NewsAPI_Interface.class);
                            Call<NewsAPI_Map> call = client.getData(source.getId(), "17f8ddef543c4c81a9df2beb60c2a478");

                            call.enqueue(new Callback<NewsAPI_Map>() {
                                @Override
                                public void onResponse(Call<NewsAPI_Map> call, Response<NewsAPI_Map> response) {
                                    if (response.body() != null) {
                                        // if source is the last one, add all articles from the HashSet to the NewsAdapter.
                                        if (source.getId().equals(sources.get(sources.size() - 1).getId())) {
                                            articlesSet.addAll(response.body().getArticles());
                                            nAdapter.addAll(articlesSet, source.getCategory());
                                        }
                                        // if not, then just add articles from this source to the HashSet.
                                        else {
                                            articlesSet.addAll(response.body().getArticles());
                                        }
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

                        ExpandableHeightGridView gv_content = (ExpandableHeightGridView) findViewById(R.id.gv_content);
                        gv_content.setAdapter(nAdapter);
                        gv_content.setExpanded(true);
                        swipeLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<Sources_Map> call_sources, Throwable t) {
                    System.out.println("There was a problem with loading source data from the API.");
                }
            });
        }
        // if internet connection is not available
        else {
            new LoadOfflineArticles(this, getWindow().getDecorView().getRootView()).execute(category);
            swipeLayout.setRefreshing(false);
        }
    }
}