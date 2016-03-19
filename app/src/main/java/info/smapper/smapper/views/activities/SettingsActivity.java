package info.smapper.smapper.views.activities;

import android.app.Activity;
import android.os.Bundle;

import info.smapper.smapper.views.fragments.SettingsFragment;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}