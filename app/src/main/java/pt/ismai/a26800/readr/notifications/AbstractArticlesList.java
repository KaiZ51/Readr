package pt.ismai.a26800.readr.notifications;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pt.ismai.a26800.readr.newsapi.Articles.Articles_Map;

public class AbstractArticlesList<T> extends AbstractList<T> {
    private final List<T> a;

    public AbstractArticlesList() {
        a = new ArrayList<>();
    }

    /*AbstractArticlesList(T[] array) {
        a = array;
    }*/

    public T get(int index) {
        return a.get(index);
    }

    public T set(int index, T element) {
        T oldValue = a.get(index);
        a.add(index, element);
        return oldValue;
    }

    public int size() {
        return a.size();
    }

    private void doAdd(T another) {
        super.add(another);
    }

    public void addAll(List<T> others) {
        for (T a : others) {
            this.doAdd(a);
        }
        //this.sort(byPublishedAtComparator);
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
