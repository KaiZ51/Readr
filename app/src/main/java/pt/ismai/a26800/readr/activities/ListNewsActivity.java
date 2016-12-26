package pt.ismai.a26800.readr.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pt.ismai.a26800.readr.R;
import pt.ismai.a26800.readr.adapters.NewsAdapter;
import pt.ismai.a26800.readr.custom_views.ExpandableHeightGridView;
import pt.ismai.a26800.readr.newsapi.Articles.Articles_Map;
import pt.ismai.a26800.readr.newsapi.Articles.NewsAPI_Interface;
import pt.ismai.a26800.readr.newsapi.Articles.NewsAPI_Map;
import pt.ismai.a26800.readr.newsapi.Retrofit_Service;
import pt.ismai.a26800.readr.newsapi.Sources.Sources_Content;
import pt.ismai.a26800.readr.newsapi.Sources.Sources_Interface;
import pt.ismai.a26800.readr.newsapi.Sources.Sources_Map;
import pt.ismai.a26800.readr.sqlite.ArticlesContract;
import pt.ismai.a26800.readr.sqlite.ArticlesDbHelper;
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
        String category = getIntent().getStringExtra("category");

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

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        //boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (activeNetwork != null && activeNetwork.isConnected()) {
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

                                        nAdapter.addAll(response.body().articles, source.category);

                                        /*System.out.println("Source ID: " + source.id + "\n" +
                                                "Adapter count: " + nAdapter.getCount() + "\n" +
                                                "Response body: " + response.body().articles + "\n" +
                                                "Articles count: " + nAdapter.getCount() + "\n");*/

                                        /*for (int i = 0; i < nAdapter.getCount(); i++) {
                                            System.out.println("Source ID: " + source.id + "\n" +
                                                    "Adapter content: " + nAdapter.getItem(i).publishedAt);
                                        }*/
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
                    }
                }

                @Override
                public void onFailure(Call<Sources_Map> call_sources, Throwable t) {
                    System.out.println("An error ocurred!");
                }
            });
        } else {
            ArticlesDbHelper mDbHelper = new ArticlesDbHelper(this);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    ArticlesContract.ArticleEntry._ID,
                    ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE,
                    ArticlesContract.ArticleEntry.COLUMN_NAME_DESCRIPTION,
                    ArticlesContract.ArticleEntry.COLUMN_NAME_URL,
                    ArticlesContract.ArticleEntry.COLUMN_NAME_URLTOIMAGE,
                    ArticlesContract.ArticleEntry.COLUMN_NAME_DATE,
                    ArticlesContract.ArticleEntry.COLUMN_NAME_CATEGORY
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = ArticlesContract.ArticleEntry.COLUMN_NAME_CATEGORY + " = ?";
            String[] selectionArgs = {category};

            // How you want the results sorted in the resulting Cursor
            String sortOrder = ArticlesContract.ArticleEntry.COLUMN_NAME_DATE + " DESC";

            Cursor c = db.query(
                    ArticlesContract.ArticleEntry.TABLE_NAME,   // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    selectionArgs,                              // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    sortOrder                                   // The sort order
            );

            if (c.getCount() > 0) {
                NewsAdapter nAdapter = new NewsAdapter(ListNewsActivity.this, R.layout.article_layout);
                List<Articles_Map> articles = new ArrayList<>();

                while (c.moveToNext()) {
                    String title = c.getString(c.getColumnIndexOrThrow(ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE));
                    String description = c.getString(c.getColumnIndexOrThrow(ArticlesContract.ArticleEntry.COLUMN_NAME_DESCRIPTION));
                    String url = c.getString(c.getColumnIndexOrThrow(ArticlesContract.ArticleEntry.COLUMN_NAME_URL));
                    String urlToImage = c.getString(c.getColumnIndexOrThrow(ArticlesContract.ArticleEntry.COLUMN_NAME_URLTOIMAGE));

                    Date date = new Date();
                    DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                    try {
                        date = df.parse(c.getString(c.getColumnIndexOrThrow(ArticlesContract.ArticleEntry.COLUMN_NAME_DATE)));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Articles_Map article = new Articles_Map(title, description, url, urlToImage, date);
                    articles.add(article);
                }

                nAdapter.addAll(articles);

                ExpandableHeightGridView gv_content = (ExpandableHeightGridView) findViewById(R.id.gv_content);
                gv_content.setAdapter(nAdapter);
                gv_content.setExpanded(true);
            } else {
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final Intent i = new Intent(ListNewsActivity.this, MainActivity.class);

                builder.setTitle(R.string.noarticles_title)
                        .setMessage(R.string.noarticles_desc)
                        .setPositiveButton(R.string.noarticles_back, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(i);
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                startActivity(i);
                            }
                        });

                // Create the AlertDialog object and show it
                builder.create();
                builder.show();
            }

            c.close();
        }
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