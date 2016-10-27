package pt.ismai.a26800.readr.newsapi1.Articles;

import java.util.Date;

public class Articles_Map {
    public final String title;
    public final String description;
    public final String url;
    public final String urlToImage;
    public final Date publishedAt;

    public Articles_Map(String title, String description, String url, String urlToImage, Date publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }
}