package pt.ismai.a26800.readr.adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Comparator;
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

    public void addAll(List<Articles_Map> others) {
        ArticlesDbHelper mDbHelper = new ArticlesDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (Articles_Map a : others) {
            this.doAdd(a);

            if (a.publishedAt != null) {
                // Create a new map of values, where column names are the keys
                values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_TITLE, a.title);
                values.put(ArticlesContract.ArticleEntry.COLUMN_NAME_DATE, a.publishedAt.toString());

                // Insert the new row, returning the primary key value of the new row
                db.insert(ArticlesContract.ArticleEntry.TABLE_NAME, null, values);
            }
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
                    return o1.publishedAt.compareTo(o2.publishedAt);
                }
            };

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
                Intent intent = new Intent(mContext, ShowArticleActivity.class);
                intent.putExtra("url", article.url);
                mContext.startActivity(intent);
            }
        });

        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView description = (TextView) view.findViewById(R.id.description);

        Picasso.with(mContext).load(article.urlToImage).fit().centerCrop().into(thumbnail);
        title.setText(article.title);
        description.setText(article.description);

        return view;
    }
}