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

import java.util.Comparator;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<Articles_Map> {
    Context mContext;

    public NewsAdapter(Context c, int resource) {
        super(c, resource);
        this.mContext = c;
    }

    protected void doAdd(Articles_Map another) {
        super.add(another);
    }

    public void addAll(List<Articles_Map> others) {
        for (Articles_Map a : others) {
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
                    return o1.publishedAt.compareTo(o2.publishedAt);
                }
            };

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

        Picasso.with(mContext).load(article.urlToImage).fit().centerCrop().into(thumbnail);
        title.setText(article.title);
        description.setText(article.description);

        return view;
    }
}