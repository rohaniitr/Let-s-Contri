package com.rohansarkar.letscontri.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;
import android.view.View;

import com.rohansarkar.letscontri.Adapters.ContainerAdapter;
import com.rohansarkar.letscontri.Fragments.CalculateShareFragment;
import com.rohansarkar.letscontri.Fragments.HistoryFragment;
import com.rohansarkar.letscontri.Fragments.PersonalNotesFragment;
import com.rohansarkar.letscontri.R;

/**
 * Created by rohan on 10/12/15.
 */
public class ContainerHomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    CoordinatorLayout layout;
    ViewPager viewPager;
    ContainerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_container);

        init();
        setToolbar();
        setNavigationDrawer();

        viewPager = (ViewPager) findViewById(R.id.vpContainer);
        setupViewPager(viewPager);
    }

    /*
    **Initializations.
    * */

    private void init(){
        layout= (CoordinatorLayout) findViewById(R.id.clContainer);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ContainerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HistoryFragment());
        viewPager.setAdapter(adapter);

    }

    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tbContainer);
        setSupportActionBar(toolbar);
    }

    /*
    **Display Assets..
    * */

     private void showSnackBar(String message){
        if(message!=null)
            Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }

    /*
    **Overriding events.
    * */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

        }
        return super.onOptionsItemSelected(item);
    }

    private void setNavigationDrawer(){
        navigationView = (NavigationView) findViewById(R.id.nvContainer);
        navigationView.setItemIconTintList(null);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()){
                    case R.id.action_history_of_expenses:
                        adapter = new ContainerAdapter(getSupportFragmentManager());
                        adapter.addFragment(new HistoryFragment());
                        viewPager.setAdapter(adapter);
                        break;
                    case R.id.action_calculate_shares:
                        adapter = new ContainerAdapter(getSupportFragmentManager());
                        adapter.addFragment(new CalculateShareFragment());
                        viewPager.setAdapter(adapter);
                        break;
                    case R.id.action_start_new:
                        Intent iStartNew= new Intent(ContainerHomeActivity.this, StartNewActivity.class);
                        startActivity(iStartNew);
                        finish();
                        break;
                    case R.id.action_personal_notes:
                        adapter = new ContainerAdapter(getSupportFragmentManager());
                        adapter.addFragment(new PersonalNotesFragment());
                        viewPager.setAdapter(adapter);
                        break;
                    case R.id.action_help:
                        showSnackBar("Help.");
                        break;
                }
                return true;
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.dlContainer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }
}