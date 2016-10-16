package pt.ismai.a26800.readr;

import java.util.ArrayList;

public class Sources_Map {
    String status;
    ArrayList<Sources_Content> sources;

    public Sources_Map(String status, ArrayList<Sources_Content> sources) {
        this.status = status;
        this.sources = sources;
    }
}
