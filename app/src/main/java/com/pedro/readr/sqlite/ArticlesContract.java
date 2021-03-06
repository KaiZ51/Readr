package com.pedro.readr.sqlite;

import android.provider.BaseColumns;

public final class ArticlesContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ArticlesContract() {
    }

    /* Inner class that defines the table contents */
    public static class ArticleEntry implements BaseColumns {
        public static final String TABLE_NAME = "articles";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_URLTOIMAGE = "urlToImage";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_CATEGORY = "category";
    }
}
