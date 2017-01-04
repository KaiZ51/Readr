package pt.ismai.a26800.readr.newsapi.Sources;

public class Sources_Content {
    private final String id;
    private final String category;
    private final String language;
    private final String country;

    public Sources_Content(String id, String category, String language, String country) {
        this.id = id;
        this.category = category;
        this.language = language;
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }
}
