package pt.ismai.a26800.readr;

import java.util.ArrayList;

public class NewsAPI_Map {
    String status;
    String source;
    ArrayList<Articles_Map> articles;

    public NewsAPI_Map(String status, String source, ArrayList<Articles_Map> articles) {
        this.status = status;
        this.source = source;
        this.articles = articles;
    }
}