package pt.ismai.a26800.readr.notifications;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pt.ismai.a26800.readr.newsapi.Articles.Articles_Map;

public class ArticlesList extends ArrayList<Articles_Map> {
    private void doAdd(Articles_Map another) {
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
}
