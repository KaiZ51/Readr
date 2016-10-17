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

import java.util.List;

public class NewsAdapter extends ArrayAdapter<Articles_Map> {
    Context mContext;

    public NewsAdapter(Context c, int resource) {
        super(c, resource);
        this.mContext = c;
    }

    public NewsAdapter(Context c, int resource, List<Articles_Map> articles) {
        super(c, resource, articles);
        this.mContext = c;
    }

    /*public void addAll(List<Articles_Map> articles) {
        if (this.articles == null) {
            this.articles = new ArrayList<>(articles);
        } else {
            this.articles.addAll(articles);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int size = articles == null ? 0 : articles.size();
        return size;
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the property we are displaying
        Articles_Map article = getItem(position);

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