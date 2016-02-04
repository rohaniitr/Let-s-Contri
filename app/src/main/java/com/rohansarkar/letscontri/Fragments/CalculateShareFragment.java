package com.rohansarkar.letscontri.Fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.RelativeLayout;

import com.rohansarkar.letscontri.Adapters.CalculateShareAdapter;
import com.rohansarkar.letscontri.CustomData.DataAddRecord;
import com.rohansarkar.letscontri.CustomData.DataCalculateShare;
import com.rohansarkar.letscontri.CustomData.DataExpense;
import com.rohansarkar.letscontri.CustomData.DataIndividualShare;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManager;
import com.rohansarkar.letscontri.DatabaseManagers.DatabaseManagerFriendsName;
import com.rohansarkar.letscontri.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rohan on 10/12/15.
 */
public class CalculateShareFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    CoordinatorLayout layout;
    RelativeLayout emptyLayout, permanentLayout;

    ArrayList<DataCalculateShare> calculatedExchange;
    String LOG_TAG= "CalculateShareFragments Logs";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_calculate_share, container, false);
        init(view);
        calculateShares();
        manageVisibility();
        return view;
    }

    /*
    **Logic Implementation.
    * */

    private void calculateShares(){
        ArrayList<DataExpense> history= getExpenses();
        ArrayList<String> names= getNames();
        ArrayList<DataIndividualShare> shares= new ArrayList<>();
        for(int i=0; i<names.size(); i++)
            shares.add(new DataIndividualShare(names.get(i)));

        for(int i=0; i<history.size(); i++){
            String binary= history.get(i).binary;
            int totalShares=0;

            for(int j=0; j<binary.length(); j++){
                if(binary.charAt(j)=='1') totalShares++;
            }
            if(totalShares<=0)
                break;

            double individualShare= history.get(i).amount/totalShares;
            shares.get(names.indexOf(history.get(i).name)).amount += history.get(i).amount;

            for(int j=0; j<binary.length(); j++){
                if(binary.charAt(j)=='1')
                    shares.get(j).amount -= individualShare;
            }

        }

        Collections.sort(shares, DataIndividualShare.SORT_BY_SHARE);

        int front=0;
        int rear= shares.size()-1;
        while(shares.get(front).amount>=1.0 && shares.get(rear).amount<=-1.0){
            double pos= shares.get(front).amount;
            double neg= -1*shares.get(rear).amount;
            DataCalculateShare transaction= new DataCalculateShare(shares.get(rear).name, shares.get(front).name);

            if(Math.abs(pos-neg)<1.0){
                transaction.amount= pos;
                front++;
                rear--;
            }
            else if(pos>neg){
                transaction.amount= neg;
                shares.get(front).amount -= neg;
                rear--;
            }
            else{
                transaction.amount= pos;
                shares.get(rear).amount += pos;
                front++;
            }

            calculatedExchange.add(transaction);
        }
        adapter.notifyDataSetChanged();
    }

    private void manageVisibility(){
        if(calculatedExchange.size()<=0){
            permanentLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    /*
    **Database Interface.
    * */

    private ArrayList<DataExpense> getExpenses(){
        DatabaseManager manager= new DatabaseManager(getActivity());
        manager.open();
        ArrayList<DataExpense> history= manager.getExpenses();
        manager.close();
        return history;
    }

    private ArrayList<String> getNames(){
        ArrayList<String> names= new ArrayList<>();;
        DatabaseManagerFriendsName manager= new DatabaseManagerFriendsName(getActivity());
        manager.open();
        //It may be a problem, as have not initialised the list
        if(!manager.isEmpty())
            names= manager.getData();
        else
            Log.d(LOG_TAG, "Friend_Name table is empty");
        manager.close();
        return names;
    }

    /*
    **Initializations.
    * */

    private void init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.rvCalculateShare);
        layout= (CoordinatorLayout) view.findViewById(R.id.clCalculateShare);
        permanentLayout= (RelativeLayout) view.findViewById(R.id.rlCalculateSharePermanent);
        emptyLayout= (RelativeLayout) view.findViewById(R.id.rlCalculateShareEmpty);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        calculatedExchange= new ArrayList<>();

        calculatedExchange= new ArrayList<>();

        adapter = new CalculateShareAdapter(calculatedExchange, getActivity(), layout);
        recyclerView.setAdapter(adapter);
    }

    /*
    **Display Assets..
    * */

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

}
