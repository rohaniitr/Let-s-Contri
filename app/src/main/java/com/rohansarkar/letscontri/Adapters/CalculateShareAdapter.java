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

import com.rohansarkar.letscontri.CustomData.DataCalculateShare;
import com.rohansarkar.letscontri.R;

import java.util.ArrayList;

/**
 * Created by rohan on 10/12/15.
 */
public class CalculateShareAdapter extends RecyclerView.Adapter<CalculateShareAdapter.ViewHolder> {
    private ArrayList<DataCalculateShare> shares;
    Context context;
    CoordinatorLayout layout;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView serialNumber, payer, amount;

        public ViewHolder(View v) {
            super(v);
            serialNumber= (TextView) v.findViewById(R.id.tvCalculateShareSerialNumber);
            payer= (TextView) v.findViewById(R.id.tvCalculateSharePayer);
            amount= (TextView) v.findViewById(R.id.tvCalculateShareAmount);
        }
    }

    public void deleteItem(int position) {
        Log.d("UIBrandImageAdapter", "position = " + position);
        shares.remove(position);
        notifyDataSetChanged();
    }

    public CalculateShareAdapter(ArrayList<DataCalculateShare> shares, Context context, CoordinatorLayout layout) {
        this.shares= shares;
        this.layout= layout;
        this.context= context;
    }

    @Override
    public CalculateShareAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_calculate_share, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.serialNumber.setText("# " + (position+1));
        holder.payer.setText(shares.get(position).payer + "  " + '\u2192' + "  " + shares.get(position).receiver);
        String formattedAmount= String.format("%.2f",shares.get(position).amount);
        holder.amount.setText("Amount: " + '\u20B9' + formattedAmount);
    }

    @Override
    public int getItemCount() {
        return shares.size();
    }
    void showSnackBar(String message){
        if(layout!= null)
            Snackbar.make(layout, message, Snackbar.LENGTH_SHORT).show();
    }
    public void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
