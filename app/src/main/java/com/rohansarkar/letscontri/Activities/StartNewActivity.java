package com.rohansarkar.letscontri.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.rohansarkar.letscontri.Adapters.StartNewAdapter;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManager;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManagerFriendsName;
import com.rohansarkar.letscontri.R;

import java.util.ArrayList;

/**
 * Created by rohan on 11/12/15.
 */
public class StartNewActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    CoordinatorLayout layout;
    EditText etName;
    FloatingActionButton fabAdd, fabDone;
    Toolbar toolbar;

    boolean singleEntryDone= false;
    ArrayList<String> names;
    String LOG_TAG= "StartNewFragment Logs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_start_new);
        init();
        setRecyclerView();
        setToolbar();
    }

    /*
    **Logic Implementation.
    * */

    /*
    **Database Interface.
    * */

    private void addName(){
        if(etName.getText().toString().trim().length()>0) {
            if(!singleEntryDone){
                deleteAllDatabase();
                singleEntryDone= true;
            }

            DatabaseManagerFriendsName manager = new DatabaseManagerFriendsName(this);
            manager.open();
            manager.createEntry(etName.getText().toString());
            manager.close();

            names.add(etName.getText().toString());
            adapter.notifyDataSetChanged();
            showSnackBar(etName.getText().toString() + " added.");
            etName.setText("");
        }
        else
            showSnackBar("Enter the name first.");
    }

    private void deleteAllDatabase(){
        //Delete both the databases.
        DatabaseManagerFriendsName friendManager= new DatabaseManagerFriendsName(this);
        friendManager.open();
        friendManager.deleteAll();
        friendManager.close();

        DatabaseManager manager= new DatabaseManager(this);
        manager.open();
        manager.deleteAll();
        manager.close();
    }

    /*
    **Initializations.
    * */

    private void init(){
        recyclerView = (RecyclerView) findViewById(R.id.rvStartNew);
        layout= (CoordinatorLayout) findViewById(R.id.clStartNew);
        etName= (EditText) findViewById(R.id.etStartNewName);
        fabAdd= (FloatingActionButton) findViewById(R.id.fabStartNewAdd);
        fabDone= (FloatingActionButton) findViewById(R.id.fabStartNewDone);
        fabAdd.setOnClickListener(this);
        fabDone.setOnClickListener(this);

        names= new ArrayList<>();
    }

    private void setRecyclerView(){
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StartNewAdapter(names, this, layout, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    private void setToolbar(){
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tbStartNew);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*
    **Display Assets..
    * */

    private void showDeleteAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");

        // set dialog message
        builder.setMessage("Starting a new contri trip will erase your previous data.");
        builder.setIcon(R.drawable.icon_delete_blue);

        builder.setPositiveButton("Proceed",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                addName();
                singleEntryDone= true;
            }
        });

        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                showSnackBar("Cancel");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showSnackBar(String message){
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }

    /*
    **Overriding events.
    * */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fabStartNewAdd:
                if(etName.getText().toString().trim().length()>0) {
                    if (!singleEntryDone)
                        showDeleteAlert();
                    else
                        addName();
                }
                else
                    showSnackBar("Enter the name first.");
                break;
            case R.id.fabStartNewDone:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent iContainer= new Intent(StartNewActivity.this, ContainerHomeActivity.class);
        startActivity(iContainer);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
