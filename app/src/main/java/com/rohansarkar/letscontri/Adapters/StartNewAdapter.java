package com.rohansarkar.letscontri.Adapters;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rohansarkar.letscontri.R;

import java.util.ArrayList;

/**
 * Created by rohan on 11/12/15.
 */
public class StartNewAdapter extends RecyclerView.Adapter<StartNewAdapter.ViewHolder> {
    public ArrayList<String> names;
    Context context;
    CoordinatorLayout layout;
    RecyclerView recyclerView;
    String LOG_TAG= "AddRecordAdapter Logs";

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public ViewHolder(View v) {
            super(v);
            name= (TextView) v.findViewById(R.id.tvStartNewName);
        }
    }

    public void deleteItem(int position) {
        Log.d("UIBrandImageAdapter", "position = " + position);
        names.remove(position);
        notifyDataSetChanged();
    }

    public StartNewAdapter (ArrayList<String> names, Context context, CoordinatorLayout layout, RecyclerView recyclerView) {
        this.names= names;
        this.layout= layout;
        this.context= context;
        this.recyclerView= recyclerView;
    }

    @Override
    public StartNewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_start_new, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText((position+1) + ". " + names.get(position));
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

}
