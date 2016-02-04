package com.rohansarkar.letscontri.Adapters;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.rohansarkar.letscontri.CustomData.DataAddRecord;
import com.rohansarkar.letscontri.R;

import java.util.ArrayList;

/**
 * Created by rohan on 10/12/15.
 */
public class AddRecordAdapter extends RecyclerView.Adapter<AddRecordAdapter.ViewHolder> {
    public ArrayList<DataAddRecord> names;
    Context context;
    CoordinatorLayout layout;
    RecyclerView recyclerView;
    String LOG_TAG= "AddRecordAdapter Logs";

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        CheckBox selected;
        public ViewHolder(View v) {
            super(v);
            name= (TextView) v.findViewById(R.id.tvAddRecordName);
            selected= (CheckBox) v.findViewById(R.id.cbAddRecordSelected);
        }
    }

    public void deleteItem(int position) {
        Log.d("UIBrandImageAdapter", "position = " + position);
        names.remove(position);
        notifyDataSetChanged();
    }

    public AddRecordAdapter(ArrayList<DataAddRecord> names, Context context, CoordinatorLayout layout, RecyclerView recyclerView) {
        this.names= names;
        this.layout= layout;
        this.context= context;
        this.recyclerView= recyclerView;
    }

    @Override
    public AddRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_add_record, parent, false);
        v.setOnClickListener(recyclerViewListener);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(names.get(position).name);
        holder.selected.setChecked(names.get(position).selected);

        holder.selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                names.get(position).selected= !names.get(position).selected;
            }
        });

        Log.d(LOG_TAG, "Position: " + position + ", Status: "+ names.get(position).selected);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    void showSnackBar(String message){
        if(layout!= null)
            Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }
    public void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    final private View.OnClickListener recyclerViewListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position= recyclerView.getChildPosition(v);

            CheckBox selected= (CheckBox) v.findViewById(R.id.cbAddRecordSelected);
            selected.setChecked(!selected.isChecked());

            names.get(position).selected= selected.isChecked();

            Log.d(LOG_TAG, "..Position: " + position + ", Status: "+ names.get(position).selected);
        }
    };

}
