package com.rohansarkar.letscontri.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rohansarkar.letscontri.Adapters.AddRecordAdapter;
import com.rohansarkar.letscontri.Adapters.HistoryAdapter;
import com.rohansarkar.letscontri.Adapters.PersonalNotesAdapter;
import com.rohansarkar.letscontri.CustomData.DataAddRecord;
import com.rohansarkar.letscontri.CustomData.DataExpense;
import com.rohansarkar.letscontri.CustomData.DataNote;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManager;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManagerFriendsName;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManagerNotes;
import com.rohansarkar.letscontri.R;

import java.util.ArrayList;

/**
 * Created by rohan on 11/1/16.
 */
public class PersonalNotesFragment extends Fragment implements View.OnClickListener{

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    CoordinatorLayout layout;
    RelativeLayout firstLayout, permanentLayout;
    TextView firstAdd;
    FloatingActionButton addNote;

    ArrayList<DataNote> notes;
    String LOG_TAG= "PersonalNotesFragment Logs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_personal_notes, container, false);
        init(view);
        getNotes();
        manageVisibility();
        setRecyclerView();
        return view;
    }

    /*
    **Logic Implementation.
    * */

    private void manageVisibility(){
        if(notes.size()<=0){
            permanentLayout.setVisibility(View.GONE);
            firstLayout.setVisibility(View.VISIBLE);
        }
        else{
            permanentLayout.setVisibility(View.VISIBLE);
            firstLayout.setVisibility(View.GONE);
        }
    }

    /*
    **Database Interface.
    * */

    private long addNote(String dataNote){
        DatabaseManagerNotes manager = new DatabaseManagerNotes(getActivity());
        manager.open();
        long rowId= manager.createEntry(dataNote);
        Log.d(LOG_TAG, "Created Entry.");
        manager.close();
        return rowId;
    }

    private void getNotes(){
        DatabaseManagerNotes manager= new DatabaseManagerNotes(getActivity());
        manager.open();
        notes.clear();
        notes= manager.getNotes();
        manager.close();
    }

    /*
    **Initializations.
    * */

    private void init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.rvPersonalNotes);
        layout= (CoordinatorLayout) view.findViewById(R.id.clPersonalNotes);
        permanentLayout= (RelativeLayout) view.findViewById(R.id.rlPersonalNotesPermanent);
        firstLayout= (RelativeLayout) view.findViewById(R.id.rlPersonalNotesFirst);
        firstAdd= (TextView) view.findViewById(R.id.tvPersonalNotesFirst);
        addNote= (FloatingActionButton) view.findViewById(R.id.fabPersonalNotesAdd);
        firstAdd.setOnClickListener(this);
        addNote.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        notes= new ArrayList<>();
    }

    private void setRecyclerView(){
        adapter = new PersonalNotesAdapter(notes, getActivity(), layout, permanentLayout, firstLayout, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    /*
    **Display Assets..
    * */

    private void launchAddDialog(){
        final Dialog dialog= new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations= R.style.CustomLeftDialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_note);

        WindowManager.LayoutParams lp= new WindowManager.LayoutParams();
        Window window= dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        final EditText note= (EditText) dialog.findViewById(R.id.etAddNoteNote);
        FloatingActionButton done= (FloatingActionButton) dialog.findViewById(R.id.fabAddNoteDone);


        //Button Listener here.
        View.OnClickListener doneListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(note.getText().toString().trim().length()>0){
                    long rowId= addNote(note.getText().toString());
                    DataNote data= new DataNote(Integer.parseInt(rowId+""), note.getText().toString());
                    notes.add(data);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else{
                    showToast("Enter the note first.");
                }
            }
        };

        DialogInterface.OnDismissListener dismissListener= new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                manageVisibility();
            }
        };

        //Buttons here.
        done.setOnClickListener(doneListener);
        dialog.setOnDismissListener(dismissListener);
        dialog.show();
    }

    private void showSnackBar(String message){
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /*
    **Overriding events.
    * */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fabPersonalNotesAdd:
                launchAddDialog();
                break;
            case R.id.tvPersonalNotesFirst:
                launchAddDialog();
                break;
        }
    }
}
