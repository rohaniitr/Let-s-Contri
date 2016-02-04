package com.rohansarkar.letscontri.CustomData;

import java.util.Comparator;

/**
 * Created by rohan on 13/12/15.
 */
public class DataIndividualShare {
    public String name;
    public double amount;

    public DataIndividualShare(String name){
        this.name= name;
        this.amount= 0.0;
    }

    public static final Comparator<DataIndividualShare> SORT_BY_SHARE = new Comparator<DataIndividualShare>() {
        public int compare(DataIndividualShare activity1, DataIndividualShare activity2) {
            return (int)(activity2.amount - activity1.amount);
        }
    };
}
