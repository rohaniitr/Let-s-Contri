package com.rohansarkar.letscontri.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rohansarkar.letscontri.CustomData.DataAddRecord;
import com.rohansarkar.letscontri.CustomData.DataExpense;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManager;
import com.rohansarkar.letscontri.R;

import java.util.ArrayList;

/**
 * Created by rohan on 10/12/15.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private ArrayList<DataExpense> history;
    private ArrayList<String> names;
    Context context;
    CoordinatorLayout layout;
    RelativeLayout permanentLayout, firstLayout;
    RecyclerView recyclerView;

    String LOG_TAG= "HistoryAdapter Logs";

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView serialNumber, name, title, amount, participants;
        ImageButton delete, edit;

        public ViewHolder(View v) {
            super(v);
            serialNumber= (TextView) v.findViewById(R.id.tvHistorySerialNumber);
            name= (TextView) v.findViewById(R.id.tvHistoryName);
            title= (TextView) v.findViewById(R.id.tvHistoryTitle);
            amount= (TextView) v.findViewById(R.id.tvHistoryAmount);
            participants= (TextView) v.findViewById(R.id.tvHistoryParticipants);
            delete= (ImageButton) v.findViewById(R.id.ibHistoryDelete);
            edit= (ImageButton) v.findViewById(R.id.ibHistoryEdit);
        }
    }

    public void deleteItem(int position) {
        Log.d("UIBrandImageAdapter", "position = " + position);
        history.remove(position);
        notifyDataSetChanged();
    }

    public HistoryAdapter(ArrayList<DataExpense> history, ArrayList<String> names, Context context,
                          CoordinatorLayout layout, RelativeLayout permanentLayout, RelativeLayout firstLayout,
                          RecyclerView recyclerView) {
        this.history= history;
        this.names= names;
        this.layout= layout;
        this.permanentLayout= permanentLayout;
        this.firstLayout= firstLayout;
        this.context= context;
        this.recyclerView= recyclerView;
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_history, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(LOG_TAG, "Id_"+ position + ": " + history.get(position).rowId);
        holder.serialNumber.setText("# " + (position+1));
        holder.name.setText(history.get(position).name);

        String formattedAmount= String.format("%.2f",history.get(position).amount);
        holder.amount.setText("Amount: " + '\u20B9' + formattedAmount);

        if(history.get(position).title!=null)
            holder.title.setText(history.get(position).title);

        String participantNames= "";
        String binary= history.get(position).binary;
        for(int i=0; i<names.size(); i++){
            if(binary.charAt(i)=='1')
                participantNames+= names.get(i) + ", ";
        }
        if(participantNames.length()>=2)
            participantNames= participantNames.substring(0,participantNames.length()-2);

        holder.participants.setText(participantNames);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlert(position);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchUpdateDialog(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return history.size();
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

     private void update(DataExpense dataExpense){
        DatabaseManager manager= new DatabaseManager(context);
        manager.open();
        long id= manager.updateEntry(dataExpense);
        Log.d(LOG_TAG, "Updated id: " + id);
        manager.close();
    }

    private void deleteRow(int position){
        DatabaseManager manager= new DatabaseManager(context);
        manager.open();
        Log.d(LOG_TAG, "Deleted Position : " + position);
        manager.deleteEntry(history.get(position).rowId);
        manager.close();
        history.remove(position);
        notifyDataSetChanged();
    }

    /*
    **Display Assets..
    * */

    private void launchUpdateDialog(final int position){
        final DataExpense dataExpense= new DataExpense();
        final Dialog dialog= new Dialog(context);
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
        ArrayAdapter friendsAdapter= new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, names);
        friendsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNames.setAdapter(friendsAdapter);

        //Initialise fields.
        etTitle.setText(history.get(position).title);
        etAmount.setText(String.format("%.2f", history.get(position).amount));
        spinnerNames.setSelection(names.indexOf(history.get(position).name));

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
                        launchListDialog(position, dataExpense);
                    } catch (Exception e) {
                        showSnackBar("Fill the amount properly.");
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

    private void launchListDialog(final int position, final DataExpense dataExpense){
        final Dialog dialog= new Dialog(context);
        dialog.getWindow().getAttributes().windowAnimations= R.style.CustomRightDialogAnimation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_record);

        String binary= history.get(position).binary;
        final ArrayList<DataAddRecord> nameRecords= new ArrayList<>();
        for(int i=0; i<names.size(); i++) {
            if(binary.charAt(i)=='1') nameRecords.add(new DataAddRecord(names.get(i), true));
            else nameRecords.add(new DataAddRecord(names.get(i), false));
        }

        //Initialising Recycler View here.
        RecyclerView addRecordRecyclerView = (RecyclerView) dialog.findViewById(R.id.rvAddRecord);;
        RecyclerView.LayoutManager addRecordLayoutManager = new LinearLayoutManager(context);
        addRecordRecyclerView.setLayoutManager(addRecordLayoutManager);

        RecyclerView.Adapter addRecordAdapter = new AddRecordAdapter(nameRecords, context, layout, addRecordRecyclerView);
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
                dataExpense.rowId= history.get(position).rowId;
                update(dataExpense);
                history.get(position).equals(dataExpense);
                showSnackBar("Changes have been saved successfully.");
                notifyDataSetChanged();

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

     private void showDeleteAlert(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Expense");

        builder.setMessage("Are you sure you want to delete this?");
        builder.setIcon(R.drawable.icon_delete_blue);

        builder.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                deleteRow(position);
                manageVisibility();
                showSnackBar("Deleted successfully.");
            }
        });

        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSnackBar(String message){
        if(layout!= null)
            Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
