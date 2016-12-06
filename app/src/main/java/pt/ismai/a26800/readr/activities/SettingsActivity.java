package pt.ismai.a26800.readr.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import pt.ismai.a26800.readr.R;
import pt.ismai.a26800.readr.notifications.NotificationAlarm;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            final View rootview = inflater.inflate(R.layout.activity_settings, container, false);
            Toolbar toolbar = (Toolbar) rootview.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

            return rootview;
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference connectionPref = findPreference(key);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            if (key.equals("notifications") && sharedPrefs.getBoolean("notifications", false)) {
                connectionPref.setSummary(R.string.notifications_on);
                String[] cats = sharedPrefs.getStringSet("notifications_cat_select", new HashSet<String>())
                        .toArray(new String[0]);
                long intervalValue = Long.parseLong(sharedPrefs.getString("notifications_interval", ""));
                long interval = TimeUnit.MINUTES.toMillis(intervalValue);
                scheduleNotificationsAlarm(true, cats, interval);
            } else if (key.equals("notifications") && !sharedPrefs.getBoolean("notifications", false)) {
                connectionPref.setSummary(R.string.notifications_off);
                scheduleNotificationsAlarm(false, null, 0);
            }

            if (key.equals("notifications_cat_select") || key.equals("notifications_interval")) {
                String[] cats = sharedPrefs.getStringSet("notifications_cat_select", new HashSet<String>())
                        .toArray(new String[0]);
                long intervalValue = Long.parseLong(sharedPrefs.getString("notifications_interval", ""));
                long interval = TimeUnit.MINUTES.toMillis(intervalValue);
                scheduleNotificationsAlarm(false, null, 0);
                scheduleNotificationsAlarm(true, cats, interval);
            }
        }

        private void scheduleNotificationsAlarm(boolean enabled, String[] cats, long interval) {
            AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), NotificationAlarm.class);
            intent.putExtra("cats", cats);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (enabled) {
                alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval,
                        interval,
                        alarmIntent);
                System.out.println("alarm on");
            } else {
                alarmMgr.cancel(alarmIntent);
                System.out.println("alarm off");
            }
        }
    }
}
