package com.pedro.readr.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.pedro.readr.R;
import com.pedro.readr.activities.MainActivity;
import com.pedro.readr.newsapi.Articles.Articles_Map;
import com.pedro.readr.newsapi.Articles.NewsAPI_Interface;
import com.pedro.readr.newsapi.Articles.NewsAPI_Map;
import com.pedro.readr.newsapi.Retrofit_Service;
import com.pedro.readr.newsapi.Sources.Sources_Content;
import com.pedro.readr.newsapi.Sources.Sources_Interface;
import com.pedro.readr.newsapi.Sources.Sources_Map;
import com.pedro.readr.sqlite.ArticlesContract;
import com.pedro.readr.sqlite.ArticlesDbHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends IntentService {
    public NotificationService() {
        super("Article Notifications");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String[] cats = workIntent.getStringArrayExtra("cats");

        for (final String category : cats) {
            // parameters for Sources endpoint
            String language = "en";

            // Sources endpoint
            Sources_Interface client_sources = Retrofit_Service.createService(Sources_Interface.class);
            Call<Sources_Map> call_sources = client_sources.getData(category, language);

            call_sources.enqueue(new Callback<Sources_Map>() {
                @Override
                public void onResponse(Call<Sources_Map> call_sources, Response<Sources_Map> response) {
                    if (response.body() != null) {
                        final List<Articles_Map> articlesList = new ArrayList<>();
                        final Comparator<Articles_Map> byPublishedAtComparator =
                                new Comparator<Articles_Map>() {
                                    @Override
                                    public int compare(Articles_Map o1, Articles_Map o2) {
                                        if (o1.getPublishedAt() == null) {
                                            return (o2.getPublishedAt() == null) ? 0 : -1;
                                        } else if (o2.getPublishedAt() == null) {
                                            return 1;
                                        }
                                        return o1.getPublishedAt().compareTo(o2.getPublishedAt());
                                    }
                                };
                        final ArrayList<Sources_Content> sources = response.body().getSources();

                        for (final Sources_Content source : response.body().getSources()) {
                            // Articles endpoint
                            NewsAPI_Interface client = Retrofit_Service.createService(NewsAPI_Interface.class);
                            Call<NewsAPI_Map> call = client.getData(source.getId(), "17f8ddef543c4c81a9df2beb60c2a478");

                            call.enqueue(new Callback<NewsAPI_Map>() {
                                @Override
                                public void onResponse(Call<NewsAPI_Map> call, Response<NewsAPI_Map> response) {
                                    if (response.body() != null) {
                                        // if source is the last one, add the last articles and sort them by date,
                                        // then check if the most recent article from the list is older than the
                                        // newest API article.
                                        if (source.getId().equals(sources.get(sources.size() - 1).getId())) {
                                            articlesList.addAll(response.body().getArticles());
                                            Collections.sort(articlesList, byPublishedAtComparator);
                                            compareWithDb(articlesList.get(articlesList.size() - 1).getPublishedAt(), category);
                                        }
                                        // if not, then just add articles from this source to the list.
                                        else {
                                            articlesList.addAll(response.body().getArticles());
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
                    }
                }

                @Override
                public void onFailure(Call<Sources_Map> call_sources, Throwable t) {
                    System.out.println("An error ocurred!");
                }
            });
        }
    }

    private void compareWithDb(Date apiDate, String category) {
        ArticlesDbHelper mDbHelper = new ArticlesDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ArticlesContract.ArticleEntry._ID,
                ArticlesContract.ArticleEntry.COLUMN_NAME_DATE,
                ArticlesContract.ArticleEntry.COLUMN_NAME_CATEGORY
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = ArticlesContract.ArticleEntry.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {category};

        Cursor c = db.query(
                ArticlesContract.ArticleEntry.TABLE_NAME,   // The table to query
                projection,                                 // The columns to return
                selection,                                  // The columns for the WHERE clause
                selectionArgs,                              // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // The sort order
        );

        if (c.getCount() > 0) {
            List<Date> dateList = new ArrayList<>();
            Date itemDate = new Date();
            DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

            while (c.moveToNext()) {
                try {
                    itemDate = df.parse(c.getString(c.getColumnIndexOrThrow(ArticlesContract.ArticleEntry.COLUMN_NAME_DATE)));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                dateList.add(itemDate);
            }

            Collections.sort(dateList);

            Date lastDate = dateList.get(dateList.size() - 1);

            if (lastDate.before(apiDate) && !lastDate.equals(apiDate)) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_general)
                        .setContentTitle("There are new articles available!")
                        .setContentText("Touch this notification to open the app.")
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 1000})
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this, MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);

                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // mId allows you to update the notification later on.
                mNotificationManager.notify(50, mBuilder.build());
            }
        }

        c.close();
        mDbHelper.close();
    }
}
