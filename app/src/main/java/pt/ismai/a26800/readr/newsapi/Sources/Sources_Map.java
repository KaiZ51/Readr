package pt.ismai.a26800.readr.newsapi.Sources;

import java.util.ArrayList;

public class Sources_Map {
    private final ArrayList<Sources_Content> sources;

    public Sources_Map(ArrayList<Sources_Content> sources) {
        this.sources = sources;
    }

    public ArrayList<Sources_Content> getSources() {
        return sources;
    }
}
