package pt.ismai.a26800.readr.newsapi.Articles;

import java.util.List;

public class NewsAPI_Map {
    private final String status;
    private final String source;
    public final List<Articles_Map> articles;

    public NewsAPI_Map(String status, String source, List<Articles_Map> articles) {
        this.status = status;
        this.source = source;
        this.articles = articles;
    }
}