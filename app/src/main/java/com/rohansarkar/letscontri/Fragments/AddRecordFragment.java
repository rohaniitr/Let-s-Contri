package com.rohansarkar.letscontri.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.rohansarkar.letscontri.Activities.StartNewActivity;
import com.rohansarkar.letscontri.Adapters.AddRecordAdapter;
import com.rohansarkar.letscontri.CustomData.DataAddRecord;
import com.rohansarkar.letscontri.CustomData.DataExpense;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManager;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManagerFriendsName;
import com.rohansarkar.letscontri.R;

import java.util.ArrayList;

/**
 * Created by rohan on 10/12/15.
 */
public class AddRecordFragment extends Fragment implements View.OnClickListener {

    public RecyclerView recyclerView;
    CoordinatorLayout layout;
    ArrayList<String> names;
    ArrayList<DataAddRecord> nameRecords;
    Spinner spinnerNames;
    FloatingActionButton addRecord;
    EditText etTitle, etAmount;

    String LOG_TAG= "AddRecordFragment Logs";
    String selectedSpinnerName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        checkDatabase();
        View view = inflater.inflate(R.layout.ui_add_record, container, false);
        init(view);
        getFriendsName();
        setSpinner();
        return view;
    }

    /*
    **Logic Implementation.
    * */

    private void add(String binary){
        //For NumberFormatException in string to Double conversion.
        try {
            DatabaseManager manager = new DatabaseManager(getActivity());
            manager.open();

            DataExpense data= new DataExpense();
            data.amount = Double.parseDouble(etAmount.getText().toString());
            data.binary= binary;
            data.name= selectedSpinnerName;
            data.title= etTitle.getText().toString();

            manager.createEntry(data);
            manager.close();

            //Clear the fields.
            etTitle.setText("");
            etAmount.setText("");
            spinnerNames.setSelection(0);
        }catch(NumberFormatException e){
            e.printStackTrace();
            showSnackBar("Please fill the Amount properly.");
        }

    }

    private void checkDatabase(){
        DatabaseManagerFriendsName manager= new DatabaseManagerFriendsName(getActivity());
        manager.open();
        if(manager.getData().isEmpty()){
            Intent i= new Intent(getActivity(), StartNewActivity.class);
            getActivity().finish();
            startActivity(i);
            getActivity().finish();
        }
        manager.close();
    }

    /*
    **Database Interface.
    * */

    private void getFriendsName(){
        DatabaseManagerFriendsName manager= new DatabaseManagerFriendsName(getActivity());
        manager.open();
        //It may be a problem, as have not initialised the list
        if(!manager.isEmpty())
            names= manager.getData();
        else
            Log.d(LOG_TAG, "Friend_Name table is empty");
        manager.close();

        for(int i=0; i<names.size(); i++)
            nameRecords.add(new DataAddRecord(names.get(i)));
    }

    /*
    **Initializations.
    * */

    private void init(View view){
        spinnerNames= (Spinner) view.findViewById(R.id.sAddRecordNameList);
        spinnerNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedSpinnerName= adapterView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        addRecord= (FloatingActionButton) view.findViewById(R.id.fabAddRecordAdd);
        addRecord.setOnClickListener(this);
        etTitle= (EditText) view.findViewById(R.id.etAddRecordTitle);
        etAmount= (EditText) view.findViewById(R.id.etAddRecordAmount);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvAddRecord);
        layout= (CoordinatorLayout) view.findViewById(R.id.clAddRecord);

        names= new ArrayList<>();
        nameRecords= new ArrayList<>();
    }

    private void setSpinner(){
        ArrayAdapter friendsAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, names);
        friendsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNames.setAdapter(friendsAdapter);
    }

    /*
    **Display Assets.
    * */

    private void launchDialog(final ArrayList<DataAddRecord> list){
        final Dialog dialog= new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_record);

        //Initialising Recycler View here.
        RecyclerView addRecordRecyclerView = (RecyclerView) dialog.findViewById(R.id.rvAddRecord);;
        RecyclerView.LayoutManager addRecordLayoutManager = new LinearLayoutManager(getActivity());
        addRecordRecyclerView.setLayoutManager(addRecordLayoutManager);

        RecyclerView.Adapter addRecordAdapter = new AddRecordAdapter(list, getActivity(), layout, addRecordRecyclerView);
        addRecordRecyclerView.setAdapter(addRecordAdapter);

        //Button Listener here.
        View.OnClickListener okListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String binary="";

                for(int i=0; i<list.size(); i++){
                    if(list.get(i).selected) binary += 1;
                    else binary+= 0;
                }

                Log.d(LOG_TAG, "Binary: " + binary);
                add(binary);
                dialog.dismiss();
            }
        };
        View.OnClickListener cancelListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        };

        //Changes in the adapter are reflected in "nameRecords".
        //Hence remove this on dismissing the dialog box.
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                for(int i=0; i<list.size(); i++)
                    list.get(i).selected=false;
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fabAddRecordAdd:
                if(etAmount.getText().toString().trim().length()>0) {
                    launchDialog(nameRecords);
                }
                else
                    showSnackBar("Amount field can not be left empty.");
                break;
        }
    }
}
