package pt.ismai.a26800.readr;

import java.util.ArrayList;

public class NewsAPI {
    String status;
    String source;
    ArrayList<Articles> articles;

    public NewsAPI(String status, String source, ArrayList<Articles> articles) {
        this.status = status;
        this.source = source;
        this.articles = articles;
    }
}