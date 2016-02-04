package com.rohansarkar.letscontri.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
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
import com.rohansarkar.letscontri.CustomData.DataNote;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManager;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManagerNotes;
import com.rohansarkar.letscontri.R;

import java.util.ArrayList;

/**
 * Created by rohan on 11/1/16.
 */
public class PersonalNotesAdapter extends RecyclerView.Adapter<PersonalNotesAdapter.ViewHolder> {
    private ArrayList<DataNote> notes;
    Context context;
    CoordinatorLayout layout;
    RelativeLayout firstLayout, permanentLayout;
    RecyclerView recyclerView;

    String LOG_TAG= "PersonalNotesAdapter Logs";

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView serialNumber, note;
        ImageButton delete, edit;

        public ViewHolder(View v) {
            super(v);
            serialNumber= (TextView) v.findViewById(R.id.tvPersonalNotesSerialNumber);
            note= (TextView) v.findViewById(R.id.tvPersonalNotesNote);
            delete= (ImageButton) v.findViewById(R.id.ibPersonalNotesDelete);
            edit= (ImageButton) v.findViewById(R.id.ibPersonalNotesEdit);
        }
    }

    public PersonalNotesAdapter(ArrayList<DataNote> notes, Context context, CoordinatorLayout layout,
                                RelativeLayout permanentLayout, RelativeLayout firstLayout, RecyclerView recyclerView){
        this.notes= notes;
        this.layout= layout;
        this.permanentLayout= permanentLayout;
        this.firstLayout= firstLayout;
        this.context= context;
        this.recyclerView= recyclerView;
    }

    @Override
    public PersonalNotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_personal_notes, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.serialNumber.setText("# " + (position+1));
        holder.note.setText(notes.get(position).note);

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
        return notes.size();
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

    private void updateNote(int position, DataNote dataNote){
        DatabaseManagerNotes manager= new DatabaseManagerNotes(context);
        manager.open();
        manager.updateEntry(dataNote);
        manager.close();
        notes.get(position).note= dataNote.note;
        notifyDataSetChanged();
        Log.d(LOG_TAG, "Updated id: " + dataNote.rowId);
    }

    private void deleteNote(int position){
        DatabaseManagerNotes manager= new DatabaseManagerNotes(context);
        manager.open();
        manager.deleteEntry(notes.get(position).rowId);
        manager.close();
        notes.remove(position);
        notifyDataSetChanged();
        Log.d(LOG_TAG, "Deleted Position : " + position);
    }

    /*
    **Display Assets..
    * */

    private void launchUpdateDialog(final int position){
        final Dialog dialog= new Dialog(context);
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
        note.setText(notes.get(position).note);

        //Button Listener here.
        View.OnClickListener doneListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(note.getText().toString().trim().length()>0){
                    DataNote dataNote= new DataNote(notes.get(position).rowId, note.getText().toString());
                    updateNote(position, dataNote);
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
                else{
                    showToast("Enter the note first.");
                }
            }
        };

        //Buttons here.
        done.setOnClickListener(doneListener);
        dialog.show();
    }

    private void showDeleteAlert(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Expense");

        builder.setMessage("Are you sure you want to delete this?");
        builder.setIcon(R.drawable.icon_delete_blue);

        builder.setPositiveButton("Delete",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                deleteNote(position);
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
