package pt.ismai.a26800.readr.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import pt.ismai.a26800.readr.R;

public class ShowArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_article);

        String url = getIntent().getStringExtra("url");

        WebView wv = (WebView) findViewById(R.id.wv_url);
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl(url);
    }
}
