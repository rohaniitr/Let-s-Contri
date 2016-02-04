package com.rohansarkar.letscontri;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

/**
 * Created by rohan on 29/9/15.
 */
public class Help extends ActionBarActivity {
    CoordinatorLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        setActionbar();

        init();
        setPagerAdapter();
        showSnackBar("Swipe Right and Left to navigate.");
    }

    void setPagerAdapter(){
        HelpAdapter mCustomPagerAdapter = new HelpAdapter(this);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.vpHelp);
        mViewPager.setAdapter(mCustomPagerAdapter);
    }

    void init(){
        layout= (CoordinatorLayout) findViewById(R.id.clHelp);
    }

    void setActionbar(){
        ActionBar action= getSupportActionBar();
        action.setDisplayHomeAsUpEnabled(true);
        action.show();
    }

    void showSnackBar(String message){
        if(message!=null)
            Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
