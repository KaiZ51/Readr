package pt.ismai.a26800.readr;

import java.util.ArrayList;

public class Sources_Content {
    String id;
    String category;
    String language;
    String country;
    ArrayList<String> sortBysAvailable;

    public Sources_Content(String id,
                           String category,
                           String language,
                           String country,
                           ArrayList<String> sortBysAvailable) {
        this.id = id;
        this.category = category;
        this.language = language;
        this.country = country;
        this.sortBysAvailable = sortBysAvailable;
    }
}
