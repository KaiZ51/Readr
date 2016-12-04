package pt.ismai.a26800.readr.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import pt.ismai.a26800.readr.R;
import pt.ismai.a26800.readr.activities.MainActivity;
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

public class NotificationService extends IntentService {
    public NotificationService() {
        super("Article Notifications");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
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
                    final List<Articles_Map> articlesList = new ArrayList<>();
                    final Comparator<Articles_Map> byPublishedAtComparator =
                            new Comparator<Articles_Map>() {
                                @Override
                                public int compare(Articles_Map o1, Articles_Map o2) {
                                    if (o1.publishedAt == null) {
                                        return (o2.publishedAt == null) ? 0 : -1;
                                    } else if (o2.publishedAt == null) {
                                        return 1;
                                    }
                                    return o1.publishedAt.compareTo(o2.publishedAt);
                                }
                            };

                    for (final Sources_Content source : response.body().sources) {
                        // Articles endpoint
                        NewsAPI_Interface client = Retrofit_Service.createService(NewsAPI_Interface.class);
                        Call<NewsAPI_Map> call = client.getData(source.id, "17f8ddef543c4c81a9df2beb60c2a478");

                        call.enqueue(new Callback<NewsAPI_Map>() {
                            @Override
                            public void onResponse(Call<NewsAPI_Map> call, Response<NewsAPI_Map> response) {
                                if (response.body() != null) {
                                    articlesList.addAll(response.body().articles);
                                    Collections.sort(articlesList, byPublishedAtComparator);
                                    compareWithDb(articlesList.get(articlesList.size() - 1).publishedAt);
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

    private void compareWithDb(Date apiArticle) {
        ArticlesDbHelper mDbHelper = new ArticlesDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ArticlesContract.ArticleEntry._ID,
                ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE,
                ArticlesContract.ArticleEntry.COLUMN_NAME_DATE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {"My Title"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = ArticlesContract.ArticleEntry.COLUMN_NAME_DATE + " DESC";

        Cursor c = db.query(
                ArticlesContract.ArticleEntry.TABLE_NAME,   // The table to query
                projection,                                 // The columns to return
                null,                                       // The columns for the WHERE clause
                null,                                       // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                sortOrder                                   // The sort order
        );

        c.moveToFirst();

        long itemId = c.getLong(c.getColumnIndexOrThrow(ArticlesContract.ArticleEntry._ID));
        String itemValue = c.getString(c.getColumnIndexOrThrow(ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE));

        Date itemDate = new Date();
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        try {
            itemDate = df.parse(c.getString(c.getColumnIndexOrThrow(ArticlesContract.ArticleEntry.COLUMN_NAME_DATE)));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (itemDate.before(apiArticle) && !itemDate.equals(apiArticle)) {
            System.out.println("yes");

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_general)
                    .setContentTitle("There are new articles available!")
                    .setContentText("Touch this notification to open the app.")
                    .setAutoCancel(true);

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
        } else {
            System.out.println("no");
        }

        c.close();
    }

    // will count how many articles are new since the articles stored in the DB
    private void newArticlesCount() {

    }
}
