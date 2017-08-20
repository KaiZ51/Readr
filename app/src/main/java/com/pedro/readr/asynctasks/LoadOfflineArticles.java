package com.pedro.readr.asynctasks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.pedro.readr.R;
import com.pedro.readr.activities.MainActivity;
import com.pedro.readr.adapters.NewsAdapter;
import com.pedro.readr.custom_views.ExpandableHeightGridView;
import com.pedro.readr.newsapi.Articles.Articles_Map;
import com.pedro.readr.sqlite.ArticlesContract;
import com.pedro.readr.sqlite.ArticlesDbHelper;

public class LoadOfflineArticles extends AsyncTask<String, Void, NewsAdapter> {
    private final Context context;
    private final View rootView;

    public LoadOfflineArticles(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
    }

    @Override
    protected NewsAdapter doInBackground(String... category) {
        ArticlesDbHelper mDbHelper = new ArticlesDbHelper(context);
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
        String[] selectionArgs = {category[0]};

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
            NewsAdapter nAdapter = new NewsAdapter(context, R.layout.article_layout);
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

            c.close();
            mDbHelper.close();
            return nAdapter;
        } else {
            c.close();
            mDbHelper.close();

            return null;
        }
    }

    @Override
    protected void onPostExecute(NewsAdapter nAdapter) {
        if (nAdapter != null) {
            ExpandableHeightGridView gv_content = (ExpandableHeightGridView) rootView.findViewById(R.id.gv_content);
            gv_content.setAdapter(nAdapter);
            gv_content.setExpanded(true);

            SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipelayout);
            swipeLayout.setRefreshing(false);
        } else {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final Intent i = new Intent(context, MainActivity.class);

            builder.setTitle(R.string.noarticles_title)
                    .setMessage(R.string.noarticles_desc)
                    .setPositiveButton(R.string.noarticles_back, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            context.startActivity(i);
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            context.startActivity(i);
                        }
                    });

            // Create the AlertDialog object and show it
            builder.create();
            builder.show();
        }
    }
}