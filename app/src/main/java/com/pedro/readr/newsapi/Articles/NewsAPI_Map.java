package com.pedro.readr.newsapi.Articles;

import java.util.List;

public class NewsAPI_Map {
    private final List<Articles_Map> articles;

    public NewsAPI_Map(List<Articles_Map> articles) {
        this.articles = articles;
    }

    public List<Articles_Map> getArticles() {
        return articles;
    }
}