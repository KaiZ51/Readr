package pt.ismai.a26800.readr.asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.List;

import pt.ismai.a26800.readr.newsapi.Articles.Articles_Map;
import pt.ismai.a26800.readr.sqlite.ArticlesContract;
import pt.ismai.a26800.readr.sqlite.ArticlesDbHelper;

public class InsertNewArticles extends AsyncTask<Void, Void, Void> {
    private Context context;
    private List<Articles_Map> listArticles;
    private String category;

    public InsertNewArticles(Context context, List<Articles_Map> listArticles, String category) {
        this.context = context;
        this.listArticles = listArticles;
        this.category = category;
    }

    @Override
    protected Void doInBackground(Void... article) {
        ArticlesDbHelper mDbHelper = new ArticlesDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (Articles_Map a : listArticles) {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    ArticlesContract.ArticleEntry._ID,
                    ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE,
                    ArticlesContract.ArticleEntry.COLUMN_NAME_DATE,
                    ArticlesContract.ArticleEntry.COLUMN_NAME_CATEGORY
            };

            // Filter results WHERE "title" = 'My Title'
            String selection = ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE + " = ? " +
                    "AND " + ArticlesContract.ArticleEntry.COLUMN_NAME_DATE + " = ? " +
                    "AND " + ArticlesContract.ArticleEntry.COLUMN_NAME_CATEGORY + " = ?";
            String[] selectionArgs = {a.title, a.publishedAt.toString(), category};

            Cursor c = db.query(
                    ArticlesContract.ArticleEntry.TABLE_NAME,   // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    selectionArgs,                              // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    null                                        // The sort order
            );

            if (c.moveToFirst()) {
                c.close();
            } else {
                // Create a new map of values, where column names are the keys
                values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE, a.title);
                values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_DESCRIPTION, a.description);
                values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_URL, a.url);
                values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_URLTOIMAGE, a.urlToImage);
                values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_DATE, a.publishedAt.toString());
                values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_CATEGORY, category);

                // Insert the new row, returning the primary key value of the new row
                db.insert(ArticlesContract.ArticleEntry.TABLE_NAME, null, values);

                c.close();
            }
        }

        mDbHelper.close();
        return null;
    }
}