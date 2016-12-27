package pt.ismai.a26800.readr.adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import pt.ismai.a26800.readr.activities.ShowArticleActivity;
import pt.ismai.a26800.readr.newsapi.Articles.Articles_Map;
import pt.ismai.a26800.readr.R;
import pt.ismai.a26800.readr.sqlite.ArticlesContract;
import pt.ismai.a26800.readr.sqlite.ArticlesDbHelper;

public class NewsAdapter extends ArrayAdapter<Articles_Map> {
    private final Context mContext;

    public NewsAdapter(Context c, int resource) {
        super(c, resource);
        this.mContext = c;
    }

    private void doAdd(Articles_Map another) {
        super.add(another);
    }

    public void addAll(HashSet<Articles_Map> others, String category) {
        ArticlesDbHelper mDbHelper = new ArticlesDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Date comparisonDate = new Date(0);

        for (Articles_Map a : others) {
            if (a.title != null &&
                    a.description != null &&
                    a.description.trim().length() > 0 &&
                    a.url != null &&
                    a.urlToImage != null &&
                    a.publishedAt != null &&
                    a.publishedAt.after(comparisonDate)) {
                this.doAdd(a);

                if (!checkExistsDb(db, a.title, category)) {
                    // Create a new map of values, where column names are the keys
                    values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE, a.title);
                    values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_DESCRIPTION, a.description);
                    values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_URL, a.url);
                    values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_URLTOIMAGE, a.urlToImage);
                    values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_DATE, a.publishedAt.toString());
                    values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_CATEGORY, category);

                    // Insert the new row, returning the primary key value of the new row
                    db.insert(ArticlesContract.ArticleEntry.TABLE_NAME, null, values);
                }
            }
        }
        mDbHelper.close();
        this.sort(byPublishedAtComparator);
    }

    public void addAll(List<Articles_Map> articles) {
        for (Articles_Map a : articles) {
            this.doAdd(a);
        }
        this.sort(byPublishedAtComparator);
    }

    private static final Comparator<Articles_Map> byPublishedAtComparator =
            new Comparator<Articles_Map>() {
                @Override
                public int compare(Articles_Map o1, Articles_Map o2) {
                    if (o1.publishedAt == null) {
                        return (o2.publishedAt == null) ? 0 : -1;
                    } else if (o2.publishedAt == null) {
                        return 1;
                    }
                    return o2.publishedAt.compareTo(o1.publishedAt);
                }
            };

    private boolean checkExistsDb(SQLiteDatabase db, String title, String category) {
        /*ArticlesDbHelper mDbHelper = new ArticlesDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();*/

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
                "AND " + ArticlesContract.ArticleEntry.COLUMN_NAME_CATEGORY + " = ?";
        String[] selectionArgs = {title, category};

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
            //c.close();
            return true;
        } else {
            //c.close();
            return false;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the property we are displaying
        final Articles_Map article = getItem(position);

        // get the inflater and inflate the XML layout for each item, then set the listener
        // for each row to be clickable, and on click the user is redirected to the WebView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.article_layout, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnected()) {
                    Intent intent = new Intent(mContext, ShowArticleActivity.class);
                    intent.putExtra("url", article.url);
                    mContext.startActivity(intent);
                } else {
                    // Use the Builder class for convenient dialog construction
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder.setTitle(R.string.noconnection_title)
                            .setMessage(R.string.noconnection_desc)
                            .setPositiveButton(R.string.noconnection_close, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });

                    // Create the AlertDialog object and show it
                    builder.create();
                    builder.show();
                }
            }
        });

        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView description = (TextView) view.findViewById(R.id.description);

        Picasso.with(mContext).load(article.urlToImage).fit().centerCrop().into(thumbnail);
        title.setText(article.title);
        description.setText(article.publishedAt.toString());

        return view;
    }
}