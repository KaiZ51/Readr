package com.pedro.readr.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.pedro.readr.activities.ShowArticleActivity;
import com.pedro.readr.asynctasks.InsertNewArticles;
import com.pedro.readr.newsapi.Articles.Articles_Map;
import com.pedro.readr.R;

public class NewsAdapter extends ArrayAdapter<Articles_Map> {
    private final Context mContext;

    public NewsAdapter(Context c, @SuppressWarnings("SameParameterValue") int resource) {
        super(c, resource);
        this.mContext = c;
    }

    private void doAdd(Articles_Map another) {
        super.add(another);
    }

    public void addAll(HashSet<Articles_Map> others, String category) {
        Date comparisonDate = new Date(0);
        List<Articles_Map> listArticles = new ArrayList<>();

        for (Articles_Map a : others) {
            if (a.getTitle() != null &&
                    a.getDescription() != null &&
                    a.getDescription().trim().length() > 0 &&
                    a.getUrl() != null &&
                    a.getUrlToImage() != null &&
                    a.getUrlToImage().trim().length() > 0 &&
                    a.getPublishedAt() != null &&
                    a.getPublishedAt().after(comparisonDate)) {
                this.doAdd(a);
                listArticles.add(a);
            }
        }

        new InsertNewArticles(getContext(), listArticles, category).execute();
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
                    if (o1.getPublishedAt() == null) {
                        return (o2.getPublishedAt() == null) ? 0 : -1;
                    } else if (o2.getPublishedAt() == null) {
                        return 1;
                    }
                    return o2.getPublishedAt().compareTo(o1.getPublishedAt());
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
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null && activeNetwork.isConnected()) {
                    Intent intent = new Intent(mContext, ShowArticleActivity.class);
                    intent.putExtra("url", article.getUrl());
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

        Picasso.with(mContext).load(article.getUrlToImage()).fit().centerCrop().into(thumbnail);
        title.setText(article.getTitle());
        description.setText(article.getDescription());

        return view;
    }
}