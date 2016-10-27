package pt.ismai.a26800.readr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import pt.ismai.a26800.readr.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // creates event handlers for each category button
        goToCategory(1, ListNewsActivity.class, "general");
        goToCategory(2, ListNewsActivity.class, "sport");
        goToCategory(3, ListNewsActivity.class, "business");
        goToCategory(4, ListNewsActivity.class, "entertainment");
        goToCategory(5, ListNewsActivity.class, "music");
        goToCategory(6, ListNewsActivity.class, "technology");
        goToCategory(7, ListNewsActivity.class, "gaming");
        goToCategory(8, ListNewsActivity.class, "science-and-nature");

        // creates the side navigation drawer
        String[] navDrawerStrings = getResources().getStringArray(R.array.nav_drawer_strings);
        //DrawerLayout navDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
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

    private void goToCategory(int bID, final Class destinyActivity, final String category) {
        ImageButton buttonCats = (ImageButton) findViewById(getResources()
                .getIdentifier("imageButton" + bID, "id", this.getPackageName()));
        buttonCats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, destinyActivity);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });
    }
}
