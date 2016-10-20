package pt.ismai.a26800.readr;

import java.util.Date;

public class Articles_Map {
    String title;
    String description;
    String url;
    String urlToImage;
    Date publishedAt;

    public Articles_Map(String title, String description, String url, String urlToImage, Date publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }
}