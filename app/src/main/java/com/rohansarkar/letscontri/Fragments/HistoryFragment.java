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

import com.rohansarkar.letscontri.Adapters.AddRecordAdapter;
import com.rohansarkar.letscontri.Adapters.HistoryAdapter;
import com.rohansarkar.letscontri.CustomData.DataAddRecord;
import com.rohansarkar.letscontri.CustomData.DataExpense;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManager;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManagerFriendsName;
import com.rohansarkar.letscontri.R;

import java.util.ArrayList;

/**
 * Created by rohan on 10/12/15.
 */
public class HistoryFragment extends Fragment implements View.OnClickListener{

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    CoordinatorLayout layout;
    RelativeLayout firstLayout, permanentLayout;
    TextView first;
    FloatingActionButton addRecord;

    ArrayList<DataExpense> history;
    ArrayList<String> names;
    String LOG_TAG= "HistoryFragment Logs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_history, container, false);
        init(view);
        getExpenses();
        getNames();
        manageVisibility();
        setRecyclerView();
        return view;
    }

    /*
    **Logic Implementation.
    * */

    private void manageVisibility(){
        if(history.size()<=0){
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

    private void addRecord(DataExpense dataExpense){
        DatabaseManager manager = new DatabaseManager(getActivity());
        manager.open();
        manager.createEntry(dataExpense);
        Log.d(LOG_TAG, "Created Entry.");
        manager.close();
    }

    private void getExpenses(){
        DatabaseManager manager= new DatabaseManager(getActivity());
        manager.open();
        history.clear();
        history= manager.getExpenses();
        manager.close();
    }

    private void getNames(){
        DatabaseManagerFriendsName manager= new DatabaseManagerFriendsName(getActivity());
        manager.open();
        if(!manager.isEmpty())
            names= manager.getData();
        else
            Log.d(LOG_TAG, "Friend_Name table is empty");
        manager.close();
    }

    /*
    **Initializations.
    * */

     private void init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.rvHistory);
        layout= (CoordinatorLayout) view.findViewById(R.id.clHistory);
        permanentLayout= (RelativeLayout) view.findViewById(R.id.rlHistoryPermanent);
        firstLayout= (RelativeLayout) view.findViewById(R.id.rlHistoryFirst);
        first= (TextView) view.findViewById(R.id.tvHistoryFirst);
        addRecord= (FloatingActionButton) view.findViewById(R.id.fabHistoryAdd);
        first.setOnClickListener(this);
        addRecord.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        history= new ArrayList<>();
        names= new ArrayList<>();
    }

    private void setRecyclerView(){
        adapter = new HistoryAdapter(history, names, getActivity(), layout, permanentLayout, firstLayout, recyclerView);
        recyclerView.setAdapter(adapter);
    }

    /*
    **Display Assets..
    * */

    private void launchAddDialog(){
        final DataExpense dataExpense= new DataExpense();
        final Dialog dialog= new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations= R.style.CustomLeftDialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_record);

        WindowManager.LayoutParams lp= new WindowManager.LayoutParams();
        Window window= dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        final EditText etTitle= (EditText) dialog.findViewById(R.id.etAddRecordTitle);
        final EditText etAmount= (EditText) dialog.findViewById(R.id.etAddRecordAmount);
        Spinner spinnerNames= (Spinner) dialog.findViewById(R.id.sAddRecordNameList);
        FloatingActionButton done= (FloatingActionButton) dialog.findViewById(R.id.fabAddRecordAdd);

        //Set spinner
        ArrayAdapter friendsAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, names);
        friendsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNames.setAdapter(friendsAdapter);

        //Button Listener here.
        View.OnClickListener doneListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etAmount.getText().toString().trim().length()<=0)
                    showSnackBar("Fill the amount.");
                else {
                    try {
                        dataExpense.amount = Double.parseDouble(etAmount.getText().toString());
                        dataExpense.title = etTitle.getText().toString();
                        launchListDialog(dataExpense);
                    } catch (Exception e) {
                        showSnackBar("Fill the amount field properly.");
                    }
                }
                dialog.dismiss();
            }
        };

        spinnerNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                dataExpense.name= adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Buttons here.
        done.setOnClickListener(doneListener);

        dialog.show();
    }

    private void launchListDialog(final DataExpense dataExpense){
        final Dialog dialog= new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations= R.style.CustomRightDialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_record);

        String binary= "";
        final ArrayList<DataAddRecord> nameRecords= new ArrayList<>();
        for(int i=0; i<names.size(); i++) {
            nameRecords.add(new DataAddRecord(names.get(i), false));
        }

        //Initialising Recycler View here.
        RecyclerView addRecordRecyclerView = (RecyclerView) dialog.findViewById(R.id.rvAddRecord);;
        RecyclerView.LayoutManager addRecordLayoutManager = new LinearLayoutManager(getActivity());
        addRecordRecyclerView.setLayoutManager(addRecordLayoutManager);

        RecyclerView.Adapter addRecordAdapter = new AddRecordAdapter(nameRecords, getActivity(), layout, addRecordRecyclerView);
        addRecordRecyclerView.setAdapter(addRecordAdapter);

        //Button Listener here.
        View.OnClickListener okListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String binary="";

                for(int i=0; i<nameRecords.size(); i++){
                    if(nameRecords.get(i).selected) binary += 1;
                    else binary+= 0;
                }
                dataExpense.binary= binary;

                addRecord(dataExpense);
                history.add(dataExpense);
                manageVisibility();
                showSnackBar("New transaction added.");
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        };
        View.OnClickListener cancelListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        };

        //Changes in the adapter are reflected in "list".
        //Hence remove this on dismissing the dialog box.
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                for(int i=0; i<nameRecords.size(); i++)
                    nameRecords.get(i).selected=false;
            }
        });

        //Buttons here.
        Button ok= (Button) dialog.findViewById(R.id.bAddRecordDialogOk);
        Button cancel= (Button) dialog.findViewById(R.id.bAddRecordDialogCancel);
        ok.setOnClickListener(okListener);
        cancel.setOnClickListener(cancelListener);

        dialog.show();
    }

    public void showSnackBar(String message){
        Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
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
            case R.id.fabHistoryAdd:
                launchAddDialog();
                break;
            case R.id.tvHistoryFirst:
                launchAddDialog();
                break;
        }
    }
}
