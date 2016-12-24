package pt.ismai.a26800.readr.newsapi.Articles;

import java.util.Date;

public class Articles_Map {
    public final String title;
    public final String description;
    public String url;
    public String urlToImage;
    public Date publishedAt;

    public Articles_Map(String title, String description, Date publishedAt) {
        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
    }

    public Articles_Map(String title, String description, String url, String urlToImage, Date publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }
}