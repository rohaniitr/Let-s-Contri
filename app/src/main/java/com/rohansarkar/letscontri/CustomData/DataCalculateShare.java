package com.rohansarkar.letscontri.CustomData;

/**
 * Created by rohan on 10/12/15.
 */
public class DataCalculateShare {
    public double amount;
    public String payer, receiver;

    public DataCalculateShare(String payer, String receiver){
        this.payer= payer;
        this.receiver= receiver;
    }

    public DataCalculateShare(String payer, String receiver, double amount){
        this.payer= payer;
        this.receiver= receiver;
        this.amount= amount;
    }
}
