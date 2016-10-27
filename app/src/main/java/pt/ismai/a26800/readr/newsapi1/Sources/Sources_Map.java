package pt.ismai.a26800.readr.newsapi1.Sources;

import java.util.ArrayList;

public class Sources_Map {
    private final String status;
    public final ArrayList<Sources_Content> sources;

    public Sources_Map(String status, ArrayList<Sources_Content> sources) {
        this.status = status;
        this.sources = sources;
    }
}
