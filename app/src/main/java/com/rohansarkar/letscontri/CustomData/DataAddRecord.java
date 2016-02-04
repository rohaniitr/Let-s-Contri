package com.rohansarkar.letscontri.CustomData;

/**
 * Created by rohan on 11/12/15.
 */
public class DataAddRecord {
    public boolean selected;
    public String name;

    public DataAddRecord(String name){
        this.name= name;
        this.selected= false;
    }

    public DataAddRecord(String name, boolean selected){
        this.name= name;
        this.selected= selected;
    }

}
