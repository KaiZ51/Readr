package pt.ismai.a26800.readr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // creates event handlers for each category button
        for (int i = 1; i <= 6; i++) {
            ImageButton buttonCats = (ImageButton) findViewById(getResources()
                    .getIdentifier("imageButton" + i, "id", this.getPackageName()));
            buttonCats.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ListNewsActivity.class);
                    startActivity(intent);
                }
            });
        }

        // creates the side navigation drawer
        String[] navDrawerStrings = getResources().getStringArray(R.array.nav_drawer_strings);
        DrawerLayout navDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        ListView navDrawerOptions = (ListView) findViewById(R.id.navBarContent);

        // Set the adapter for the list view
        navDrawerOptions.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, navDrawerStrings));
        // Set the list's click listener
        //navDrawerOptions.setOnItemClickListener(new DrawerItemClickListener());
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
