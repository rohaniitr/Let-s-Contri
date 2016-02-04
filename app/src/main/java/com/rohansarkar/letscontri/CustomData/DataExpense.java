package com.rohansarkar.letscontri.CustomData;

/**
 * Created by rohan on 10/12/15.
 */
public class DataExpense {

    public long rowId;
    public double amount;
    public String name, title, binary;

    public void equals(DataExpense dataExpense){
        this.rowId= dataExpense.rowId;
        this.amount= dataExpense.amount;
        this.name= dataExpense.name;
        this.title= dataExpense.title;
        this.binary= dataExpense.binary;
    }

    public DataExpense(){
        this.amount= 0.0;
        this.name= "";
        this.binary= "";
    }

    public DataExpense(String name, double amount, String binary){
        this.amount= amount;
        this.name= name;
        this.binary= binary;
    }

    public DataExpense(String title, String name, double amount, String binary){
        this.amount= amount;
        this.name= name;
        this.title= title;
        this.binary= binary;
    }

    public DataExpense(long rowId, String title, String name, double amount, String binary){
        this.rowId= rowId;
        this.amount= amount;
        this.name= name;
        this.title= title;
        this.binary= binary;
    }

}
