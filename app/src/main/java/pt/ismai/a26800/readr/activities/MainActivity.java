package pt.ismai.a26800.readr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import pt.ismai.a26800.readr.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // creates event handlers for each category button
        goToCategoryListener(1, "general");
        goToCategoryListener(2, "sport");
        goToCategoryListener(3, "business");
        goToCategoryListener(4, "entertainment");
        goToCategoryListener(5, "music");
        goToCategoryListener(6, "technology");
        goToCategoryListener(7, "gaming");
        goToCategoryListener(8, "science-and-nature");

        // creates the side navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_general) {
            goToCategory("general");
        } else if (id == R.id.nav_sports) {
            goToCategory("sport");
        } else if (id == R.id.nav_business) {
            goToCategory("business");
        } else if (id == R.id.nav_entertainment) {
            goToCategory("entertainment");
        } else if (id == R.id.nav_music) {
            goToCategory("music");
        } else if (id == R.id.nav_technology) {
            goToCategory("technology");
        } else if (id == R.id.nav_gaming) {
            goToCategory("gaming");
        } else if (id == R.id.nav_science_and_nature) {
            goToCategory("science-and-nature");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToCategory(String category) {
        Intent intent = new Intent(MainActivity.this, ListNewsActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    private void goToCategoryListener(int bID, final String category) {
        ImageButton buttonCats = (ImageButton) findViewById(getResources()
                .getIdentifier("imageButton" + bID, "id", this.getPackageName()));
        buttonCats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListNewsActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
    }
}