package com.pedro.readr.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import com.pedro.readr.R;

public class ShowArticleActivity extends AppCompatActivity {
    private ShareActionProvider mShareActionProvider;
    private String url;
    private boolean immersiveCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        url = getIntent().getStringExtra("url");
        final WebView wv = (WebView) findViewById(R.id.wv_url);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar);
        //getSupportActionBar().setHideOnContentScrollEnabled(true);

        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                pb.setProgress(progress);
            }
        });

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                setTitle(wv.getTitle());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(wv.getTitle());
                //srLayout.setRefreshing(false);
                pb.setVisibility(View.GONE);
            }
        });
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl(url);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener
                    (new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            // Note that system bars will only be "visible" if none of the
                            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                // TODO: The system bars are visible. Make any desired
                                // adjustments to your UI, such as showing the action bar or
                                // other navigational controls.
                                exitImmersive();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (immersiveCheck) {
                                            // enter immersive mode after 3 seconds
                                            enterImmersive();
                                        }
                                    }
                                }, 3000);
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getMenuInflater().inflate(R.menu.menu_show_article, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.action_share);

            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, url);
            sendIntent.setType("text/plain");

            setShareIntent(Intent.createChooser(sendIntent, getResources().getString(R.string.share_title)));

            return true;
        } else {
            getMenuInflater().inflate(R.menu.menu_show_article_backwards, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.action_share);

            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, url);
            sendIntent.setType("text/plain");

            setShareIntent(Intent.createChooser(sendIntent, getResources().getString(R.string.share_title)));

            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_fullscreen) {
            // if immersive mode isn't on
            if (!immersiveCheck) {
                enterImmersive();
            }
            // if immersive mode is on, then set it off
            else {
                exitImmersive();
                immersiveCheck = false;
            }
        }

        if (id == R.id.action_refresh) {
            WebView wv = (WebView) findViewById(R.id.wv_url);
            wv.reload();

            ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar);
            pb.setVisibility(View.VISIBLE);
        }

        if (id == R.id.action_openbrowser) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @SuppressLint("InlinedApi")
    private void enterImmersive() {
        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

        getSupportActionBar().hide();
        immersiveCheck = true;
    }

    private void exitImmersive() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(0);
        getSupportActionBar().show();
    }
}
