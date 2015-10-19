package com.example.mad.shellpandroid;

import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class ShellpActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationView mNavigationView;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    /**
     * Used to store the last screen title. For use in {@link #restoreToolBar()} ()}.
     */
    private CharSequence mTitle;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shellp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.shellp_toolbar);
        setSupportActionBar(toolbar);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
//        mNavigationView.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout),
//                toolbar);
        setUpDrawerContent(mNavigationView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // TODO Rework the actionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                toolbar,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
//                if (!isAdded()) {
//                    return;
//                }

//                this.invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                if (!isAdded()) {
//                    return;
//                }
//
//                if (!mUserLearnedDrawer) {
//                    // The user manually opened the drawer; store this flag to prevent auto-showing
//                    // the navigation drawer automatically in the future.
//                    mUserLearnedDrawer = true;
//                    SharedPreferences sp = PreferenceManager
//                            .getDefaultSharedPreferences(getActivity());
//                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
//                }
//
//                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                actionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.tab_title_schedule);
                break;
            case 2:
                mTitle = getString(R.string.tab_title_buses);
                break;
            case 3:
                mTitle = getString(R.string.tab_title_navigation);
                break;
        }
    }

    public void restoreToolBar() {
//        ActionBar actionBar = (ActionBar) getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
//
//        Toolbar toolbar = (Toolbar) getSupportActionBar();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationView.is) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.shellp, menu);
////            restoreToolBar();
//            return true;
//        }
        getMenuInflater().inflate(R.menu.shellp, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        onNavigationDrawerItemSelected(menuItem.getItemId());
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
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
        private static int sectionNum;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            sectionNum = sectionNumber;

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        /** TODO I think this has to be altered to handle the header image messing up with
         * the positions of the sectionNum, also set images for each item in the nav bar
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_schedule, container, false);
            if (sectionNum == R.id.schedule_drawer_menu) {
                rootView = inflater.inflate(R.layout.activity_schedule, container, false);
            }
            else if (sectionNum == R.id.buses_drawer_menu) {
                rootView = inflater.inflate(R.layout.activity_buses, container, false);
            }
            else if (sectionNum == R.id.navigation_drawer_menu) {
                rootView = inflater.inflate(R.layout.activity_navigation, container, false);
            }

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ShellpActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
