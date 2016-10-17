package pt.ismai.a26800.readr;

public class Articles_Map {
    String title;
    String description;
    String url;
    String urlToImage;
    String publishedAt;

    public Articles_Map(String title, String description, String url, String urlToImage, String publishedAt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }
}