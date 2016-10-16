package pt.ismai.a26800.readr;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<Articles_Map> {
    Context mContext;
    ArrayList<Articles_Map> articles;

    public NewsAdapter(Context c, int resource, ArrayList<Articles_Map> articles) {
        super(c, resource, articles);
        this.mContext = c;
        this.articles = articles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the property we are displaying
        Articles_Map article = articles.get(position);

        // get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.article_layout, null);

        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView description = (TextView) view.findViewById(R.id.description);

        Picasso.with(mContext).load(article.urlToImage).into(thumbnail);
        title.setText(article.title);
        description.setText(article.description);

        return view;
    }
}