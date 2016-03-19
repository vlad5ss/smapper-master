package info.smapper.smapper.views.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.android.gms.maps.OnMapReadyCallback;

import info.smapper.smapper.R;
import info.smapper.smapper.data.Configuration;
import info.smapper.smapper.logic.BackgroundUploader;
import info.smapper.smapper.logic.BackgroundWorker;
import info.smapper.smapper.logic.IoHandler;
import info.smapper.smapper.logic.Logger;
import info.smapper.smapper.views.fragments.ConnectionFragment;
import info.smapper.smapper.views.fragments.LogFragment;
import info.smapper.smapper.views.fragments.MapFragment;
import info.smapper.smapper.views.fragments.NetworksFragment;

public class MainActivity extends AppCompatActivity {
    private static Activity activity;
    public static float version = 0.04f;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.activity = this;

        Logger.initLogger(this);
        Logger.add("Starting Smapper v" + version + ".");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selection) {
                        switch (selection) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Uri path = Uri.fromFile(IoHandler.getAccessibleFileObject());
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                // set the type to 'email'
                                emailIntent.setType("vnd.android.cursor.item/email");
                                String to[] = {"gadient@gmx.ch"};
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                                // the attachment
                                emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                                // the mail subject
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Smapper Measurements");
                                startActivity(Intent.createChooser(emailIntent, "Publish measurements with:"));
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity);
                builder.setMessage("Do you want to publish your measurements?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        IoHandler.initIoHandler(this);
        BackgroundWorker.initWorker(this);
        BackgroundUploader.initWorker(this);

        Configuration config = IoHandler.readSettings();
        BackgroundWorker.setUpdateInterval(config.getUpdateInterval());
        BackgroundWorker.setCompatibleModeState(config.getCompatibleModeStatus());
        MapFragment.setMapType(config.getMapType());

        Logger.add("Settings restored.");
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {}

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            if (position == 0) {
                return new MapFragment();
            }
            if (position == 1) {
                return new ConnectionFragment();
            }
            if (position == 2) {
                return new NetworksFragment();
            }
            if (position == 3) {
                return new LogFragment();
            }

            return null; // should not happen
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Map";
                case 1:
                    return "Current Connection";
                case 2:
                    return "Available Networks";
                case 3:
                    return "Log";

            }
            return null;
        }
    }

    private class MapIsReady implements OnMapReadyCallback {
        public void onMapReady(com.google.android.gms.maps.GoogleMap map) {

    }

    }
}

