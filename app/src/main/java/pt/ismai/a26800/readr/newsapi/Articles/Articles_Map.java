package pt.ismai.a26800.readr.newsapi.Articles;

import java.util.Date;

public class Articles_Map {
    private final String title;
    private final String description;
    private final String url;
    private final String urlToImage;
    private final Date publishedAt;

    public Articles_Map(String title, String description, String url, String urlToImage, Date publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Articles_Map that = (Articles_Map) o;

        return title != null ? title.equals(that.title) : that.title == null;

    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }
}